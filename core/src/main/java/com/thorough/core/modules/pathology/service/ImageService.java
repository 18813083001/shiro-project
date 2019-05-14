package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.exception.CoreException;
import com.thorough.core.modules.pathology.model.dao.ImageDao;
import com.thorough.core.modules.pathology.model.entity.*;
import com.thorough.core.modules.sys.service.CoreDiseaseService;
import com.thorough.library.constant.Constant;
import com.thorough.library.mybatis.persistence.Page;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.shiro.session.RedisSessionDAO;
import com.thorough.library.specification.service.CommonService;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.service.UserUserService;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.ResponseBuilder;
import com.thorough.library.utils.StringUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.Int;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;


@Service
@Transactional(readOnly = false)
public class ImageService extends CommonService<String,ImageDao,Image> {

    @Autowired
    LabelService labelService;
    @Autowired
    ImageUserService imageUserService;
    @Autowired
    CoreDiseaseService diseaseService;
    @Autowired
    ImageCustomService imageCustomService;
    @Autowired
    UserUserService userUserService;
    @Autowired
    ImageReviewPoolService imageReviewPoolService;
    @Autowired
    SubmitUserUserService submitUserUserService;
    @Autowired
    ImageUserOverdueService imageUserOverdueService;
    @Autowired
    ImageUserCommentService imageUserCommentService;
    @Autowired
    ImageService imageService;
    RedisSessionDAO jedisSessionDAO;

    @Transactional(readOnly = false)
    public int insert(Image image){
        return super.insert(image);
    }

    @Transactional(readOnly = false)
    public int insertatch(List<Image> imageList){
        int rows = 0;
        for (Image image:imageList){
            image.setNewRecord(true);
            int row = super.insert(image);
            rows +=row;
        }
        return rows;
    }

    @Transactional(readOnly = false)
    public int deleteByPrimaryKey(String imageId){
        Image image = new Image();
        image.setId(imageId);
        image.setDelFlag("1");
        return super.updateByPrimaryKeySelective(image);
    }

    @Transactional(readOnly = false)
    public void deleteByPrimaryKeyList(List<String> imageIdList){
        for (String imageId:imageIdList){
            Image image = new Image();
            image.setId(imageId);
            image.setDelFlag("1");
            super.updateByPrimaryKeySelective(image);
        }

    }

    @Transactional(readOnly = false)
    public int updateByPrimaryKey(Image image){
        return super.updateByPrimaryKey(image);
    }


    @Transactional(readOnly = false)
    public Map updateDifficult(String imageId,String diseaseRegionId,String describes){
        User user = UserUtils.getUser();
        Map map = new HashMap();
        if (user.isDoctor()){
            Image image = new Image();
            image.setId(imageId);
            image.setReviewStage(11);
            image.setLabelStatus(0);
            int imageUpdate = this.updateByPrimaryKeySelective(image);
            map.put("imageUpdate",imageUpdate);
            //医生提交疑难改变所属权,并将片子提交给主任
            updateOwnShip(imageId,map,5,describes);
        }
        else if (user.isDirector()){
            Image image = new Image();
            image.setId(imageId);
            image.setDifficult(1);
            //疑难图片不给任何人看
            image.setDescribes(describes);
            image.setDelFlag("1");
            int updateImageDifficult = this.updateByPrimaryKeySelective(image);
            map.put("updateImageDifficult",updateImageDifficult);
            //主任提交疑难改变所属权，包括改变所属医生的所属权
            updateOwnShip(imageId,map,6,describes);
        }
        //分配切片（一审或者二审的切片）
        allocationImage(UserUtils.getUser().getId(), UserUtils.getUser().getCompany().getId(),diseaseRegionId,map);
        updateImageCount(imageId);
        return map;
    }

    @Transactional(readOnly = false)
    public Map updateFavorites(String imageId,int favorites,String describes){
        User user = UserUtils.getUser();
        Map map = new HashMap();
        ImageUser imageUser = new ImageUser();
        imageUser.setUserId(user.getId());
        imageUser.setImageId(imageId);
        imageUser.setFavorites(favorites);
        imageUser.setFavoritesDescribes(describes);
        int updateFavorites = imageUserService.updateByImageIdUserIdSelective(imageUser,true);
        map.put("updateFavorites",updateFavorites);
        return map;
    }

