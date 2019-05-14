package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.exception.CoreException;
import com.thorough.core.modules.pathology.model.dao.ImageUserDao;
import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.model.entity.ImageReviewPool;
import com.thorough.core.modules.pathology.model.entity.ImageUser;
import com.thorough.core.modules.pathology.model.entity.ImageUserOverdue;
import com.thorough.core.modules.pathology.model.vo.AgainAllocationVo;
import com.thorough.core.modules.pathology.model.vo.ImageIdUserIdVo;
import com.thorough.core.modules.pathology.model.vo.ImageUserIdVo;
import com.thorough.core.modules.sys.service.CoreDiseaseService;
import com.thorough.library.constant.Constant;
import com.thorough.library.mybatis.persistence.Page;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.specification.service.BaseService;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class ImageUserService implements BaseService {

    @Autowired
    ImageUserDao imageUserDao;

    @Autowired
    CoreDiseaseService diseaseService;

    @Autowired
    ImageUserOverdueService imageUserOverdueService;

    @Autowired
    ImageService imageService;

    @Autowired
    ImageReviewPoolService imageReviewPoolService;


    @Transactional(readOnly = false)
    public int insertSelective(ImageUser imageUser){
        imageUser.preInsert();
        imageUser.setCreateBy(UserUtils.getUser().getId());
        imageUser.setCreateDate(new Date());
        imageUser.setUpdateDate(new Date());
        imageUser.setUpdateBy(UserUtils.getUser().getId());
        int row = imageUserDao.insertSelective(imageUser);
        return row;
    }

    public long getCountByUser(Image image){
        return imageUserDao.getCountByUser(image);
    }

    /**
     * 管理系统的
     * */
    public Page<Image> getImagesByUserForManage(Image image){
        List<Image> imageList;
        imageList = imageUserDao.getImagesByUser(image);
        image.getPage().setList(imageList);
        return image.getPage();
    }

    /**
     * 前端的
     * */
    @Transactional(readOnly = false)
    public Page<Image> getImagesByUser(Image image) {
        if (image == null)
            return null;
        List<Image> imageList = null;
        image = packageCondition(image);
        if (image == null)
            return null;
        setCondition(image);
        image.setDelFlag(Image.DEL_FLAG_NORMAL);
        Map paramMap = new HashMap();
        paramMap.put("image",image);
        imageList = imageUserDao.getImagesByUser(image);
        image.getPage().setList(imageList);
        return image.getPage();
    }

    private void setCondition(Image image){
        User user = UserUtils.getUser();
        //管理员，忽略删除标记的数据
        if (user.isAdmin()){
            //一审已提交或者二审已提交
            if (image.getOwnership() != null && image.getOwnership() == 0){
                if (image.getLabelStatus() == null && image.getReviewStage() == null){
                    if (image.getInitialReviewStage()!=null && image.getInitialReviewStage() == 10){
                        //一审已提交,忽略删除标记的数据
                        image.setImageUserDelFlag(null);
                    }else if(image.getInitialReviewStage()!=null && image.getInitialReviewStage() == 11){
                        //二审已提交,忽略删除标记的数据
                        image.setImageUserDelFlag(null);
                    }else if (image.getInitialReviewStage()!=null && image.getInitialReviewStage() == 20){
                        //专家已审核,忽略删除标记的数据
                        image.setImageUserDelFlag(null);
                    }
                }
            }
        }

        //查询包含撤回的数据
        if (image.getOwnership() == null && image.getReviewStage() != null && image.getInitialReviewStage() != null && image.getLabelStatus() != null){
            //当二审用户提交疑难给主任时，image表中review_stage=11，这时二审待标注和二审已提交的状态可能会相同，如果不加这个条件，查询二审待标注时，会将二审已提交的数据查出来
            image.setOwnership((byte) 1);
            if (image.getInitialReviewStage() == 10 && image.getReviewStage() == 10 && image.getLabelStatus() == 0){
                //查询一审待标注切包含一审退回
                image.setLabelStatusRollback(2);

            }else if (image.getInitialReviewStage() == 11 && image.getReviewStage() == 11 && image.getLabelStatus() == 0){
                //查询二审待标注且包含二审退回
                image.setLabelStatusRollback(2);
            }
        }
    }

    /*
   * 统计
   * */
    @Transactional(readOnly = false)
    public Map statisticsLabelPage(Image image){
        image = packageCondition(image);
        if (image == null)
            return null;
        Map map = null;
        image.setOwnership(null);
        image.setLabelStatus(null);
        image.setReviewStage(null);
        image.setInitialReviewStage(null);
        User user = UserUtils.getUser();
        if (user.isAdmin()){
            map = getAdminStatistic(image);
        }else if (user.isDirector()|| user.isDoctor()){
            map = getDirectorAndDoctorStatistic(image);
        }else if (user.isExpert()){
            map = getExpertStatistic(image);
        }else if (user.isAdviser()){
            map = getAdviserStatistic(image);
        }
        return map;
    }


    /*
    * 统计
    * */
    @Transactional(readOnly = false)
    public Map statisticsManagePage(Image image){
        image = packageCondition(image);
        if (image == null)
            return null;
        Map map = new HashMap();
        //统计一审已提交或者二审已提交
        if (image.getOwnership() != null && image.getOwnership() == 0){
            if (image.getLabelStatus() == null && image.getReviewStage() == null){
                if (image.getInitialReviewStage()!=null && image.getInitialReviewStage() == 10){
                    //一审已提交
                    long submit10 = submitLabel10(image,Image.DEL_FLAG_NORMAL);
                    map.put("submit10",submit10);
                }else if(image.getInitialReviewStage()!=null && image.getInitialReviewStage() == 11){
                    //二审已提交
                    long submit11 = submitLabel11(image,Image.DEL_FLAG_NORMAL);
                    map.put("submit11",submit11);
                }else if (image.getInitialReviewStage()!=null && image.getInitialReviewStage() == 20){
                    //专家已提交
//                     long submit20 = submitLabel20(image);
//                     map.put("submit20",submit20);
                }
                else {
                    throw new CoreException("参数错误，统计一审提交、二审提交、顾问已审核，initialReviewStage不能为空且必须是10、11、30，labelStatus和reviewStage参数忽略");
                }
            }else {
                throw new CoreException("参数错误，统计一审提交或者二审提交，initialReviewStage不能为空且必须是10或者11，labelStatus和reviewStage参数忽略");
            }

        }else if (image.getOwnership() == null){
            //统计多项
            if (image.getLabelStatus() == null && image.getReviewStage() == null && image.getInitialReviewStage() == null){
                User user = UserUtils.getUser();
                if (user.isAdmin()){
                    map = getAdminStatistic(image);
                }else if (user.isDirector()){
                    map = getDirectorAndDoctorStatistic(image);
                }
            }else {
                //统计单项
                if (image.getReviewStage() != null && image.getInitialReviewStage() != null){
                    if (image.getLabelStatus() != null){
                        if (image.getInitialReviewStage() == 10 && image.getReviewStage() == 10 && image.getLabelStatus() == 0){
                            //一审待标注
                            long unLabel10 = unLabel10(image);
                            map.put("unLabel10",unLabel10);
                        }else if (image.getInitialReviewStage() == 10 && image.getReviewStage() == 10 && image.getLabelStatus() == 1){
                            //一审正在标注
                            long onLabel10 = onLabel10(image);
                            map.put("onLabel10",onLabel10);
                        }else if (image.getInitialReviewStage() == 11 && image.getReviewStage() == 11 && image.getLabelStatus() == 0){
                            //二审待标注
                            long unLabel11 = unLabel11(image);
                            map.put("unLabel11",unLabel11);
                        }else if (image.getInitialReviewStage() == 11 && image.getReviewStage() == 11 && image.getLabelStatus() == 1){
                            //二审正在标注
                            long onLabel11 = onLabel11(image);
                            map.put("onLabel11",onLabel11);
                        }else if (image.getInitialReviewStage() == 20 && image.getReviewStage() == 20 && image.getLabelStatus() == 0){
                            //专家待审核
                            long unLabel20 = unLabel20(image);
                            map.put("unLabel20",unLabel20);
                        }else if (image.getInitialReviewStage() == 20 && image.getReviewStage() == 20 && image.getLabelStatus() == 1){
                            //专家正在审核
                            long onLabel20 = onLabel20(image);
                            map.put("onLabel20",onLabel20);
                        }
                        else if (image.getInitialReviewStage() == 30 && image.getReviewStage() == 30 && image.getLabelStatus() == 0){
                            //顾问待审核
                            long unLabel30 = unLabel30(image);
                            map.put("unLabel30",unLabel30);
                        }else if (image.getInitialReviewStage() == 30 && image.getReviewStage() == 30 && image.getLabelStatus() == 1){
                            //顾问正在审核
                            long onLabel30 = onLabel30(image);
                            map.put("onLabel30",onLabel30);
                        }
                        else {
                            throw new CoreException("参数错误，reviewStage和initialReviewStage必须是10/11/20/30之一，如果reviewStage和initialReviewStage值不一致，目前不支持统计（如initialReviewStage=10，reviewStage20统计一审提交的切片，且处于专家审核的有多少），labelStatus必须是0/1之一");
                        }

                    }
                }
                else {
                    if (image.getReviewStage() == null)
                        throw new CoreException("reviewStage不能为空");
                    else if (image.getInitialReviewStage() == null)
                        throw new CoreException("initialReviewStage不能为空");
                }
            }
        }else throw new CoreException("ownership参数错误,ownership=0表示统计一审已提交、二审已提交，ownership=null为统计其他");
        long unallocated = unallocated(image);
        map.put("unallocated",unallocated);
        return map;

    }

    /*
   * 统计一审、二审分配数、完成数
   * */
    @Transactional(readOnly = false)
    public Map statisticsGraphicsPage(Image image){
        image = packageCondition(image);
        if (image == null)
            throw new CoreException("搜索条件为空");
        Map map = new HashMap();
        if (image.getStatisticsType()!=null && image.getStatisticsType() == 10){
            //统计一审分配数
            long  allocation10 = allocation10(image);
            //统计一审已完成
            long  submitLabel10 = submitLabel10(image,null);
            map.put("allocation10",allocation10);
            map.put("submitLabel10",submitLabel10);
        }else if (image.getStatisticsType()!=null && image.getStatisticsType() == 11){
            //统计二审分配数
            long  allocation11 = allocation11(image);
            //统计一审已完成
            long  submitLabel11 = submitLabel11(image,null);
            map.put("allocation11",allocation11);
            map.put("submitLabel11",submitLabel11);
        }else {
            //统计一审分配数
            long  allocation10 = allocation10(image);
            //统计一审已完成
            long  submitLabel10 = submitLabel10(image,null);
            map.put("allocation10",allocation10);
            map.put("submitLabel10",submitLabel10);
            //统计二审分配数
            long  allocation11 = allocation11(image);
            //统计一审已完成
            long  submitLabel11 = submitLabel11(image,null);
            map.put("allocation11",allocation11);
            map.put("submitLabel11",submitLabel11);

        }
        return map;
    }

    /*
    * 管理员统计切片数量
    * */
    private Map getAdminStatistic(Image image){
        Map map = new HashMap();
        //统计所有的
        Map mapDirector = getDirectorAndDoctorStatistic(image);
        Map mapExpert = getExpertStatistic(image);
        Map mapAdviser = getAdviserStatistic(image);
        map.putAll(mapDirector);
        map.putAll(mapExpert);
        map.putAll(mapAdviser);
        return map;
    }

    /*
    * 主任或者医生统计切片数量
    * */
    private Map getDirectorAndDoctorStatistic(Image image){
        Map map = new HashMap();
        Image imageClone = image.clone();
        long unLabel10 = unLabel10(imageClone);
        imageClone = image.clone();
        long onLabel10 = onLabel10(imageClone);
        imageClone = image.clone();
        long unLabel11 = unLabel11(imageClone);
        imageClone = image.clone();
        long onLabel11 = onLabel11(imageClone);
        imageClone = image.clone();
        long submit10 = submitLabel10(imageClone,Image.DEL_FLAG_NORMAL);
        imageClone = image.clone();
        long submit11 = submitLabel11(imageClone,Image.DEL_FLAG_NORMAL);
        map.put(Constant.LABEL_ONE_UN,unLabel10);
        map.put(Constant.LABEL_ONE_ON,onLabel10);
        map.put(Constant.LABEL_ONE_SUBMIT,submit10);
        map.put(Constant.LABEL_TWO_UN,unLabel11);
        map.put(Constant.LABEL_TWO_ON,onLabel11);
        map.put(Constant.LABEL_TWO_SUBMIT,submit11);
        return map;
    }

    /*
    * 专家计切片数量
    * */
    private Map getExpertStatistic(Image image){
        Map map = new HashMap();
        Image imageClone = image.clone();
        long unLabel20 = unLabel20(imageClone);
        imageClone = image.clone();
        long onLabel20 = onLabel20(imageClone);
//        imageClone = image.clone();
//        long submit20 = submitLabel20(imageClone);
        map.put(Constant.LABEL_EXPERT_UN,unLabel20);
        map.put(Constant.LABEL_EXPERT_ON,onLabel20);
//        map.put("submit20",submit20);
        return map;
    }

    /*
    * 顾问计切片数量
    * */
    private Map getAdviserStatistic(Image image){
        Map map = new HashMap();
        Image imageClone = image.clone();
        long unLabel30 = unLabel30(imageClone);
        imageClone = image.clone();
        long onLabel30 = onLabel30(imageClone);
        imageClone = image.clone();
//        long submit30 = submitLabel30(imageClone);
        map.put(Constant.LABEL_ADVISER_UN,unLabel30);
        map.put(Constant.LABEL_ADVISER_ON,onLabel30);
//        map.put("submit30",submit30);
        return map;
    }

    /*
    * 统计尚未分配的数据
    * */
    private long unallocated(Image image){
        List<String> diseaseIdList = image.getDiseaseIdList();
        if (diseaseIdList == null || diseaseIdList.size() == 0){
            return 0;
        }
        User user = UserUtils.getUser();
        CommonExample imageExample = new CommonExample(Image.class);
        imageExample.createCriteria().andEqualTo(Image.getFieldHospitalId(),user.getCompany().getId()).andEqualTo(Image.getFieldAllocation(),"0").andIn(Image.getFieldDiseaseId(),diseaseIdList).andEqualTo(Image.getFieldDelFlag(),"0");
        long unallocated = imageService.countByExample(imageExample);
        return unallocated;
    }

    /*
    * 统计一审已分配
    * */
    private long allocation10(Image image){
        image.setInitialReviewStage(10);
        image.setReviewStage(null);
        image.setLabelStatus(null);
        image.setOwnership(null);
        image.setImageUserDelFlag(null);
        long unLabel1 = imageUserDao.getCountByUser(image);
        return unLabel1;
    }

    /*
    * 统计一审待标注
    * */
    private long unLabel10(Image image){
        image.setInitialReviewStage(10);
        image.setReviewStage(10);
        image.setLabelStatus(0);
        image.setOwnership((byte) 1);
        image.setImageUserDelFlag(Image.DEL_FLAG_NORMAL);
        long unLabel1 = imageUserDao.getCountByUser(image);
        return unLabel1;
    }

    /*
    * 一审正在标注
    * */
    private long onLabel10(Image image){
        image.setInitialReviewStage(10);
        image.setReviewStage(10);
        image.setLabelStatus(1);
        image.setOwnership((byte) 1);
        image.setImageUserDelFlag(Image.DEL_FLAG_NORMAL);
        long onLabel1 = imageUserDao.getCountByUser(image);
        return onLabel1;
    }

    /*
   * 统计二审已分配
   * */
    private long allocation11(Image image){
        image.setInitialReviewStage(11);
        image.setReviewStage(null);
        image.setLabelStatus(null);
        image.setOwnership(null);
        image.setImageUserDelFlag(null);
        long unLabel1 = imageUserDao.getCountByUser(image);
        return unLabel1;
    }

    /*
    * 二审待标注
    * */
    private long unLabel11(Image image){
        image.setInitialReviewStage(11);
        image.setReviewStage(11);
        image.setLabelStatus(0);
        image.setOwnership((byte) 1);
        image.setImageUserDelFlag(Image.DEL_FLAG_NORMAL);
        long unLabel2 = imageUserDao.getCountByUser(image);
        return unLabel2;
    }

    /*
    * 二审正在标注
    * */
    private long onLabel11(Image image){
        image.setInitialReviewStage(11);
        image.setReviewStage(11);
        image.setLabelStatus(1);
        image.setOwnership((byte) 1);
        image.setImageUserDelFlag(Image.DEL_FLAG_NORMAL);
        long onLabel2 = imageUserDao.getCountByUser(image);
        return onLabel2;
    }

    /*
    * 一审已提交
    * */
    private long submitLabel10(Image image,String delFlag){
        image.setInitialReviewStage(10);
        image.setOwnership((byte) 0);
        image.setLabelStatus(null);
        image.setReviewStage(null);
        image.setImageUserDelFlag(delFlag);
        long submitl10 = imageUserDao.getCountByUser(image);
        return submitl10;
    }

    /*
    * 二审已提交
    * */
    private long submitLabel11(Image image,String delFlag){
        image.setInitialReviewStage(11);
        image.setOwnership((byte) 0);
        image.setReviewStage(null);
        image.setLabelStatus(null);
        image.setImageUserDelFlag(delFlag);
        long submitl11 = imageUserDao.getCountByUser(image);
        return submitl11;
    }

    /*
    * 专家待审核
    * */
    private long unLabel20(Image image){
        image.setInitialReviewStage(20);
        image.setReviewStage(20);
        image.setLabelStatus(0);
        image.setOwnership((byte) 1);
        image.setImageUserDelFlag(Image.DEL_FLAG_NORMAL);
        long unLabel20 = imageUserDao.getCountByUser(image);
        return unLabel20;
    }

    /*
    * 专家正在审核
    * */
    private long onLabel20(Image image){
        image.setInitialReviewStage(20);
        image.setReviewStage(20);
        image.setLabelStatus(1);
        image.setOwnership((byte) 1);
        image.setImageUserDelFlag(Image.DEL_FLAG_NORMAL);
        long onLabel20 = imageUserDao.getCountByUser(image);
        return onLabel20;
    }

//    /*
//    * 专家已提交
//    * */
//    private long submitLabel20(Image image){
//        image.setInitialReviewStage(20);
//        image.setOwnership((byte) 0);
//        image.setReviewStage(null);
//        image.setLabelStatus(null);
//        image.setImageUserDelFlag(null);
//        long submit20 = imageUserDao.getCountByUser(image);
//        return submit20;
//    }


    /*
   * 顾问待审核
   * */
    private long unLabel30(Image image){
        image.setInitialReviewStage(30);
        image.setReviewStage(30);
        image.setLabelStatus(0);
        image.setOwnership((byte) 1);
        image.setImageUserDelFlag(Image.DEL_FLAG_NORMAL);
        long unLabel30 = imageUserDao.getCountByUser(image);
        return unLabel30;
    }

    /*
    * 顾问正在审核
    * */
    private long onLabel30(Image image){
        image.setInitialReviewStage(30);
        image.setReviewStage(30);
        image.setLabelStatus(1);
        image.setOwnership((byte) 1);
        image.setImageUserDelFlag(Image.DEL_FLAG_NORMAL);
        long onLabel30 = imageUserDao.getCountByUser(image);
        return onLabel30;
    }

//    /*
//    * 顾问已审核
//    * */
//    private long submitLabel30(Image image){
//        image.setInitialReviewStage(30);
//        image.setOwnership((byte) 0);
//        image.setReviewStage(null);
//        image.setLabelStatus(null);
//        image.setImageUserDelFlag(null);
//        long submit30 = imageUserDao.getCountByUser(image);
//        return submit30;
//    }



    /*
   * 1.用户自己的切片        type = 1
   * 2.获取所属全部用户的切片 type = 2
   * 3.获取所属某一用户的切片 type = 3
   * */
    private Image packageCondition(Image image){
        User user = UserUtils.getUser();
        if (image == null)
            throw new CoreException("条件为空");
        //如果有用户id的搜索条件
        int type;
        if (image.getActionType()==null || image.getActionType() == 1){
            //获取用户自己的切片
            type = 1;
            image.setUserId(UserUtils.getUser().getId());
        }
        else if(image.getActionType() == 2){
            //获取所属全部用户的切片
            type = 2;
            //如果不是管理员
            if (!user.isAdmin()){
                //获取当前用户下的所属用户
                List<String> userIdList;
                userIdList = UserUtils.getBelongUserIds();
                if (userIdList == null || userIdList.size()==0)
                    throw new CoreException("没有所属用户");
                image.setUserIdList(userIdList);
            }else {
                image.setUserIdList(null);
            }
            image.setUserId(null);
        }
        else if ((image.getActionType() == 3) && StringUtils.isNotBlank(image.getUserId())){
            //获取所属某一用户的切片
            type = 1;
        }else if ((image.getActionType() == 3) && StringUtils.isBlank(image.getUserId())){
            throw new CoreException("获取用户所属用户的切片时，必须上传userId");
        }else {
            throw new CoreException("参数不正确");
        }


        //如果有配置项的搜索条件
        if (StringUtils.isNotBlank(image.getDiseaseId())){
            //个人或者所属某用户
            if (type == 1){
                if (user.isAdmin()){
                    List<String> diseaseIdList = diseaseService.getChildsByParentIdAndCategory(image.getDiseaseId(),"disease");
                    image.setDiseaseIdList(diseaseIdList);
                }else {
                    //获取某疾病及其子疾病下的切片(按角色->配置项数据)
                    List<String> childIds = diseaseService.getAllChildIdsByParentIdAndUserId(image.getDiseaseId(),image.getUserId(),"disease");
                    if (childIds != null)
                        childIds.add(image.getDiseaseId());
                    else {
                        childIds = new ArrayList<>();
                        childIds.add(image.getDiseaseId());
                    }
                    image.setDiseaseIdList(childIds);
                }
            }else if (type==2){
                //管理员看到所有配置项数据
                if (user.isAdmin()){
                    List<String> diseaseIdList = diseaseService.getChildsByParentIdAndCategory(image.getDiseaseId(),"disease");
                    image.setDiseaseIdList(diseaseIdList);
                }else {
                    //所有所属用户
                    Set<String> belongsAllChildIds = new HashSet<>();
                    List<String> belongUserDiseaseIdList = getAllChildDiseaseIdByBelongUserIdListAndParentId(image.getUserIdList(),image.getDiseaseId(),"disease");
                    if (belongUserDiseaseIdList!=null && belongUserDiseaseIdList.size() > 0){
                        belongsAllChildIds.addAll(belongUserDiseaseIdList);
                    }
                    //添加parentId自己
                    belongsAllChildIds.add(image.getDiseaseId());
                    List list = new ArrayList();
                    list.addAll(belongsAllChildIds);
                    image.setDiseaseIdList(list);
                }
            }

        }else {
            //管理员看到所有配置项数据
            if (type == 1){
                if (user.isAdmin()){
                    List<String> diseaseIdList = diseaseService.getChildsByParentIdAndCategory("0","disease");
                    image.setDiseaseIdList(diseaseIdList);
                }else {
                    //获取某疾病及其子疾病下的切片(按角色->数据)
                    List<String> childIds = diseaseService.getAllChildIdsByParentIdAndUserId("0",image.getUserId(),"disease");
                    image.setDiseaseIdList(childIds);
                }
            }
            else if (type==2){
                //管理员看到所
                if (user.isAdmin()){
                    List<String> diseaseIdList = diseaseService.getChildsByParentIdAndCategory("0","disease");
                    image.setDiseaseIdList(diseaseIdList);
                }else {
                    List diseaseIdList = getAllDiseaseIdByBelongUserIdList(image.getUserIdList(),"disease");
                    image.setDiseaseIdList(diseaseIdList);
                }

            }
        }

        //如果不传配置项的搜索条件，则不返回数据
        if (!user.isAdmin())
            if (image.getDiseaseIdList() == null || image.getDiseaseIdList().size() == 0)
                throw  new CoreException("user does not have diseaseId");
        return image;
    }

    @Transactional(readOnly = false)
    public void rollback(String imageId,String describe,int difficult){
        User user = UserUtils.getUser();
        Map map = new HashMap();
        map.put("imageId",imageId);
        map.put("userId",user.getId());
        long count = imageUserDao.count(map);
        if (count > 0){
            //增加判断 用户是否有这张切片
            if(user.isAdmin()){
                throw new CoreException("管理员暂不支持退回");
            }
            else if (user.isAdviser()){
                //如果是顾问,可以直接找专家
                List<String> userId = imageUserDao.getUserIdByImageIdAndReviewStage(imageId,20);
                if (userId!=null && userId.size() == 1){
                    //退回专家
                    ImageUser imageUser = new ImageUser();
                    imageUser.setImageId(imageId);
                    imageUser.setUserId(userId.get(0));
                    imageUser.setRollback(1);
                    imageUser.setRollbackDescribes(describe);
                    imageUser.setOwnership((byte) 1);
                    imageUser.setRollbackUser(user.getId());
                    imageUser.setDelFlag("0");
                    imageUserDao.updateByImageIdUserIdSelective(imageUser);

                    //删除顾问的切片
                    imageUserDao.deleteByImageIdAndUserId(imageId,user.getId());

                    //图片状态改为专家退回状态
                    Image image = new Image();
                    image.setId(imageId);
                    image.setReviewStage(20);
                    image.setLabelStatus(2);
                    imageService.updateByPrimaryKeySelective(image);

                }else if (userId!=null && userId.size() > 1){
                    throw new CoreException("找到两个专家提交人，不能退回");
                }else if (userId==null){
                    throw new CoreException("该切片没有经过专家提交,是直接分配给顾问的数据，不能退回");
                }
            }
            else if (user.isExpert()){
                //如果是专家,可以直接找到医生或者主任
                List<String> userId = imageUserDao.getUserIdByImageIdAndReviewStage(imageId,11);
                if (userId!=null && userId.size() == 1){
                    //退回给医生或者主任
                    ImageUser imageUser = new ImageUser();
                    imageUser.setImageId(imageId);
                    imageUser.setUserId(userId.get(0));
                    imageUser.setRollback(1);
                    imageUser.setRollbackDescribes(describe);
                    imageUser.setOwnership((byte) 1);
                    imageUser.setRollbackUser(user.getId());
                    imageUser.setDelFlag("0");
                    imageUserDao.updateByImageIdUserIdSelective(imageUser);

                    //删除专家的切片
                    imageUserDao.deleteByImageIdAndUserId(imageId,user.getId());

                    //图片状态改为二审退回状态
                    Image image = new Image();
                    image.setId(imageId);
                    image.setReviewStage(11);
                    image.setLabelStatus(2);
                    imageService.updateByPrimaryKeySelective(image);

                }else if (userId!=null && userId.size() > 1){
                    throw new CoreException("找到两个二审提交人，不能退回");
                }else if (userId==null){
                    throw new CoreException("该切片没有经过医生或者主任二审提交,是直接分配给主任的数据，不能退回");
                }

            }else if (user.isDoctor() || user.isDirector()){
                Image ig = imageService.selectByPrimaryKey(imageId);
                if (ig.getReviewStage() == 11){
                    if (difficult == 1){
                        //主任才能退回疑难的片子，虽然一审可以提交疑难，二审可以提交疑难，但是疑难的片子都会按二审提交
                        if (user.isDirector()){
                            //假设是二审提交的疑难
                            boolean isReview11 = true;
                            //先判断是否是二审提交的疑难，因为imageUserDao.getUserIdByImageIdAndReviewStage(imageId,10)可能会将一审正常提交的人查找出来
                            List<String> userIdListll = imageUserDao.getUserIdByImageIdAndReviewStage(imageId,11);
                            if (userIdListll != null && userIdListll.size() > 0)
                                userIdListll.remove(user.getId());//删除主任本身
                            if (userIdListll == null || userIdListll.size() == 0){
                                //如果没有找到二审提交疑难的人，说明是一审提交的疑难，一审提交疑难时，切片不可能进行了正常的二审分配
                                userIdListll = imageUserDao.getUserIdByImageIdAndReviewStage(imageId,10);
                                isReview11 = false;
                            }
                            if (userIdListll!=null && userIdListll.size() > 0){
                                String userId = user.getId();
                                for (String rollbackId:userIdListll){
                                    //userIdListll里面包含主任自己
                                    if (!userId.equals(rollbackId)){
                                        //退回给医生
                                        ImageUser imageUser = new ImageUser();
                                        imageUser.setImageId(imageId);
                                        imageUser.setUserId(userIdListll.get(0));
                                        imageUser.setRollback(1);
                                        imageUser.setRollbackDescribes(describe);
                                        imageUser.setOwnership((byte) 1);
                                        imageUser.setDifficult(0);
                                        imageUser.setRollbackUser(userId);
                                        imageUser.setDelFlag("0");
                                        imageUserDao.updateByImageIdUserIdSelective(imageUser);

                                        //删除主任的切片
                                        imageUserDao.deleteByImageIdAndUserId(imageId,userId);

                                        Image image = new Image();
                                        image.setId(imageId);
                                        image.setLabelStatus(2);
                                        //切片状态改为二审退回状态
                                        if (isReview11)
                                            image.setReviewStage(11);
                                        else
                                            //切片状态改为一审退回状态
                                            image.setReviewStage(10);
                                        imageService.updateByPrimaryKeySelective(image);
                                    }
                                }
                            }
                        }

                    }else {
                        //二审提交的片子一定是退回给一审
                        List<String> userIdList10 = imageUserDao.getUserIdByImageIdAndReviewStage(imageId,10);
                        if (userIdList10!=null && userIdList10.size() == 1){
                            //退回给医生或者主任
                            ImageUser imageUser = new ImageUser();
                            imageUser.setImageId(imageId);
                            imageUser.setUserId(userIdList10.get(0));
                            imageUser.setRollback(1);
                            imageUser.setRollbackDescribes(describe);
                            imageUser.setOwnership((byte) 1);
                            imageUser.setRollbackUser(user.getId());
                            imageUser.setDelFlag("0");
                            imageUserDao.updateByImageIdUserIdSelective(imageUser);

                            //删除主任或者医生的切片
                            imageUserDao.deleteByImageIdAndUserId(imageId,user.getId());

                            //切片状态改为一审退回状态
                            Image image = new Image();
                            image.setId(imageId);
                            image.setReviewStage(10);
                            image.setLabelStatus(2);
                            imageService.updateByPrimaryKeySelective(image);


                        }else if (userIdList10!=null && userIdList10.size() > 1){
                            throw new CoreException("找到两个一审提交人，不能退回");
                        }else if (userIdList10==null){
                            throw new CoreException("该切片没有经过医生或者主任一审提交,是直接分配给主任的数据，不能退回");
                        }
                    }

                }else if (ig.getReviewStage() == 10){
                    throw new CoreException("一审切片,不能退回");
                }else {
                    throw new CoreException("切片状态："+ig.getReviewStage()+",不是主任和医生的状态");
                }
            }
            imageService.updateImageCount(imageId);
        }else {
            throw new CoreException("用户没有该切片");
        }
    }

    private List<String> getAllDiseaseIdByBelongUserIdList(List<String> userIdList,String category){
        //统计所属用户的所有配置项
        List<String> diseaseIdList = diseaseService.getDiseaseIdListByUserIdList(userIdList,category);
        return diseaseIdList;
    }

    private List<String> getAllChildDiseaseIdByBelongUserIdListAndParentId(List<String> userIdList,String parentId,String category){
        //统计所属用户的所有配置项
        Set<String> belongsAllChildIds = new HashSet<>();
        for (String userId:userIdList){
            List<String> childIds = diseaseService.getAllChildIdsByParentIdAndUserId(parentId,userId,category);
            if (childIds !=null && childIds.size() > 0)
                belongsAllChildIds.addAll(childIds);
        }
        if (belongsAllChildIds.size() > 0){
            List list = new ArrayList();
            list.addAll(belongsAllChildIds);
            return list;
        }
        return null;
    }


    private List<Image> getAdminImageList(Image image){
        if (StringUtils.isNotBlank(image.getDiseaseId())){
            List<String> childIds = diseaseService.getDiseaseChildIdsByParentId(image.getDiseaseId());
            if (childIds != null)
                childIds.add(image.getDiseaseId());
            else if (StringUtils.isNotBlank(image.getDiseaseId())){
                childIds = new ArrayList<>();
                childIds.add(image.getDiseaseId());
            }
            image.setDiseaseIdList(childIds);
            return imageUserDao.getImagesByUser(image);
        }else return null;
    }

    @Transactional(readOnly = false)
    public int transferOwnership(List<ImageIdUserIdVo> list, String transferUserId){
        int row = 0;
        for (ImageIdUserIdVo imageIdList:list){
            ImageUser imageUser = new ImageUser();
            imageUser.setImageId(imageIdList.getImageId());
            imageUser.setUserId(imageIdList.getUserId());
            imageUser.setOwnership((byte) 0);
            imageUser.setDelFlag("1");
            int row1 = updateByImageIdUserIdSelective(imageUser,true);
            if (row1 != 0){
                ImageUser transImage = new ImageUser();
                transImage.setImageId(imageIdList.getImageId());
                transImage.setUserId(transferUserId);
                transImage.setOwnership((byte) 1);
                int row2 = insertSelective(transImage);
                row += row1+row2;
            }
        }
        return row;
    }


    /*
    * currentThread：是否是当前线程操作
    * */
    @Transactional(readOnly = false)
    public int updateByImageIdUserIdSelective(ImageUser imageUser, boolean currentThread){
        if (currentThread){
            imageUser.preUpdate();
        }
        return imageUserDao.updateByImageIdUserIdSelective(imageUser);
    }

    /*
    * ownership 1:有所属权 0:没有所属权
    * */
    @Transactional(readOnly = false)
    public int updateOwnership(String userId,String imageId,int ownership){
        Map map = new HashMap();
        map.put("userId",userId);
        map.put("imageId",imageId);
        map.put("ownership",ownership);
        return  imageUserDao.updateOwnership(map);
    }

    public long countByUserId(String userId){
        Map map = new HashMap();
        map.put("userId",userId);
        return  imageUserDao.count(map);
    }

    public long countByImageId(String imageId){
        Map map = new HashMap();
        map.put("imageId",imageId);
        return  imageUserDao.count(map);
    }

    public long count(String userId,String imageId){
        Map map = new HashMap();
        map.put("imageId",imageId);
        map.put("userId",userId);
        return  imageUserDao.count(map);
    }

    public List<String> getUserIdByImageId(String imageId){
        return imageUserDao.getUserIdByImageId(imageId);
    }

    public int getOwnership(String imageId,String userId){
        return imageUserDao.getOwnership(imageId,userId);
    }

    @Transactional(readOnly = false)
    public void updateImageUserAndOverdue(ImageUserOverdue imageUserOverdue){
        //用户对数据不可见
        ImageUser imageUser = new ImageUser();
        imageUser.setUserId(imageUserOverdue.getUserId());
        imageUser.setImageId(imageUserOverdue.getImageId());
        imageUser.setDelFlag("1");
        imageUser.setUpdateBy("OverdueThread");
        imageUser.setUpdateDate(new Date());
        this.updateByImageIdUserIdSelective(imageUser,false);
        //删除过期数据
        CommonExample example = new CommonExample(ImageUserOverdue.class);
        example.createCriteria().
                andEqualTo(ImageUserOverdue.getFieldImageId(),imageUserOverdue.getImageId()).
                andEqualTo(ImageUserOverdue.getFieldUserId(),imageUserOverdue.getUserId());
        imageUserOverdueService.deleteByExample(example);
    }

    @Transactional(readOnly = false)
    public void allocationNewList(List<String> imageIdList,List<String> userIdList,Integer toInitialReviewStage, Integer toReviewStage, Integer toLabelStatus){
        for (String userId:userIdList){
            allocationNew(imageIdList,userId,toInitialReviewStage,toReviewStage,toLabelStatus);
        }
    }

    @Transactional(readOnly = false)
    public void allocationNew(List<String> imageIdList,String toUserId,Integer toInitialReviewStage, Integer toReviewStage, Integer toLabelStatus){
        for (String imageId:imageIdList){
            //标识未已分配
            Image image = new Image();
            image.setId(imageId);
            image.setAllocation(1);
            image.setLabelStatus(toLabelStatus);
            image.setReviewStage(toReviewStage);
            imageService.updateByPrimaryKeySelective(image);

            //添加
            ImageUser imageUser = new ImageUser();
            imageUser.setImageId(imageId);
            imageUser.setUserId(toUserId);
            imageUser.setReviewStage(toInitialReviewStage);
            this.insertSelective(imageUser);
        }
    }

    @Transactional(readOnly = false)
    public void allocationReviewPool(List<ImageUserIdVo> imageUserIdList, List<String> toUserIdList, Integer toInitialReviewStage, Integer toReviewStage, Integer toLabelStatus){
        for (ImageUserIdVo vo:imageUserIdList){
            //更新
            Image image = new Image();
            image.setLabelStatus(toLabelStatus);
            image.setReviewStage(toReviewStage);
            image.setId(vo.getImageId());
            imageService.updateByPrimaryKeySelective(image);
            for (String userId:toUserIdList){
                //添加
                ImageUser imageUser = new ImageUser();
                imageUser.setImageId(vo.getImageId());
                imageUser.setUserId(userId);
                imageUser.setReviewStage(toInitialReviewStage);
                this.insertSelective(imageUser);
            }
            //删除二审池数据
            imageReviewPoolService.deleteByPrimaryKey(vo.getImageId());
        }
    }

    @Transactional(readOnly = false)
    public void allocationAgain(List<ImageUserIdVo> imageUserIdList, List<String> toUserIdList, Integer toInitialReviewStage, Integer toReviewStage, Integer toLabelStatus){
        for (ImageUserIdVo vo:imageUserIdList){
            imageUserDao.deleteByImageIdAndUserId(vo.getImageId(),vo.getUserId());
            //更新
            Image image = new Image();
            image.setLabelStatus(toLabelStatus);
            image.setReviewStage(toReviewStage);
            image.setId(vo.getImageId());
            imageService.updateByPrimaryKeySelective(image);
            for (String userId:toUserIdList){
                //添加
                ImageUser imageUser = new ImageUser();
                imageUser.setImageId(vo.getImageId());
                imageUser.setUserId(userId);
                imageUser.setReviewStage(toInitialReviewStage);
                this.insertSelective(imageUser);
            }
        }
    }

    @Transactional(readOnly = false)
    public void allocationExpert(List<ImageUserIdVo> imageUserIdList, List<String> toUserIdList, Integer toInitialReviewStage, Integer toReviewStage, Integer toLabelStatus){
        for (ImageUserIdVo vo:imageUserIdList){
//            imageUserDao.deleteByImageIdAndUserId(vo.getImageId(),vo.getUserId());
            //更新
            Image image = new Image();
            image.setLabelStatus(toLabelStatus);
            image.setReviewStage(toReviewStage);
            image.setId(vo.getImageId());
            imageService.updateByPrimaryKeySelective(image);
            for (String userId:toUserIdList){
                //添加
                ImageUser imageUser = new ImageUser();
                imageUser.setImageId(vo.getImageId());
                imageUser.setUserId(userId);
                imageUser.setReviewStage(toInitialReviewStage);
                this.insertSelective(imageUser);
            }
        }
    }


    @Transactional(readOnly = false)
    public void allocation(List<String> imageIdList, String fromUserId, String toUserId,Integer toInitialReviewStage, Integer toReviewStage, Integer toLabelStatus ,boolean isDelete,boolean isNew){
        for (String imageId:imageIdList){
            //删除
            if (isDelete)
                imageUserDao.deleteByImageIdAndUserId(imageId,fromUserId);
            if (isNew){
                Image image = new Image();
                image.setId(imageId);
                image.setAllocation(1);
                imageService.updateByPrimaryKeySelective(image);
            }
            //更新
            Image image = new Image();
            image.setLabelStatus(toLabelStatus);
            image.setReviewStage(toReviewStage);
            image.setId(imageId);
            imageService.updateByPrimaryKeySelective(image);
            //添加
            ImageUser imageUser = new ImageUser();
            imageUser.setImageId(imageId);
            imageUser.setUserId(toUserId);
            imageUser.setReviewStage(toInitialReviewStage);
            this.insertSelective(imageUser);
        }
    }

    @Transactional(readOnly = false)
    public void allocationPool(List<String> imageIdList,String toUserId,String hospitalId){
        for (String imageId:imageIdList){
            //更新
            CommonExample example = new CommonExample(ImageReviewPool.class);
            example.createCriteria().
                    andEqualTo(ImageReviewPool.getFieldHospitalId(),hospitalId).
                    andEqualTo(ImageReviewPool.getFieldImageId(),imageId);
            imageReviewPoolService.deleteByExample(example);
            //添加
            ImageUser imageUser = new ImageUser();
            imageUser.setImageId(imageId);
            imageUser.setUserId(toUserId);
            imageUser.setReviewStage(11);
            this.insertSelective(imageUser);
        }
    }

    public int getMaxImageId(){
        return imageUserDao.getMaxImageId();
    }

    @Transactional(readOnly = false)
    public void deleteImageUser(List<String> imageIdList,String userId){
        for (String imageId : imageIdList){
            imageUserDao.deleteByImageIdAndUserId(imageId,userId);
        }
    }

    public long getCountByImageIdAndUserIdAndReviewStage(String imageId,String userId,int reviewStage){
        return imageUserDao.getCountByImageIdAndUserIdAndReviewStage(imageId,userId,reviewStage);
    }

    public long getCountByImageIdAndReviewStageAndOwnership(String imageId, int ownership,int reviewStage){
        return imageUserDao.getCountByImageIdAndReviewStageAndOwnership(imageId,ownership,reviewStage);
    }

    public Page<Image> imageListToExpertAdvisor(Image image){
        List<Image> imageList;
        imageList = imageUserDao.imageListToExpertAdvisor(image);
        image.getPage().setList(imageList);
        return image.getPage();
    }

    public Page<Image> imageAll(Image image){
        List<Image> imageList;
        imageList = imageUserDao.imageAll(image);
        image.getPage().setList(imageList);
        return image.getPage();
    }

}
