package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.exception.CoreException;
import com.thorough.core.modules.pathology.model.dao.LabelDao;
import com.thorough.core.modules.pathology.model.dao.LabelRelativeDao;
import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.model.entity.Label;
import com.thorough.core.modules.pathology.model.vo.LabelForm;
import com.thorough.core.modules.pathology.model.vo.LabelsForm;
import com.thorough.core.modules.sys.service.CoreDiseaseService;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.specification.service.CommonService;
import com.thorough.library.system.model.entity.Disease;
import com.thorough.library.utils.IdGenerate;
import com.thorough.library.utils.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class LabelService extends CommonService<String,LabelDao,Label> {

    @Autowired
    ImageService imageService;
    @Autowired
    CoreDiseaseService diseaseService;
    @Autowired
    LabelRelativeDao labelRelativeDao;

    public long countByExample(CommonExample example){
        return super.countByExample(example);
    }

    @Transactional(readOnly = false)
    public int insert(Label label){
        return super.insert(label);
    }

    @Transactional(readOnly = false)
    public int deleteByPrimaryKey(String labelId){
        return super.deleteByPrimaryKey(labelId);
    }

    @Transactional(readOnly = false)
    public int update(Label label, Label criterion){
        CommonExample example = new CommonExample(Label.class);
        if(StringUtils.isNoneBlank(criterion.getId())){
            example.createCriteria().andEqualTo(Label.getFieldId(),criterion.getId());
        }
        return super.updateByExampleSelective(label,example);
    }

    @Transactional(readOnly = false)
    public int updateByPrimaryKey(Label label){
        return super.updateByPrimaryKey(label);
    }

    public List<Label> findAllByExample(Label label){
        CommonExample example = new CommonExample(Label.class);
        example.createCriteria().
                andEqualTo(Label.getFieldImageId(),label.getImageId()).
                andEqualTo(Label.getFieldDelFlag(),label.getDelFlag());
        return this.dao.selectByExample(example);
    }

    public Map<String, Object> findLastLabelConfigByImageId(String imageId){
        CommonExample example = new CommonExample(Label.class);
        example.createCriteria().
                andEqualTo(Label.getFieldImageId(),imageId).
                andEqualTo(Label.getFieldDelFlag(),"0");
        example.setOrderByClause("create_date desc");
        List<Label> labelList = this.dao.selectByExample(example);
        Map<String,Object> config = new HashMap<>();
        if(labelList.size() > 0){
            Label label = labelList.get(0);
            String diseaseId = label.getDiseaseId();
            Disease disease = diseaseService.selectByPrimaryKey(diseaseId);
            if (disease != null){
                config.put("diseaseId",diseaseId);
                config.put("diseaseParentId",disease.getParentId());
                config.put("diseaseParentIds",disease.getParentIds());
            }else
                System.out.println(" don`t find disease by id:"+diseaseId);
        }
        return config;
    }

    @Transactional(readOnly = false)
    public Map<String,Object> updateMultiLabel(LabelsForm labels){
        String diseaseId = labels.getDiseaseId();
        List<LabelForm> labelList = labels.getLabels();
        Map<String,Object> map = null;
        if (StringUtils.isBlank(diseaseId) || labels == null)
            throw  new NullPointerException("diseaseId or labels is empty or null");
        else {
            map = new HashMap<>();
            List<String> failure = new ArrayList<>();
            List<String> success = new ArrayList<>();
            for(LabelForm form:labelList){
                if (StringUtils.isNotBlank(form.getId())){
                    Label label = new Label();
                    label.setId(form.getId());
                    label.setDiseaseId(diseaseId);
                    try {
                        int row = this.updateByPrimaryKeySelective(label);
                        if (row != 0)
                            success.add(label.getId());
                        else failure.add(label.getId());
                    }catch (Exception e){
                        failure.add(label.getId()+" || "+e.getMessage());
                    }
                }
            }
            map.put("success",success);
            map.put("failure",failure);
            return map;
        }
    }

    /*
    * 更新医生的审核状态
    * */
    @Transactional(readOnly = false)
    public int updateDoctorReviewStatus(String imageId,int reviewStatus){
        if (StringUtils.isNotBlank(imageId)){
            Label label = new Label();
            label.setReviewStatusDoctor(reviewStatus);
            CommonExample example = new CommonExample(Label.class);
            example.createCriteria().andEqualTo(Label.getFieldImageId(),imageId);
            int row = updateByExampleSelective(label,example);
            return row;
        }else return 0;
    }

     /*
    * 更新主任的审核状态
    * */

    @Transactional(readOnly = false)
    public int updateDirectorReviewStatus(String imageId,int reviewStatus){
        if (StringUtils.isNotBlank(imageId)){
            Label label = new Label();
            label.setReviewStatus((byte) reviewStatus);
            CommonExample example = new CommonExample(Label.class);
            example.createCriteria().andEqualTo(Label.getFieldImageId(),imageId);
            int row = updateByExampleSelective(label,example);
            return row;
        }else return 0;
    }

     /*
    * 更新专家的审核状态
    * */

    @Transactional(readOnly = false)
    public int updateExpertReviewStatus(String imageId,int reviewStatus){
        if (StringUtils.isNotBlank(imageId)){
            Label label = new Label();
            label.setReviewStatusExpert(reviewStatus);
            CommonExample example = new CommonExample(Label.class);
            example.createCriteria().andEqualTo(Label.getFieldImageId(),imageId);
            int row = updateByExampleSelective(label,example);
            return row;
        }else return 0;
    }

    /*
    * 更新顾问的审核状态
    * */

    @Transactional(readOnly = false)
    public int updateAdviserReviewStatus(String imageId,int reviewStatus){
        if (StringUtils.isNotBlank(imageId)){
            Label label = new Label();
            label.setReviewStatusAdviser(reviewStatus);
            CommonExample example = new CommonExample(Label.class);
            example.createCriteria().andEqualTo(Label.getFieldImageId(),imageId);
            int row = updateByExampleSelective(label,example);
            return row;
        }else return 0;
    }

    @Transactional(readOnly = false)
    public String saveSingleLabel(JSONObject jsonObject){
        Label label = new Label();
        String guid=null;
        try {
            guid = jsonObject.getString("guid");
        }catch (Exception e){
            e.printStackTrace();
        }
        String imageId;
        try {
            imageId = jsonObject.getString("imageId");
        }catch (Exception e){
            e.printStackTrace();
            throw  new CoreException("imageId is null");
        }
        String diseaseId;
        try {
            diseaseId = jsonObject.getString("diseaseId");
        }catch (Exception e){
            e.printStackTrace();
            throw  new CoreException("diseaseId is null");
        }

        label.setGuid(guid);
        label.setImageId(imageId);
        Image image = imageService.selectByPrimaryKey(label.getImageId());
        label.setPatientId(image.getPatientId());
        label.setDiseaseId(diseaseId);
        label.setContent(jsonObject.toString());

        try {
            label.setName(jsonObject.getString("name"));
        }catch (Exception e){
        }
        try {
            label.setShapeType(jsonObject.getString("shapeType"));
        }catch (Exception e){
        }
        try {
            label.setShapeName(jsonObject.getString("shapeName"));
        }catch (Exception e){
        }
        try {
            label.setResult(jsonObject.getString("result"));
        }catch (Exception e){
        }
        try {
            if(StringUtils.isBlank(label.getResult()))
            label.setResult(jsonObject.getString("score"));
        }catch (Exception e){
        }
        try {
            label.setDescription(jsonObject.getString("description"));
        }catch (Exception e){
        }
        try {
            label.setMarkerType(jsonObject.getInt("markerType"));
        }catch (Exception e){
        }
        try {
            label.setReviewerId(jsonObject.getString("reviewerId"));
        }catch (Exception e){
        }
        try {
            label.setReviewStatus((byte)jsonObject.getInt("reviewerStatus"));
        }catch (Exception e){
        }
        try {
            label.setRemarks(jsonObject.getString("remarks"));
        }catch (Exception e){
        }
        try {
            label.setId(jsonObject.getString("id"));
        }catch (Exception e){
        }

        if(StringUtils.isNotBlank(guid)){
            CommonExample example = new CommonExample(Label.class);
            example.createCriteria().andEqualTo(Label.getFieldGuid(),guid);
            long count = this.countByExample(example);
            if(count == 0){
                if(StringUtils.isBlank(label.getId())){
                    label.preInsert();
                    this.insert(label);
                }
            }
            else if(count == 1){
                //更新
                label.preUpdate();
                this.updateByPrimaryKeySelective(label);
            }else if(count > 1){
                    throw new RuntimeException("数据库存在多条guid一样的线，每个guid只能对应一条线");
            }
        }
        return label.getId();
    }

    @Transactional(readOnly = false)
    public void saveMultiStampsAndUpdateAiPredict(boolean finish,LabelsForm form,String jobId,String imageId,double process){
        if (finish){
            if(form.getLabels() != null && StringUtils.isNotBlank(form.getImageId())){
                saveMultiStampsAi(form);
                imageService.updateProgress(process,jobId,imageId);
            }
        } else {
            imageService.updateProgress(process,jobId,imageId);
        }
    }

    @Transactional(readOnly = false)
    public List<String> saveMultiStampsAi(LabelsForm form){
        List<LabelForm> labelForms = form.getLabels();
        List<String> ids = new ArrayList<>();
        int count = 0;
        if(labelForms != null){
            List<Label> labelList = new ArrayList<>();
            labelForms.forEach(stampForm -> {
                if(stampForm.getPoints()!=null && stampForm.getPoints().size() > 0){
                    Label label = convertStampForm2Label(stampForm, form.getImageId(),form.getDiseaseId());

                    if(label!=null)
                        //label.getGuid() 不为空 可能是一条线被缩放了
                        if(StringUtils.isNotBlank(label.getGuid())){
                            label.preInsert();
                            labelList.add(label);
                        }
                    ids.add(label.getId());
                }
            });
            count = labelRelativeDao.batchInsertLabel(labelList);
        }
        return ids;

    }


    @Transactional(readOnly = false)
    public List<String> saveMultiStamps(LabelsForm form){
        List<LabelForm> labelForms = form.getLabels();
        List<String> ids = new ArrayList<>();
        if(labelForms != null){
            labelForms.forEach(stampForm -> {
                if(stampForm.getPoints()!=null && stampForm.getPoints().size() > 0){
                    Label label = convertStampForm2Label(stampForm, form.getImageId(),form.getDiseaseId());

                    if(label!=null)
                    //label.getGuid() 不为空 可能是一条线被缩放了
                    if(StringUtils.isNotBlank(label.getGuid())){
                        CommonExample example = new CommonExample(Label.class);
                        example.createCriteria().andEqualTo(Label.getFieldGuid(),label.getGuid());
                        long count = this.countByExample(example);
                        if(count == 0){
                            if(StringUtils.isBlank(label.getId())){
                                label.preInsert();
                                this.insert(label);
                            }
                        }else if(count == 1){
                            //更新
                            label.preUpdate();
                            this.updateByPrimaryKeySelective(label);
                        }else if(count > 1){
                            throw new RuntimeException("数据库存在多条guid一样的线，每个guid只能对应一条线");
                        }

                    }else {
                        if(StringUtils.isBlank(label.getId())){
                            label.preInsert();
                            this.insert(label);
                        }else{
                            label.preUpdate();
                            this.updateByPrimaryKeySelective(label);
                        }
                    }
                    ids.add(label.getId());
                }
            });
        }
        if (ids.size() > 0){
            Image image = new Image();
            image.setAiPredict(1);
            image.setId(form.getImageId());
            imageService.updateByPrimaryKeySelective(image);
        }
        return ids;

    }

    private Label convertStampForm2Label(LabelForm labelForm, String imageId, String diseaseId){
        if(StringUtils.isBlank(imageId)){
            return null;
        }
        Image image = imageService.selectByPrimaryKey(imageId);
        Label label = new Label();
        label.setImageId(imageId);
        label.setPatientId(image.getPatientId());
        label.setId(labelForm.getId());
        label.setShapeName(labelForm.getType());
        label.setShapeType(labelForm.getType());
        label.setResult(labelForm.getScore());
        label.setDescription(labelForm.getDescription());
        label.setName(labelForm.getName());
        label.setMarkerType(2);
        label.setGuid(IdGenerate.uuid());

        //content
        JSONObject object = new JSONObject(labelForm);
        object.put("imageId",imageId);
        label.setContent(object.toString());

        //configId 是指复杂分析或者简单分析id,进行自动标注时，由后端系统传给ai，ai返回给后端；
        // labelId是一个int数值，表明是哪一种疾病，具体对应哪一种疾病，在pathology_disease表中查看
        if(StringUtils.isNotBlank(labelForm.getConfigId()) && labelForm.getLabelId() != null){
            CommonExample example = new CommonExample(Disease.class);
            example.createCriteria().
                    andEqualTo(Disease.getFieldParentId(),labelForm.getConfigId()).
                    andEqualTo(Disease.getFieldAiValue(),labelForm.getLabelId()+"").
                    andEqualTo(Disease.getFieldDelFlag(),"0");
            List<Disease> list = diseaseService.selectByExample(example);
            if (list !=null && list.size() == 1){
                label.setDiseaseId(list.get(0).getId());
            }else if (list !=null && list.size() == 0){
                throw new RuntimeException(" There can only be one disease,but not find disease by labelId="+labelForm.getLabelId()+" and configId="+labelForm.getConfigId());
            }
            else {
                throw new RuntimeException(" There can only be one disease,but find more than one disease by labelId="+labelForm.getLabelId()+" and configId="+labelForm.getConfigId());
            }

        }
        else if(StringUtils.isNotBlank(diseaseId)){
            //当ai系统不支持详细疾病分析时，疾病类型由前端上传
            label.setDiseaseId(diseaseId);
        }else{
            throw new RuntimeException("don`t find diseaseId!");
        }

        return label;
    }

}