    /*
    * 每次提交，labelStatus置0，在updateLabelNumber接口中，labelStatus字段被置1
    * */
    @Transactional(readOnly = false)
    public Map updateReviewStatus(String imageId, String diseaseRegionId,Integer grade){
        if (StringUtils.isBlank(diseaseRegionId) || StringUtils.isBlank(imageId))
            throw new CoreException("diseaseRegionId is null");
        Image image = new Image();
        image.setId(imageId);
        int status = 1;
        Map<String,Object> map = new HashMap<>();
        //更新审核状态
        //如果是医生提交或者主任提交，要判断次数，达到次数上限，改变review状态，否则将图片放到缓存池，并增加记录次数，
        if (UserUtils.getUser().isDoctor()|| UserUtils.getUser().isDirector()){
            Image image2 = this.selectByPrimaryKey(imageId);
            //默认切片
            int reviewStag = image2.getReviewStage();
            //一审提交,标识为二审
            if (reviewStag == 10){
                //更新审核阶段
                image.setReviewStage(11);
                image.setLabelStatus(0);
                int updateReviewStage = this.updateByPrimaryKeySelective(image);
                map.put("updateReviewStage",updateReviewStage);
                CommonExample example = new CommonExample(SubmitUserUser.class);
                example.createCriteria().andEqualTo(SubmitUserUser.getFieldSubmitUserId(),UserUtils.getUser().getId()).andEqualTo(SubmitUserUser.getFieldDelFlag(),"0");
                List<SubmitUserUser> submitUserList = submitUserUserService.selectByExample(example);
                //不添加到复审池，以二审状态分配给指定的人
                if (submitUserList !=null && submitUserList.size() > 0){
                    assignment(submitUserList.get(0),imageId,map);
                }else {
                    //添加到复审池
                    addToReviewPool(image2,imageId,map);
                }
                //当前用户不在拥有该图片的所属权
                updateOwnShip(imageId,map,1,null);
            }else {
                //二审提交 需要增加一个判断，且当前用户是二审正在标注，防止前端重复一审提交时，因为网络不好，本来已经提交成功，当时当前页面没有及时刷新，又重复提交，导致切片直接被提交到专家
                String userId = UserUtils.getUser().getId();
                long count = imageUserService.getCountByImageIdAndUserIdAndReviewStage(imageId,userId,11);
                //只有用户有二审切片才能做二审提交
                if (count > 0){
                    if (reviewStag == 11) {
                        //更新切片表
                        image.setLabelStatus(0);
                        image.setReviewStage(20);
                        image.setSubmitDate(new Date());
                        int imageRow = this.updateByPrimaryKeySelective(image);
                        map.put("updateImageReviewStatusRow",imageRow);
                        //更新label表
                        int doctorLabelWows = labelService.updateDoctorReviewStatus(imageId,status);
                        int directorLabelWows = labelService.updateDirectorReviewStatus(imageId,status);
                        map.put("directorLabelRows",directorLabelWows);
                        map.put("doctorLabelRows",doctorLabelWows);
                        //医生和主任没有所属权，一周后不能再看到切片
                        updateOwnShip(imageId,map,2,null);
                        //评分
                        if (grade != null)
                            updateGrade(imageId,grade,null,map);
                    }else {
                        throw new CoreException("reviewStag="+reviewStag+"，未知状态");
                    }
                }
            }
            //分配切片（一审或者二审的切片）
            allocationImage(UserUtils.getUser().getId(), UserUtils.getUser().getCompany().getId(),diseaseRegionId,map);
        }
        //如果是专家提交
        else if(UserUtils.getUser().isExpert()){
            image.setLabelStatus(0);
            image.setReviewStage(30);
            int expertLabelWows = labelService.updateExpertReviewStatus(imageId,status);
            map.put("expertLabelRows",expertLabelWows);

            //更新切片的审核状态
            image.setSubmitDate(new Date());
            int imageRow = this.updateByPrimaryKeySelective(image);
            map.put("updateImageReviewStatusRow",imageRow);
            //没有所属权也不能看到切片
            updateOwnShip(imageId,map,3,null);
        }
        //如果是顾问提交
        else if(UserUtils.getUser().isAdviser()){
            image.setLabelStatus(0);
            image.setReviewStage(40);
            int adviserLabelWows = labelService.updateAdviserReviewStatus(imageId,status);
            map.put("adviserLabelRows",adviserLabelWows);

            //更新切片的审核状态
            image.setSubmitDate(new Date());
            int imageRow = this.updateByPrimaryKeySelective(image);
            map.put("updateImageReviewStatusRow",imageRow);
            //没有所属权也不能看到切片
            updateOwnShip(imageId,map,4,null);
        }
        //如果是管理员
        else if(UserUtils.getUser().isAdmin()){
        }
        updateImageCount(imageId);
        return map;
    }

