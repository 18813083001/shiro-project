package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.exception.CoreException;
import com.thorough.core.modules.pathology.model.dao.LabelRelativeDao;
import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.model.entity.LabelRelative;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.utils.UserUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class LabelRelativeService {
    @Autowired
    LabelRelativeDao labelRelativeDao;
    @Autowired
    ImageService imageService;

    public String getImageId(String labelId){
        return labelRelativeDao.getImageId(labelId);
    }

    public List<LabelRelative> getLabelsByImageId(String imageId, String field, String order){
        Map param = new HashMap();
        param.put("imageId",imageId);
        param.put("field",field);
        param.put("order",order);
        List<LabelRelative> labelList = labelRelativeDao.getLabels(param);
        for(LabelRelative label:labelList){
            String content = label.getContent();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(content);
                jsonObject.put("color",label.getRgb());
                label.setContent(jsonObject.toString());
            } catch (JSONException e) {
                throw new CoreException(e.getMessage());
            }
        }
        return labelList;
    }


    public List<LabelRelative> getLabels(String imageId, String field, String order){
        Image image = imageService.selectByPrimaryKey(imageId);
        if (image == null)
            return null;

        User user = UserUtils.getUser();
//        if (user.isAdmin()){
//            //如果是管理员，获取所有的数据
//            return getLabelsByImageId(imageId,field,order);
//        } else {
        List<LabelRelative> list = new LinkedList<>();
        List allLabel = getAllLabel(imageId,field,order);
        list.addAll(allLabel);

//                if (user.isDoctor()){
//                    List doctorData = getAllLabel(imageId,field,order);
//                    list.addAll(doctorData);
//
//                }else if (user.isDirector()){
//
//                    List labelList = getAllLabel(imageId,field,order);
//                    list.addAll(labelList);
//                }else if (user.isExpert()){
//                    //如果专家已经提交，则可以看到所有专家的标注
//                    if (image.getReviewStatusExpert() == 1){
//                        List expertData = getExpertData(imageId,field,order,1);
//                        list.addAll(expertData);
//                    }else {
//                        //只能看到专家自己标注的信息
//                        List<LabelRelative> expertData = getExpertData(imageId,field,order,0);
//                        for(LabelRelative label:expertData){
//                            if (user.getId().equals(label.getCreateBy())){
//                                list.add(label);
//                            }
//                        }
//                    }
//                    //只能查看主任已经提交的数据
//                    if (image.getReviewStatus() == 1){
//                        List expertData = getDirectorData(imageId,field,order,1);
//                        list.addAll(expertData);
//                    }
//
//
//                    //可以查看顾问已经提交的数据
//                    if (image.getReviewStatusAdviser() == 1){
//                        List adverData = getAdviserData(imageId,field,order,1);
//                        list.addAll(adverData);
//                    }
//                }else if (user.isAdviser()){
//                    //如果顾问已经提交，则可以看到所有顾问的标注
//                    if (image.getReviewStatusAdviser() == 1){
//                        List adviserData = getAdviserData(imageId,field,order,1);
//                        list.addAll(adviserData);
//                    }else {
//                        //只能看到顾问自己标注的信息
//                        List<LabelRelative> adviserData = getAdviserData(imageId,field,order,0);
//                        for(LabelRelative label:adviserData){
//                            if (user.getId().equals(label.getCreateBy())){
//                                list.add(label);
//                            }
//                        }
//                    }
//
//                    //只能看到专家提交的数据
//                    if (image.getReviewStatusExpert() == 1){
//                        List expertData = getExpertData(imageId,field,order,1);
//                        list.addAll(expertData);
//                    }
//                }
//            }
        List noRepeatList = new LinkedList();
        for(LabelRelative label:list){
            //过滤重复的数据
            if (!noRepeatList.contains(label)){
                String content = label.getContent();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    jsonObject.put("color",label.getRgb());
                    label.setContent(jsonObject.toString());
                    noRepeatList.add(label);
                } catch (JSONException e) {
                    throw new CoreException(e.getMessage());
                }
            }

        }
        //排序
        Collections.sort(noRepeatList);
        return noRepeatList;

    }

    private List getAllLabel(String imageId,String field,String order){
        Map param = new HashMap();
        param.put("imageId",imageId);
        param.put("field",field);
        param.put("order",order);
        List list = labelRelativeDao.getLabels(param);
        return list;
    }


    /*
    * 查找主任标注的数据
    * */
    private List getDirectorData(String imageId,String field,String order,int status){
        Map param = new HashMap();
        param.put("imageId",imageId);
        param.put("field",field);
        param.put("order",order);
        param.put("reviewStatus",status);
        List list = labelRelativeDao.getLabels(param);
        return list;
    }

    /*
   * 查找专家标注的数据
   * */
    private List getExpertData(String imageId,String field,String order,int status){
        Map param = new HashMap();
        param.put("imageId",imageId);
        param.put("field",field);
        param.put("order",order);
        param.put("reviewStatusExpert",status);
        List list = labelRelativeDao.getLabels(param);
        return list;
    }

    /*
   * 查找顾问标注的数据
   * */
    private List getAdviserData(String imageId,String field,String order,int status){
        Map param = new HashMap();
        param.put("imageId",imageId);
        param.put("field",field);
        param.put("order",order);
        param.put("reviewStatusAdviser",status);
        List list = labelRelativeDao.getLabels(param);
        return list;
    }

}