    public void updateImageCount(String imageId){
        List<String> userIdList = imageUserService.getUserIdByImageId(imageId);
        if (userIdList != null && userIdList.size() > 0){
            for (String userId:userIdList){
                Collection<Session> sessionCollection = jedisSessionDAO.getActiveSessions();
                if (sessionCollection != null){
                    for (Session session:sessionCollection){
                        String principalId = (String) session.getAttribute("principalId");
                        if (userId.equals(principalId)){
                            Session sessionRedis = jedisSessionDAO.readSession(session.getId());
                            sessionRedis.removeAttribute(Constant.CACHE_ORGAN);
                            jedisSessionDAO.update(sessionRedis);
                        }
                    }
                }
            }
        }
    }

    @Transactional(readOnly = false)
    private void updateGrade(String imageId,int grade,String comment,Map map){
        String userId = UserUtils.getUser().getId();
        ImageUserComment imageUserComment = new ImageUserComment();
        imageUserComment.setImageId(imageId);
        imageUserComment.setUserId(userId);
        imageUserComment.setGrade(grade);
        imageUserComment.setComment(comment);
        int insertImageUserComment = imageUserCommentService.insert(imageUserComment);
        map.put("insertImageUserComment",insertImageUserComment);
    }

    private void addToReviewPool(Image image2, String imageId, Map map){
        //添加到复审池
        ImageReviewPool reviewImage = new ImageReviewPool();
        reviewImage.setImageId(imageId);
        reviewImage.setHospitalId(image2.getHospitalId());
        reviewImage.setDepartmentId(image2.getDepartmentId());
        reviewImage.setDiseaseId(image2.getDiseaseId());
        reviewImage.setReviewStage(11);
        int insertImageReviewPool;
        try {
            insertImageReviewPool = imageReviewPoolService.insert(reviewImage);
        }catch (Exception e){
            e.printStackTrace();
            throw new CoreException(e.getMessage());
        }
        map.put("insertImageReviewPool",insertImageReviewPool);
    }

    @Transactional(readOnly = false)
    private void updateOwnShip(String imageId,Map map,int type,String desc){
        //一审提交
        if (type == 1){
            //更新所属权状态（医生和主任）
            //所属权不再属于当前用户但是可以看到切片
            ImageUser imageUser = new ImageUser();
            imageUser.setUserId(UserUtils.getUser().getId());
            imageUser.setImageId(imageId);
            imageUser.setOwnership((byte)0);
            int ownership = imageUserService.updateByImageIdUserIdSelective(imageUser,true);
            map.put("updateOwnership",ownership);
        }else if (type == 2){
            //医生主任二审提交改变所属权
            //所属权不在属于医生和主任，且主任和医生一段时间后不能看到切片
            List<String> userList = imageUserService.getUserIdByImageId(imageId);
            int ownershipNum = 0;
            int insertImageOverdue = 0;
            if (userList != null && userList.size() > 0){
                for (String userId:userList){
                    User user = UserUtils.get(userId);
                    if (user.isDoctor() || user.isDirector()){
                        ImageUser imageUser = new ImageUser();
                        imageUser.setUserId(user.getId());
                        imageUser.setImageId(imageId);
                        imageUser.setOwnership((byte)0);
                        int ownership = imageUserService.updateByImageIdUserIdSelective(imageUser,true);
                        ownershipNum += ownership;

                        //添加到过期池，一段时间后设置主任和医生都不能看到切片
                        ImageUserOverdue overdue = new ImageUserOverdue();
                        overdue.setImageId(imageId);
                        overdue.setUserId(user.getId());
                        int row = imageUserOverdueService.insert(overdue);
                        insertImageOverdue += row;
                    }
                }
            }
            map.put("updateOwnership",ownershipNum);
            map.put("insertImageOverdue",insertImageOverdue);
        }else if (type == 3 || type==4 ){
            //专家和顾问提交改变所属权
            //所属权不再属于当前用户且不能看到切片
            User user = UserUtils.getUser();
            ImageUser imageUser = new ImageUser();
            imageUser.setUserId(user.getId());
            imageUser.setImageId(imageId);
            imageUser.setOwnership((byte)0);
            imageUser.setDelFlag("1");
            int ownership = imageUserService.updateByImageIdUserIdSelective(imageUser,true);
            map.put("updateOwnership",ownership);
        }else if (type == 5){
            //医生疑难提交改变所属权
            User user = UserUtils.getUser();
            ImageUser imageUser = new ImageUser();
            imageUser.setUserId(user.getId());
            imageUser.setImageId(imageId);
            imageUser.setDifficult(1);
            imageUser.setDifficultDescribes(desc);
            imageUser.setOwnership((byte)0);
            int ownership = imageUserService.updateByImageIdUserIdSelective(imageUser,true);
            map.put("updateOwnership",ownership);
            //将疑难片子提交给主任
            List<String> upperIdList = userUserService.getUpperIdList(user.getId());
            if (upperIdList !=null && upperIdList.size() > 0){
                for (String upperId:upperIdList){
                    user = UserUtils.get(upperId);
                    if (user.isDirector()){
                        imageUser = new ImageUser();
                        imageUser.setUserId(upperId);
                        imageUser.setImageId(imageId);
                        imageUser.setDifficult(1);
                        imageUser.setOwnership((byte) 1);
                        imageUser.setDifficultDescribes(desc);
                        imageUser.setReviewStage(11);
                        int insertImageUser = imageUserService.insertSelective(imageUser);
                        map.put("insertImageUser",insertImageUser);
                    }
                }
            }
        }else if (type == 6){
            //主任疑难提交改变所属权
            int ownershipNum = 0;
            User user = UserUtils.getUser();
            ImageUser imageUser = new ImageUser();
            imageUser.setUserId(user.getId());
            imageUser.setImageId(imageId);
            imageUser.setDifficult(1);
            imageUser.setDifficultDescribes(desc);
            imageUser.setOwnership((byte)0);
            imageUser.setDelFlag("1");
            int ownership = imageUserService.updateByImageIdUserIdSelective(imageUser,true);
            ownershipNum += ownership;
            //其他医生的删除状态置1
            List<String> userList = imageUserService.getUserIdByImageId(imageId);
            if (userList != null && userList.size() > 0){
                userList.remove(user.getId());
                for (String userId:userList){
                    user = UserUtils.get(userId);
                    if (user.isDoctor() || user.isDirector()){
                        imageUser = new ImageUser();
                        imageUser.setUserId(user.getId());
                        imageUser.setImageId(imageId);
                        imageUser.setOwnership((byte)0);
                        imageUser.setDelFlag("1");
                        ownership = imageUserService.updateByImageIdUserIdSelective(imageUser,true);
                        ownershipNum += ownership;
                    }
                }
            }
            map.put("updateOwnership",ownershipNum);
        }

    }

    /*
    * 智能分配，给用户分配一张新切片或者复审切片
    * */
    @Transactional(readOnly = false)
    private void allocationImage(String userId,String hospitalId,String diseaseRegionId,Map map){
        List<String> diseaseIdList = diseaseService.getAllChildIdsByParentIdAndUserId(diseaseRegionId,userId,"disease");
        if (diseaseIdList == null || diseaseIdList.size() == 0)
            return;
        boolean allocationNewIamge = true;
        if (Math.random()>=0.5){
            allocationNewIamge  = false;
        }
        Set<String> imageIdSet = new HashSet();
        //看是否还有未分配的切片
        if (allocationNewIamge){
            //新切片数量
            int newNumber = 0;
            for (String diseaseId:diseaseIdList){
                CommonExample example = new CommonExample(Image.class);
                example.createCriteria().
                        andEqualTo(Image.getFieldHospitalId(),hospitalId).
                        andEqualTo(Image.getFieldDiseaseId(),diseaseId).
                        andEqualTo(Image.getFieldDelFlag(),"0").
                        andEqualTo(Image.getFieldAllocation(),"0");
                long count = this.countByExample(example);
                newNumber += count;
            }
            //如果没有新切片，分配复审切片
            if (newNumber==0)
                allocationNewIamge = false;
        }
        else{
            //复审切片数量
            int reviewNumber = 0;
            //判断池子中是否有切片(除自己以外的切片)
            for (String diseaseId:diseaseIdList){
                CommonExample example = new CommonExample(ImageReviewPool.class);
                example.createCriteria().
                        andEqualTo(ImageReviewPool.getFieldHospitalId(),hospitalId).
                        andEqualTo(ImageReviewPool.getFieldDiseaseId(),diseaseId).
                        andEqualTo(ImageReviewPool.getFieldDelFlag(),"0").
                        andNotEqualTo(ImageReviewPool.getFieldCreateBy(),userId);
                long count = imageReviewPoolService.countByExample(example);
                reviewNumber += count;
            }
            //如果池子中没有切片，分配新切片
            if (reviewNumber==0)
                allocationNewIamge = true;

        }

        //判断是否有新切片
        if (allocationNewIamge){
            //获取尚未分配的切片
            for (String diseaseId:diseaseIdList){
                List<String> list = imageCustomService.selectImageIdListByHospitalIdAndDiseaseId(hospitalId,diseaseId);
                if (list != null && list.size() > 0)
                    imageIdSet.addAll(list);
            }
        }else {
            //获取一审切片
            for (String diseaseId:diseaseIdList){
                List<String> list = imageCustomService.selectImageIdListByHospitalIdAndDiseaseIdFromPool(hospitalId,diseaseId,11,null);
                if (list != null && list.size() > 0)
                    imageIdSet.addAll(list);
            }
            //去除自己的一审切片
            Set<String> repeatImageIdSelfSet = new HashSet();
            for (String diseaseId:diseaseIdList){
                List<String> list = imageCustomService.selectImageIdListByHospitalIdAndDiseaseIdFromPool(hospitalId,diseaseId,11,userId);
                if (list != null && list.size() > 0){
                    Iterator<String> iterator = list.iterator();
                    while (iterator.hasNext()){
                        String imageId = iterator.next();
                        if (imageIdSet.contains(imageId))
                            repeatImageIdSelfSet.add(imageId);
                    }
                }
            }
            if (repeatImageIdSelfSet.size() > 0)
                imageIdSet.removeAll(repeatImageIdSelfSet);

        }

        //随机分配一张
        if (imageIdSet.size() > 0) {
            int rand = new Random().nextInt(imageIdSet.size());
            String imageId = (String) imageIdSet.toArray()[rand];
            ImageUser imageUser = new ImageUser();
            imageUser.setUserId(userId);
            imageUser.setImageId(imageId);
            imageUser.setOwnership((byte) 1);
            if (allocationNewIamge)
                imageUser.setReviewStage(10);
            else
                imageUser.setReviewStage(11);
            int insertImageUser = imageUserService.insertSelective(imageUser);
            map.put("insertImageUser",insertImageUser);
            if (allocationNewIamge) {
                //标识图片已经被分配
                Image image = new Image();
                image.setId(imageId);
                image.setAllocation(1);
                int updateAllocation = this.updateByPrimaryKeySelective(image);
                map.put("updateAllocation",updateAllocation);
            }else {
                //从复审池中删除数据
                int deleteImageReviewPool =imageReviewPoolService.deleteByPrimaryKey(imageId);
                map.put("deleteImageReviewPoolRow",deleteImageReviewPool);
            }
        }
    }

    /*
    * 以二审状态指定分配给某人
    * */
    @Transactional(readOnly = false)
    public void assignment(SubmitUserUser submitUserUser, String imageId, Map map){
        String reviewUserId = submitUserUser.getReviewUserId();
        if (StringUtils.isNotBlank(reviewUserId)){
            ImageUser imageUser = new ImageUser();
            imageUser.setOwnership((byte) 1);
            imageUser.setImageId(imageId);
            imageUser.setUserId(reviewUserId);
            imageUser.setReviewStage(11);
            int insertImageUser = imageUserService.insertSelective(imageUser);
            map.put("insertImageUser",insertImageUser);
        }else {
            throw new CoreException("reviewUserId is empty");
        }
    }

    /*
    * 手动分配复审池数据
    * */
    public Map reviewAssigment(String diseaseId){
        Map map = new HashMap();
        List<String> upperIds = userUserService.getAllUpperIdList();
        if (upperIds !=null && upperIds.size() > 0){
            for(String upperId:upperIds){
                List<String> userIds = userUserService.getBelongsIdList(upperId);
                User user  = UserUtils.get(upperId);
                Map mapChild = new HashMap();
                if (userIds != null && userIds.size()> 0){
                    Queue<String> queue = new LinkedBlockingDeque<>();
                    queue.addAll(userIds);
                    String first = queue.poll();
                    String temp = first;
                    while (!queue.isEmpty()){
                        //统计用户1的一审片子
                        String userId1 = first;
                        User user1 = UserUtils.get(userId1);
                        String hospitalId1 = user1.getCompany().getId();
                        CommonExample example1 = new CommonExample(ImageReviewPool.class);
                        if (diseaseId == null)
                            example1.createCriteria().
                                    andEqualTo(ImageReviewPool.getFieldDelFlag(),"0").
                                    andEqualTo(ImageReviewPool.getFieldHospitalId(),hospitalId1).
                                    andEqualTo(ImageReviewPool.getFieldCreateBy(),userId1);
                        else
                            example1.createCriteria().
                                    andEqualTo(ImageReviewPool.getFieldDelFlag(),"0").
                                    andEqualTo(ImageReviewPool.getFieldHospitalId(),hospitalId1).
                                    andEqualTo(ImageReviewPool.getFieldCreateBy(),userId1).
                                    andEqualTo(ImageReviewPool.getFieldDiseaseId(),diseaseId);
                        List<ImageReviewPool> list1 = imageReviewPoolService.selectByExample(example1);
                        if ( list1!=null && list1.size() > 0){
                            //将片子分配给用户2
                            String userId2 = queue.poll();
                            first = userId2;
                            User user2 = UserUtils.get(userId2);
                            mapChild.put(user2.getName(),list1.size());
                            //将片子分配给用户2
                            for (ImageReviewPool imageReviewPool:list1){
                                String imageId = imageReviewPool.getImageId();
                                ImageUser imageUser = new ImageUser();
                                imageUser.setImageId(imageId);
                                imageUser.setUserId(userId2);
                                imageUser.setOwnership((byte) 1);
                                imageUser.setReviewStage(11);
                                imageUserService.insertSelective(imageUser);
                                imageReviewPoolService.deleteByPrimaryKey(imageReviewPool.getImageId());
                            }
                        }else {
                            String userId2 = queue.poll();
                            first = userId2;
                            User user2 = UserUtils.get(userId2);
                            mapChild.put(user2.getName(),list1.size());
                        }

                    }

                    if (!first.equals(temp)){
                        //将最后一个人的片子分配给第一个人
                        String userId1 = first;
                        User user1 = UserUtils.get(userId1);
                        String hospitalId1 = user1.getCompany().getId();
                        CommonExample example1 = new CommonExample(ImageReviewPool.class);
                        example1.createCriteria().
                                andEqualTo(ImageReviewPool.getFieldDelFlag(),"0").
                                andEqualTo(ImageReviewPool.getFieldHospitalId(),hospitalId1).
                                andEqualTo(ImageReviewPool.getFieldCreateBy(),userId1);
                        List<ImageReviewPool> list1 = imageReviewPoolService.selectByExample(example1);
                        if ( list1!=null && list1.size() > 0){
                            //将片子分配给第一个人
                            String userId2 = temp;
                            User user2 = UserUtils.get(userId2);
                            mapChild.put(user2.getName(),list1.size());
                            for (ImageReviewPool imageReviewPool:list1){
                                String imageId = imageReviewPool.getImageId();
                                ImageUser imageUser = new ImageUser();
                                imageUser.setImageId(imageId);
                                imageUser.setUserId(userId2);
                                imageUser.setOwnership((byte) 1);
                                imageUser.setReviewStage(11);
                                imageUserService.insertSelective(imageUser);
                                imageReviewPoolService.deleteByPrimaryKey(imageReviewPool.getImageId());
                            }
                        }else {
                            User user2 = UserUtils.get(temp);
                            mapChild.put(user2.getName(),list1.size());
                        }
                    }else {
                        User user2 = UserUtils.get(temp);
                        mapChild.put(user2.getName(),0);
                    }
                }
                map.put(user.getCompany().getName(),mapChild);
            }
        }
        return map;
    }

    public Page<Image> typical(Page<Image> page, String diseaseId){
        if (StringUtils.isBlank(diseaseId))
            diseaseId = "0";
        List<String> diseaseIdList = diseaseService.getAllChildIdsByParentIdAndUserId(diseaseId, UserUtils.getUser().getId(),"disease");
        if (diseaseIdList != null && diseaseIdList.size() > 0){
            CommonExample example = new CommonExample(Image.class);
            example.createCriteria().
                    andEqualTo(Image.getFiedTypical(),"1").
                    andEqualTo(Image.getFieldDelFlag(),"0").
                    andIn(Image.getFieldDiseaseId(),diseaseIdList);
            if (StringUtils.isNotBlank(page.getOrderBy())){
                example.setOrderByClause(page.getOrderBy());
            }
            example.setPage(page);
            List<Image> list = this.selectByExample(example);
            page.setList(list);
        }
        return page;
    }



    @Transactional(readOnly = false)
    public void updateProgress(double process,String jobId,String imageId){
        BigDecimal bg = new BigDecimal(process);
        process = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        Image image = new Image();
        image.setId(imageId);
        image.setProgress((int) (process*100));
        image.setJobId(jobId);
        if (process == 1){
            image.setAiPredict(1);
            image.setPredictEndDate(new Date());
        }
        this.updateByPrimaryKeySelective(image);
    }

    @Transactional(readOnly = false)
    public void switchAllocation(int allocation,List<String> imageIdList){
        for (String imageId:imageIdList){
            Image image = new Image();
            image.setId(imageId);
            image.setAllocation(allocation);
            this.updateByPrimaryKeySelective(image);
        }
    }

    public void checkParameter(String imageId,String userId){
        if (StringUtils.isBlank(imageId))
            throw new CoreException("切片ID为空");
        if (StringUtils.isBlank(userId))
            throw new CoreException("用户ID为空");
    }

    public void deleteImageUser(String imageId,String userId){
        List<String> imageIdList = new ArrayList<>();
        imageIdList.add(imageId);
        //从表中删除关系（磁盘删除）
        imageUserService.deleteImageUser(imageIdList,userId);
    }

    public void updateImage(String imageId, Integer labelStatus, Integer reviewStage, Integer allocation){
        Image image = new Image();
        image.setId(imageId);
        if (labelStatus != null)
            image.setLabelStatus(labelStatus);
        if (reviewStage != null)
            image.setReviewStage(reviewStage);
        if (allocation != null)
            image.setAllocation(allocation);
        imageService.updateByPrimaryKeySelective(image);
    }

    public void addToReviewPool(Image image){
        //添加到复审池
        ImageReviewPool reviewImage = new ImageReviewPool();
        reviewImage.setImageId(image.getId());
        reviewImage.setHospitalId(image.getHospitalId());
        reviewImage.setDepartmentId(image.getDepartmentId());
        reviewImage.setDiseaseId(image.getDiseaseId());
        reviewImage.setReviewStage(11);
        reviewImage.setCreateBy(UserUtils.getUser().getId());
        reviewImage.setCreateDate(new Date());
        reviewImage.setUpdateBy(UserUtils.getUser().getId());
        reviewImage.setUpdateDate(new Date());
        imageReviewPoolService.insert(reviewImage);
    }

    /**
     * 撤回为一审未分配状态/一审撤回
     * */
    @Transactional(readOnly = false)
    public ResponseEntity<?> withdrawToUnAllocation(String imageId, String userId){
        checkParameter(imageId,userId);
        ResponseBuilder builder = ResponseBuilder.newInstance();
        deleteImageUser(imageId,userId);
        //image片子撤回为一审待标注，且未分配状态
        updateImage(imageId,0,10,0);
        builder.message("撤回为一审未分配状态/一审撤回");
        return builder.build();
    }

    /**
     * 撤回为一审已提交状态/二审撤回
     * */
    @Transactional(readOnly = false)
    public ResponseEntity<?> withdrawToSubmit10(String imageId, String userId){
        checkParameter(imageId,userId);
        //判断是否为一审已提交
        long count = imageUserService.getCountByImageIdAndReviewStageAndOwnership(imageId,0,10);
        //是一审已提交
        if (count > 0){
            ResponseBuilder builder = ResponseBuilder.newInstance();
            deleteImageUser(imageId,userId);
            //片子撤回为一审已提交
            updateImage(imageId,0,11,null);
            //二审池添加一条数据
            Image image = imageService.selectByPrimaryKey(imageId);
            addToReviewPool(image);
            builder.message("撤回为一审已提交状态/二审撤回");
            return builder.build();
        }else {
            //不是一审已提交,退回到一审未分配状态
            return withdrawToUnAllocation(imageId,userId);
        }
    }

    /**
     * 撤回为二审已提交状态/专家撤回
     * */
    @Transactional(readOnly = false)
    public ResponseEntity<?> withdrawToSubmit11(String imageId, String userId){
        checkParameter(imageId,userId);
        //判断是否为二审已提交
        long count = imageUserService.getCountByImageIdAndReviewStageAndOwnership(imageId,0,11);
        //是二审已提交
        if (count > 0){
            ResponseBuilder builder = ResponseBuilder.newInstance();
            deleteImageUser(imageId,userId);
            //片子撤回为二审已提交
            updateImage(imageId,0,20,null);
            builder.message("撤回为二审已提交状态/专家撤回");
            return builder.build();
        }else {
            //不是二审已提交,退回到一审已提交状态
            return withdrawToSubmit10(imageId,userId);
        }
    }

    /**
     * 撤回为专家已提交/顾问撤回
     * */
    @Transactional(readOnly = false)
    public ResponseEntity<?> withdrawToSubmit20(String imageId, String userId){
        checkParameter(imageId,userId);
        //判断是否为专家已提交
        long count = imageUserService.getCountByImageIdAndReviewStageAndOwnership(imageId,0,20);
        //是专家已提交
        if (count > 0){
            ResponseBuilder builder = ResponseBuilder.newInstance();
            deleteImageUser(imageId,userId);
            //片子撤回为专家已提交
            updateImage(imageId,0,30,null);
            builder.message("撤回为专家已提交/顾问撤回");
            return builder.build();
        }else {
            //不是专家已提交,退回到二审已提交状态
            return withdrawToSubmit11(imageId,userId);
        }
    }


}
