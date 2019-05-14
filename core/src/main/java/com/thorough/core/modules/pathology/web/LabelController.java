package com.thorough.core.modules.pathology.web;

import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thorough.core.modules.pathology.aspect.label.log.LabelLog;
import com.thorough.core.modules.pathology.aspect.label.permission.Operation;
import com.thorough.core.modules.pathology.exception.CoreException;
import com.thorough.core.modules.pathology.exception.ScheduledPredictionTaskException;
import com.thorough.core.modules.pathology.model.entity.AiModel;
import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.model.entity.Label;
import com.thorough.core.modules.pathology.model.entity.LabelRelative;
import com.thorough.core.modules.pathology.model.vo.KafkaMessage;
import com.thorough.core.modules.pathology.model.vo.LabelForm;
import com.thorough.core.modules.pathology.model.vo.LabelsForm;
import com.thorough.core.modules.pathology.service.*;
import com.thorough.core.modules.pathology.thread.InitializingSystemThread;
import com.thorough.core.modules.pathology.util.CacheImageHolder;
import com.thorough.core.modules.sys.service.CoreDiseaseService;
import com.thorough.library.constant.Constant;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.specification.controller.BaseController;
import com.thorough.library.system.exception.LibraryException;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.utils.CacheUtils;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.*;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping(value = "${adminPath}/pathology/label/")
public class LabelController extends BaseController implements DisposableBean {

    protected Logger logger = LoggerFactory.getLogger(LabelController.class);

    @Autowired
    LabelService labelService;
    @Autowired
    LabelRelativeService relativeService;
    @Autowired
    AiModelService aiModelService;
    @Autowired
    ImageService imageService;
    @Autowired
    CacheImageHolder cacheImageHolder;
    @Autowired
    CoreDiseaseService diseaseService;

    @Autowired
    DiseaseCustomService diseaseCustomService;
    @Autowired
    ImageController imageController;

    @Autowired
    MongoService mongoService;

//    @Operation(value = "save")
//    @LabelLog(value="save")
//    @RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> save(@RequestBody String param) {
//        ResponseBuilder builder = ResponseBuilder.newInstance();
//        JSONObject jsonObject = new JSONObject(param);
//        try {
//            String shapeType =jsonObject.getString("shapeType");
//            if ((shapeType.equals("Curve") || shapeType.equals("Polygon"))){
//                JSONArray points =jsonObject.getJSONArray("points");
//                if (points.length() == 0){
//                    builder.error();
//                    builder.message(" 图形类型为Curve或者Polygon时，坐标点points是空");
//                }else {
//                    String labelId = labelService.saveSingleLabel(jsonObject);
//                    builder.add("id",labelId);
//                }
//            }else {
//                String labelId = labelService.saveSingleLabel(jsonObject);
//                builder.add("id",labelId);
//            }
//        }catch (Exception e){
//            builder.error();
//            builder.message(e.getMessage());
//        }
//
//        return builder.build();
//    }

    /**
     * mongo
     * */
    @Operation(value = "save")
    @LabelLog(value="save")
    @RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody String param) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        JSONObject jsonObject = new JSONObject(param);
        try {
            String shapeType =jsonObject.getString("shapeType");
            if ((shapeType.equals("Curve") || shapeType.equals("Polygon"))){
                JSONArray points =jsonObject.getJSONArray("points");
                if (points.length() == 0){
                    builder.error();
                    builder.message(" 图形类型为Curve或者Polygon时，坐标点points是空");
                }else {
                    String labelId = mongoService.saveSingleLabelMongo(jsonObject);
                    builder.add("id",labelId);
                }
            }else {
                String labelId = mongoService.saveSingleLabelMongo(jsonObject);
                builder.add("id",labelId);
            }
        }catch (Exception e){
            builder.error();
            builder.message(e.getMessage());
        }

        return builder.build();
    }

//    @Operation(value = "saveMultiManual")
//    @LabelLog(value="saveMultiManual")
//    @RequestMapping(value = "/multiSaveaManual", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> multiSaveaManual(@RequestBody String params) {
//        ResponseBuilder builder = ResponseBuilder.newInstance();
//        try {
//            List<String> success = new ArrayList<>();
//            List<String> failure = new ArrayList<>();
//            JSONArray jsonArray = new JSONArray(params);
//            for(int i = 0;i < jsonArray.length();i++){
//                try {
//                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                    JSONArray points =jsonObject.getJSONArray("points");
//                    String shapeType =jsonObject.getString("shapeType");
//                    if ((shapeType.equals("Curve") || shapeType.equals("Polygon")) && points.length() == 0){
//                        failure.add("图形类型为Curve或者Polygon时，坐标点points是空");
//                    }else {
//                        String labelId = labelService.saveSingleLabel(jsonObject);
//                        success.add(labelId);
//                    }
//                }catch (Exception e){
//                    failure.add(e.getMessage());
//                }
//            }
//            builder.add("ids",success);
//            builder.add("failureSize",failure.size());
//            builder.add("failurReason",failure);
//        }catch (Exception e){
//            builder.error();
//            builder.message(e.getMessage());
//        }
//        return builder.build();
//    }

    /**
     * mongo
     * */
    @Operation(value = "saveMultiManual")
    @LabelLog(value="saveMultiManual")
    @RequestMapping(value = "/multiSaveaManual", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> multiSaveaManual(@RequestBody String params) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        try {
            List<String> success = new ArrayList<>();
            List<String> failure = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(params);
            for(int i = 0;i < jsonArray.length();i++){
                try {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    JSONArray points =jsonObject.getJSONArray("points");
                    String shapeType =jsonObject.getString("shapeType");
                    if ((shapeType.equals("Curve") || shapeType.equals("Polygon")) && points.length() == 0){
                        failure.add("图形类型为Curve或者Polygon时，坐标点points是空");
                    }else {
                        String labelId = mongoService.saveSingleLabelMongo(jsonObject);
                        success.add(labelId);
                    }
                }catch (Exception e){
                    failure.add(e.getMessage());
                }
            }
            builder.add("ids",success);
            builder.add("failureSize",failure.size());
            builder.add("failurReason",failure);
        }catch (Exception e){
            builder.error();
            builder.message(e.getMessage());
        }
        return builder.build();
    }

    @Operation(value = "multiSaveAuto")
    @LabelLog(value="multiSaveAuto")
    @RequestMapping(value = "/multiSave", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveMultiLabelsByAi(@RequestBody LabelsForm form) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<String> stampIds = null;
        // AI自动标注接口，具体的diseaseId由LabelForm来确定，这里不做判断
        if(form.getLabels() != null && StringUtils.isNotBlank(form.getImageId())){
            stampIds = labelService.saveMultiStamps(form);
        }else {
            builder.error();
            builder.code(HttpStatus.BAD_REQUEST);
            if (StringUtils.isBlank(form.getImageId())){
                builder.message("imageId is empty!");
            }else {
                builder.message("labels is null!");
            }
        }
        return builder.add("stampIds", stampIds).build();
    }

//    @RequestMapping(value = "/label", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> label(String id) {
//        ResponseBuilder builder = ResponseBuilder.newInstance();
//        Label label = this.labelService.selectByPrimaryKey(id);
//        builder.add("label",label);
//        return builder.build();
//    }

    /**
     * mongo
     * */
    @RequestMapping(value = "/label", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> label(String id) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        Document label = this.mongoService.getLabelById(id);
        label.put("imageId",label.get("image_id"));
        label.put("diseaseId",label.get("disease_id"));
        label.put("content",label.get("content")!=null ?((Document)label.get("content")).toJson():null);
        builder.add("label",label);
        return builder.build();
    }

//    @Operation(value = "updateMultiLabel")
//    @LabelLog(value="updateMultiLabel")
//    @RequestMapping(value = "/updateMultiLabel", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> updateMultiLabel(@RequestBody LabelsForm form){
//        ResponseBuilder builder = ResponseBuilder.newInstance();
//        Map result = null;
//        if(form.getLabels() != null && StringUtils.isNotBlank(form.getDiseaseId())){
//            result = labelService.updateMultiLabel(form);
//        }else {
//            if (StringUtils.isBlank(form.getDiseaseId())){
//                throw new CoreException("diseaseId is empty!");
//            }else {
//                throw new CoreException("labelIds is null!");
//            }
//        }
//        builder.add(result);
//        return builder.build();
//    }

    /**
     * mongo
     * */
    @Operation(value = "updateMultiLabel")
    @LabelLog(value="updateMultiLabel")
    @RequestMapping(value = "/updateMultiLabel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMultiLabel(@RequestBody LabelsForm form){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        Map result = null;
        if(form.getLabels() != null && StringUtils.isNotBlank(form.getDiseaseId())){
            result = mongoService.updateMultiLabel(form);
        }else {
            if (StringUtils.isBlank(form.getDiseaseId())){
                throw new LibraryException("diseaseId is empty!");
            }else {
                throw new LibraryException("labelIds is null!");
            }
        }
        builder.add(result);
        return builder.build();
    }


//    @Operation(value = "batchDelete")
//    @LabelLog(value="batchDelete")
//    @RequestMapping(value = "batchDelete", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> batchDelete(@RequestBody LabelsForm labelsForm){
//        ResponseBuilder builder = ResponseBuilder.newInstance();
//        List<String> success = new ArrayList<>();
//        List<LabelForm> labelIdList = labelsForm.getLabels()!=null?labelsForm.getLabels():new ArrayList();
//        try {
//            if (labelIdList.size() > 0){
//                for (LabelForm labelForm:labelIdList){
//                    if (StringUtils.isNotBlank(labelForm.getId())){
//                        int i = labelService.deleteByPrimaryKey(labelForm.getId());
//                        if (i == 1)
//                            success.add(labelForm.getId());
//                    }
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        builder.add("ids",success);
//        return builder.build();
//    }

    /**
     * mongo
     */
    @Operation(value = "batchDelete")
    @LabelLog(value="batchDelete")
    @RequestMapping(value = "batchDelete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> batchDelete(@RequestBody LabelsForm labelsForm){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        long success = 0;
        List<LabelForm> labelFormList = labelsForm.getLabels()!=null?labelsForm.getLabels():new ArrayList();
        List<String> labelIdList = new ArrayList<>();
        try {
            if (labelFormList.size() > 0){

                for (LabelForm form:labelFormList){
                    labelIdList.add(form.getId());
                }
                success = mongoService.batchDelete(labelIdList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        builder.add("ids",labelIdList);
        return builder.build();
    }

//    @Operation(value = "delete")
//    @LabelLog(value="delete")
//    @RequestMapping(value = "delete")
//    public ResponseEntity<?> delete(Label label){
//        ResponseBuilder builder = ResponseBuilder.newInstance();
//        if(StringUtils.isNotBlank(label.getId())){
//            int i = labelService.deleteByPrimaryKey(label.getId());
//            builder.add("id",i);
//        }else {
//            builder.add("id","删除失败，id为空");
//        }
//        return builder.build();
//    }

    /**
     * mongo
     */
    @Operation(value = "delete")
    @LabelLog(value="delete")
    @RequestMapping(value = "delete")
    public ResponseEntity<?> delete(Label label){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if(StringUtils.isNotBlank(label.getId())){
            long count = mongoService.deleteByLabelId(label.getId());
            builder.add("count",count);
        }else {
            builder.add("id","删除失败，id为空");
        }
        return builder.build();
    }

//    @RequestMapping(value = "/labelCount", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> labelCount(@RequestParam String imageId) {
//        CommonExample example = new CommonExample(Label.class);
//        example.createCriteria().andEqualTo(Label.getFieldImageId(),imageId);
//        long count = labelService.countByExample(example);
//        ResponseBuilder builder = ResponseBuilder.newInstance().add("count", count);
//        return builder.build();
//    }

    /**
     * mongo
     * */
    @RequestMapping(value = "/labelCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> labelCount(@RequestParam String imageId) {
        long count = mongoService.countByImageId(imageId);
        ResponseBuilder builder = ResponseBuilder.newInstance().add("count", count);
        return builder.build();
    }

//    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> labelList(@RequestParam String imageId, String field, String order) {
//        List<LabelRelative> list = null;
//        list = getlabelListByImageId(imageId,field,order);
//        if (list != null)
//            for(int i=0;i < list.size();i++){
//                LabelRelative relative = list.get(i);
//                if(relative.getMarkerType() == 1){
//                    User user = UserUtils.get(relative.getCreateBy());
//                    if(user != null){
//                        relative.setMarkerName(user.getName());
//                    }
//                }else if(relative.getMarkerType() == 2){
//                    relative.setMarkerName("AI");
//                }
//            }
//        ResponseBuilder builder = ResponseBuilder.newInstance().add("stamps", list);
//        return builder.build();
//    }

    /**
     * mongo
     */
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> labelListFromMongo(@RequestParam String imageId, String field, String order) {
        List<Document> list;
        System.out.println("list-start:"+DateUtils.getDateTime());
        list = mongoService.getLabelsFromMongo(imageId);
        System.out.println("list-getDate:"+DateUtils.getDateTime());
        if (list != null && list.size() > 0) {
            Set<String> diseaseIdSet = new LinkedHashSet<>();
            Set<String> createBySet = new LinkedHashSet<>();
            for (Document document : list) {
                String diseaseId = (String) document.get("disease_id");
                String createBy = (String) document.get("create_by");
                diseaseIdSet.add(diseaseId);
                createBySet.add(createBy);
            }
            List<Map<String,String>> diseaseMapList = diseaseCustomService.getDiseaseByDiseaseIdList(diseaseIdSet);
            List<Map<String,String>> userIdNameMapList = UserUtils.getUserNameListByUserIdList(createBySet);
            if (diseaseMapList != null && userIdNameMapList!= null){
                for (Document document:list){
                    String diseaseId = (String) document.get("disease_id");
                    for (Map<String,String> map:diseaseMapList){
                        String id = map.get("id");
                        if (diseaseId.equals(id)){
                            String diseaseName = map.get("name");
                            String diseaseParentId = map.get("parentId");
                            String rgb = map.get("rgb");

                            document.put("diseaseName",diseaseName);
                            document.put("diseaseParentId",diseaseParentId);
                            Document contentDocument = (Document) document.get("content");
                            contentDocument.put("color",rgb);
                            document.put("rgb",rgb);
                            break;
                        }
                    }
                    int markerType = document.getInteger("marker_type");
                    String createBy = document.getString("create_by");
                    if (markerType == 1){
                        for (Map<String,String> map:userIdNameMapList){
                            String id = map.get("id");
                            if (createBy.equals(id)){
                                String name = map.get("name");
                                document.put("markerName",name);
                                break;
                            }
                        }
                    }else {
                        document.put("markerName","AI");
                    }
                    document.put("content",document.get("content")!=null ?((Document)document.get("content")).toJson():null);

//                    document.put("diseaseId",document.get("disease_id"));
//                    document.put("imageId",document.get("image_id"));
//                    document.put("markerType",document.get("marker_type"));
//                    document.put("shapeName",document.get("shape_name"));
//                    document.put("shapeType",document.get("shape_type"));
//                    document.put("createDate",document.get("create_date"));
//                    document.put("updateBy",document.get("update_by"));
//                    document.put("delFlag",document.get("del_flag"));
//                    document.put("patientId",document.get("patient_id"));
//                    document.put("reviewerId",document.get("reviewer_id"));
//                    document.put("updateBy",document.get("update_by"));
//                    document.put("updateDate",document.get("update_date"));
                }

            }
        }
        System.out.println("list-finish:"+DateUtils.getDateTime());
        ResponseBuilder builder = ResponseBuilder.newInstance().add("stamps", list);
        return builder.build();
    }


//    @RequestMapping(value = "/labelFile", produces = MediaType.APPLICATION_JSON_VALUE)
//    public void labelFile(@RequestParam String imageId, String field, String order, HttpServletResponse response) throws IOException {
//        List<LabelRelative> list = null;
//        list = getlabelListByImageId(imageId,field,order);
//        if (list != null)
//            for(int i=0;i < list.size();i++){
//                LabelRelative relative = list.get(i);
//                if(relative.getMarkerType() == 1){
//                    User user = UserUtils.get(relative.getCreateBy());
//                    if(user != null){
//                        relative.setMarkerName(user.getName());
//                    }
//                }else if(relative.getMarkerType() == 2){
//                    relative.setMarkerName("AI");
//                }
//            }
//        jsonToFile(list,response);
//    }

    /**
     * mongo
     * */
    @RequestMapping(value = "/labelFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public void labelFile(@RequestParam String imageId, String field, String order,HttpServletResponse response) throws IOException {
        List<Document> list;
        System.out.println("file-list-start:"+DateUtils.getDateTime());
        list = mongoService.getLabelsFromMongo(imageId);
        System.out.println("file-list-getDate:"+DateUtils.getDateTime());
        if (list != null && list.size() > 0) {
            Set<String> diseaseIdSet = new LinkedHashSet<>();
            Set<String> createBySet = new LinkedHashSet<>();
            for (Document document : list) {
                String diseaseId = (String) document.get("disease_id");
                String createBy = (String) document.get("create_by");
                diseaseIdSet.add(diseaseId);
                createBySet.add(createBy);
            }
            List<Map<String,String>> diseaseMapList = diseaseCustomService.getDiseaseByDiseaseIdList(diseaseIdSet);
            List<Map<String,String>> userIdNameMapList = UserUtils.getUserNameListByUserIdList(createBySet);
            if (diseaseMapList != null && userIdNameMapList!= null){
                for (Document document:list){
                    String diseaseId = (String) document.get("disease_id");
                    for (Map<String,String> map:diseaseMapList){
                        String id = map.get("id");
                        if (diseaseId.equals(id)){
                            String diseaseName = map.get("name");
                            String diseaseParentId = map.get("parentId");
                            String rgb = map.get("rgb");

                            document.put("diseaseName",diseaseName);
                            document.put("diseaseParentId",diseaseParentId);
                            Document contentDocument = (Document) document.get("content");
                            contentDocument.put("color",rgb);
                            document.put("rgb",rgb);
                            break;
                        }
                    }
                    int markerType = document.getInteger("marker_type");
                    String createBy = document.getString("create_by");
                    if (markerType == 1){
                        for (Map<String,String> map:userIdNameMapList){
                            String id = map.get("id");
                            if (createBy.equals(id)){
                                String name = map.get("name");
                                document.put("markerName",name);
                                break;
                            }
                        }
                    }else {
                        document.put("markerName","AI");
                    }
                    document.put("content",document.get("content")!=null ?((Document)document.get("content")).toJson():null);

//                    document.put("diseaseId",document.get("disease_id"));
//                    document.put("imageId",document.get("image_id"));
//                    document.put("markerType",document.get("marker_type"));
//                    document.put("shapeName",document.get("shape_name"));
//                    document.put("shapeType",document.get("shape_type"));
//                    document.put("createDate",document.get("create_date"));
//                    document.put("updateBy",document.get("update_by"));
//                    document.put("delFlag",document.get("del_flag"));
//                    document.put("patientId",document.get("patient_id"));
//                    document.put("reviewerId",document.get("reviewer_id"));
//                    document.put("updateBy",document.get("update_by"));
//                    document.put("updateDate",document.get("update_date"));

                }
            }
        }
        System.out.println("file-list-finish:"+DateUtils.getDateTime());

        jsonToFile(list,response);
    }

    public void jsonToFile(Object list,HttpServletResponse response) throws IOException {
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition","attachment; filename="+"label.zip");
        ObjectMapper mapper = new ObjectMapper();
        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        out.putNextEntry(new ZipEntry("labelList.json"));
        mapper.writeValue(out, list);
        out.close();
        response.flushBuffer();
    }

//    @RequestMapping(value = "/listReult", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> listReult(String imageId, String field, String order) {
//        List<LabelRelative> list = null;
//        list = getlabelListByImageId(imageId,field,order);
//        List<Map<String,Object>> maps = new ArrayList<>();
//        if (list != null)
//        for(int i=0;i < list.size();i++){
//            LabelRelative relative = list.get(i);
//            if(relative.getMarkerType() == 1){
//                User user = UserUtils.get(relative.getCreateBy());
//                if(user != null){
//                    relative.setMarkerName(user.getName());
//                }
//            }else if(relative.getMarkerType() == 2){
//                relative.setMarkerName("AI");
//            }
//            Map map = new HashMap();
//            map.put("id",relative.getId());
//            map.put("rgb",relative.getRgb());
//            map.put("result",relative.getResult());
//            map.put("diseaseName",relative.getDiseaseName());
//            map.put("diseaseId",relative.getDiseaseId());
//            map.put("diseaseParentId",relative.getDiseaseParentId());
//            map.put("createBy",relative.getCreateBy());
//            map.put("markerName",relative.getMarkerName());
//            map.put("markerType",relative.getMarkerType());
//            maps.add(map);
//        }
//        ResponseBuilder builder = ResponseBuilder.newInstance().add("labelsResult", maps);
//        return builder.build();
//    }

    /**
     * mongo
     * */
    @RequestMapping(value = "/listReult", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listReult(String imageId, String field, String order) {
        List<Document> list;
        System.out.println("list-start:"+DateUtils.getDateTime());
        list = mongoService.getLabelsInfoFromMongo(imageId,false);
        System.out.println("list-getDate:"+DateUtils.getDateTime());
        if (list != null && list.size() > 0) {
            Set<String> diseaseIdSet = new LinkedHashSet<>();
            Set<String> createBySet = new LinkedHashSet<>();
            for (Document document : list) {
                String diseaseId = (String) document.get("disease_id");
                String createBy = (String) document.get("create_by");
                diseaseIdSet.add(diseaseId);
                createBySet.add(createBy);
            }
            List<Map<String,String>> diseaseMapList = diseaseCustomService.getDiseaseByDiseaseIdList(diseaseIdSet);
            List<Map<String,String>> userIdNameMapList = UserUtils.getUserNameListByUserIdList(createBySet);
            if (diseaseMapList != null && userIdNameMapList!= null){
                for (Document document:list){
                    String diseaseId = (String) document.get("disease_id");
                    for (Map<String,String> map:diseaseMapList){
                        String id = map.get("id");
                        if (diseaseId.equals(id)){
                            String diseaseName = map.get("name");
                            String diseaseParentId = map.get("parentId");
                            String rgb = map.get("rgb");

                            document.put("diseaseName",diseaseName);
                            document.put("diseaseParentId",diseaseParentId);
                            document.put("color",rgb);
                            break;
                        }
                    }
                    int markerType = document.getInteger("marker_type");
                    String createBy = document.getString("create_by");
                    if (markerType == 1){
                        for (Map<String,String> map:userIdNameMapList){
                            String id = map.get("id");
                            if (createBy.equals(id)){
                                String name = map.get("name");
                                document.put("markerName",name);
                                break;
                            }
                        }
                    }else {
                        document.put("markerName","AI");
                    }
                }
            }
        }

        List<Map<String,Object>> maps = new ArrayList<>();
        if (list != null)
            for(int i=0;i < list.size();i++){
                Document document = list.get(i);
                Map map = new HashMap();
                map.put("id",document.get("id"));
                map.put("rgb",document.getString("color"));
                map.put("result",document.get("result"));
                map.put("diseaseName",document.get("diseaseName"));
                map.put("diseaseId",document.get("disease_id"));
                map.put("diseaseParentId",document.get("diseaseParentId"));
                map.put("createBy",document.get("create_By"));
                map.put("markerName",document.get("markerName"));
                map.put("markerType",document.get("marker_type"));
                maps.add(map);
            }
        ResponseBuilder builder = ResponseBuilder.newInstance().add("labelsResult", maps);
        return builder.build();
    }

    private List<LabelRelative> getlabelListByImageId(String imageId, String field, String order){
        List list = relativeService.getLabels(imageId,field,order);
        return list;
    }

//    @RequestMapping(value = "/lastConfig", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> lastDiseaseConfig(String imageId){
//        ResponseBuilder builder = ResponseBuilder.newInstance().add("lastConfig", labelService.findLastLabelConfigByImageId(imageId));
//        System.out.println("lastConfig: ");
//        return builder.build();
//    }

    /**
     * mongo
     * */
    @RequestMapping(value = "/lastConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> lastDiseaseConfig(String imageId){
        ResponseBuilder builder = ResponseBuilder.newInstance().add("lastConfig", mongoService.findLastLabelConfigByImageId(imageId));
        return builder.build();
    }



    /**
     * @param diseaseId diseaseId以前是用来接收复杂分析或者简单分析的ID。从2018-11-7日起，考虑这是后端用的功能，决定将diseaseId作为真正的disease类型的ID,
     *                  也就是切片录入时的diseaseId,预测时通过diseaseId找到分类ID，通过分类ID找到需要预测的模型，所以ai_model表中将
     *                  由以前的复杂分析或者简单分析对应某个模型改为分类对应某个模型，如果某分类有多个模型，必须设置只有一个模型可用，其
     *                  他模型暂时不可以
     * */
    @RequestMapping(value = "/autoLabel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> autoLabel(@RequestParam String path,
                                       @RequestParam Double topLeftX,
                                       @RequestParam Double topLeftY,
                                       @RequestParam Double bottomRightX,
                                       @RequestParam Double bottomRightY,
                                       @RequestParam Double width,
                                       @RequestParam Double height,
                                       @RequestParam String imageId,
                                       @RequestParam String jobType,
                                       @RequestParam String diseaseId) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        //查找ai模型
        Map<String,String> region = diseaseService.getParentByChildId(diseaseId, Constant.CATEGORY_RIGION);
        if (region == null || region.size() == 0){
            builder.error();
            builder.message("根据diseaseId:"+diseaseId+",没有找到分类");
            return builder.build();
        }
        String regionId = region.get("id");
        AiModel aiModel = aiModelService.getAiModelByImageIdAndLabelTypeId(regionId);
        if (aiModel == null){
            builder.error();
            builder.message("根据regionId:"+regionId+",没有找到AI模型");
            return builder.build();
        }

        //任务参数
        Map<String,Object> requestParam = new HashMap<>();
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        requestParam.put("job_id",uuid);
        requestParam.put("image_path",path);
        requestParam.put("image_id",imageId);
        requestParam.put("job_type",jobType);
        List<Integer> size = new ArrayList<>();
        size.add(width.intValue());
        size.add(height.intValue());
        requestParam.put("image_size",size);
        List<Integer> topLeft = new ArrayList<>();
        topLeft.add(topLeftX.intValue());
        topLeft.add(topLeftY.intValue());
        requestParam.put("top_left",topLeft);
        List<Integer> bottomRight = new ArrayList<>();
        bottomRight.add(bottomRightX.intValue());
        bottomRight.add(bottomRightY.intValue());
        requestParam.put("bottom_right",bottomRight);
        //模型参数
        requestParam.put("model_name",aiModel.getModel());
        requestParam.put("level",aiModel.getLavel());
        //简单分析或者复杂分析ID
        requestParam.put("configId",aiModel.getLabelTypeId());
        requestParam.put("model_type",aiModel.getModelType());
        String paramJson = JSONUtils.toJSONString(requestParam);

        //kafka
        String brokerList = PropertyUtil.getProperty("pathology.kafka.broker.list");
        Properties props = new Properties();
        //此处配置的是kafka的端口
        props.put("metadata.broker.list", brokerList);
        //配置value的序列化类
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        //配置key的序列化类
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");

        //request.required.acks
        //0, which means that the producer never waits for an acknowledgement from the broker (the same behavior as 0.7). This option provides the lowest latency but the weakest durability guarantees (some data will be lost when a server fails).
        //1, which means that the producer gets an acknowledgement after the leader replica has received the data. This option provides better durability as the client waits until the server acknowledges the request as successful (only messages that were written to the now-dead leader but not yet replicated will be lost).
        //-1, which means that the producer gets an acknowledgement after all in-sync replicas have received the data. This option provides the best durability, we guarantee that no messages will be lost as long as at least one in sync replica remains.
        props.put("request.required.acks","-1");
        Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(props));
        String topic = PropertyUtil.getProperty("pathology.kafka.producer.topic");
        producer.send(new KeyedMessage<String, String>(topic, "mkey" ,paramJson));

        Map<String, Object> response = new HashMap<>();
        response.put("id", uuid);

        builder.add(response);
        return  builder.build();
    }

    @RequestMapping(value = "/cancelAutoLabel")
    public ResponseEntity<?> cancelAutoLabel(String jobId, String command, Integer priority){
        //kafka
        Properties props = new Properties();
        //此处配置的是kafka的端口
        String brokerList = PropertyUtil.getProperty("pathology.kafka.broker.list");
        props.put("metadata.broker.list", brokerList);

        //配置value的序列化类
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        //配置key的序列化类
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");

        //request.required.acks
        //0, which means that the producer never waits for an acknowledgement from the broker (the same behavior as 0.7). This option provides the lowest latency but the weakest durability guarantees (some data will be lost when a server fails).
        //1, which means that the producer gets an acknowledgement after the leader replica has received the data. This option provides better durability as the client waits until the server acknowledges the request as successful (only messages that were written to the now-dead leader but not yet replicated will be lost).
        //-1, which means that the producer gets an acknowledgement after all in-sync replicas have received the data. This option provides the best durability, we guarantee that no messages will be lost as long as at least one in sync replica remains.
        props.put("request.required.acks","-1");
        Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(props));
        String topic = PropertyUtil.getProperty("pathology.kafka.producer.cancel");
        Map<String,Object> requestParam = new HashMap<>();
        ResponseBuilder builder = ResponseBuilder.newInstance();
        requestParam.put("job_id",jobId);
        //设置参数
        if ("cancel".equals(command)){
            requestParam.put("command","cancel");
            builder.message("取消成功");
        }
        else if ("priority".equals(command)){
            requestParam.put("command","priority");
            requestParam.put("priority",priority);
            builder.message("加速成功");
        }

        //发起预测
        String paramJson = JSONUtils.toJSONString(requestParam);
        producer.send(new KeyedMessage<String, String>(topic, "mkey" ,paramJson));

        //设置返回结果，如果是取消，将任务从任务队列里删除
        if ("cancel".equals(command)){
            builder.message("取消成功");
        }
        return builder.build();
    }

    @RequestMapping(value = "/autoLabelTest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> autoLabelTest(String param){

        //kafka
        Properties props = new Properties();
        //此处配置的是kafka的端口
        String brokerList = PropertyUtil.getProperty("pathology.kafka.broker.list");;
        props.put("metadata.broker.list", brokerList);

        //配置value的序列化类
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        //配置key的序列化类
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");

        //request.required.acks
        //0, which means that the producer never waits for an acknowledgement from the broker (the same behavior as 0.7). This option provides the lowest latency but the weakest durability guarantees (some data will be lost when a server fails).
        //1, which means that the producer gets an acknowledgement after the leader replica has received the data. This option provides better durability as the client waits until the server acknowledges the request as successful (only messages that were written to the now-dead leader but not yet replicated will be lost).
        //-1, which means that the producer gets an acknowledgement after all in-sync replicas have received the data. This option provides the best durability, we guarantee that no messages will be lost as long as at least one in sync replica remains.
        props.put("request.required.acks","-1");
        Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(props));
        String topic = PropertyUtil.getProperty("pathology.kafka.consumer.topic");
        producer.send(new KeyedMessage<String, String>(topic, "mkey" ,param));

        Map<String, Object> response = new HashMap<>();
        response.put("id", IdGenerate.uuid());
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/getKafkaRedisSize", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getKafkaRedisSize(){
        int size = CacheUtils.size(InitializingSystemThread.kafkaCache);
        return new ResponseEntity<Object>(size, HttpStatus.OK);
    }

    @RequestMapping(value = "/autoLabel/progress", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> progress(String id) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        KafkaMessage kafkaMessage = (KafkaMessage) CacheUtils.get(InitializingSystemThread.kafkaCache,id);
        JSONObject progressedObject = kafkaMessage!=null? new JSONObject(kafkaMessage.getMessage()) :null;
        String result = null;
        if(progressedObject != null){
            String jobId = progressedObject.getString("job_id");
            int isFinished = progressedObject.getInt("is_finished");
            if(isFinished == 1){
                deleteProgressCacheInRedis(id);
//                CacheUtils.remove(InitializingSystemThread.kafkaCache,id);
            }
            result = progressedObject.toString();
        }
        System.out.println("progress reques：/autoLabel/progress 从redis host:"+ PropertyUtil.getProperty("redis.host")+" 端口:"+ PropertyUtil.getProperty("redis.port")+" 获取的结果是： "+result);
        builder.add("message",result);
        return builder.build();
    }

    public void deleteProgressCacheInRedis(String jobId){
        CacheUtils.remove(InitializingSystemThread.kafkaCache,jobId);
    }

    Map<String,ScheduledFuture> scheduledTaskMap = new ConcurrentHashMap<>();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

    @RequestMapping(value = "/manualPrediction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> manualPrediction(@RequestParam List<String> imageIdList, @RequestParam(defaultValue = "true",required = false) Boolean saveImage) throws Exception {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<Map> success = new ArrayList<>();
        List<Map> failure = new ArrayList<>();
        int successNum = 0;
        int failureNum = 0;
        for (String imageId:imageIdList){
            String path;
            Double topLeftX;
            Double topLeftY;
            Double bottomRightX;
            Double bottomRightY;
            Double width;
            Double height;
            String jobType;
            String diseaseId;

            //切片路径
            Image image = imageService.selectByPrimaryKey(imageId);
            if (image != null){
                path = image.getPath();
                diseaseId = image.getDiseaseId();
                jobType = "1";//为1时，不用传高度和宽度

                //切片宽度和高度
//              CacheImage cacheImage = cacheImageHolder.getCacheImage(imageId);
                width = 0d;
//                    Double.parseDouble(cacheImage.getWidth()+"");
                height = 0d;
//                    Double.parseDouble(cacheImage.getHeight()+"");
                topLeftX = 0d;
                topLeftY = 0d;
                bottomRightX = width;
                bottomRightY = height;

                //如果之前已经预测过，删除标注
                if (image.getAiPredict() == 1){
                    CommonExample example = new CommonExample(Label.class);
                    example.createCriteria().andEqualTo(Label.getFieldImageId(),imageId);
                    labelService.deleteByExample(example);
                }
                //如果是重新预测，删除之前的预测任务
                String preJobId = image.getJobId();
                if (StringUtils.isNotBlank(preJobId)){
                    cancelPrediction(preJobId,0);
                }

                //发起预测
                ResponseEntity<?> responseEntity = autoLabel(path, topLeftX, topLeftY, (Double) bottomRightX, (Double) bottomRightY, (Double) width, (Double) height, imageId, jobType, diseaseId);
                ResponseWrapper body = (ResponseWrapper) responseEntity.getBody();
                boolean successSend = body.isSuccess();
                if (successSend){
                    successNum ++;
                    String jobId = (String) body.getData().get("id");
                    if (saveImage){
                        Image image1 = new Image();
                        image1.setId(imageId);
                        image1.setJobId(jobId);
                        image1.setPredictStartDate(new Date());
                        imageService.updateByPrimaryKeySelective(image1);
                    }
                    Runnable scheduledTask = new ScheduledPredictionTask(jobId,imageId,null);
                    ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(scheduledTask,0,1,TimeUnit.SECONDS);
                    scheduledTaskMap.put(jobId,scheduledFuture);
                    Map successMap = new HashMap();
                    successMap.put("jobId",jobId);
                    successMap.put("imageId",image.getId());
                    success.add(successMap);
                }else {
                    failureNum ++;
                    Map failureMap = new HashMap();
                    failureMap.put("jobId",null);
                    failureMap.put("imageId",image.getId());
                    failure.add(failureMap);
                    builder.error();
                    builder.message(body.getMessage());
                }
            }
        }
        builder.add("successID",success);
        builder.add("failureID",failure);
        String message = "没有预测数据！";
        if (successNum > 0){
            message = "成功发起"+successNum+"个预测任务！";
        }
        if (failureNum > 0){
            message += failureNum+"个任务预测发起失败";
        }
        builder.message(message);
        return builder.build();
    }

    @RequestMapping(value = "/predictionProgress")
    public ResponseEntity<?> predictionProgress(@RequestParam String imageId) throws Exception {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        Image image = imageService.selectByPrimaryKey(imageId);
        int progress = image.getProgress();
        builder.add("imageId",imageId);
        builder.add("progress",progress);
        return builder.build();
    }

    class ScheduledPredictionTask implements Runnable{
        String jobId;
        String imageId;
        String diseaseId;

        public ScheduledPredictionTask(String jobId,String imageId,String diseaseId){
            this.jobId = jobId;
            this.imageId = imageId;
            this.diseaseId = diseaseId;
        }

        @Override
        public void run() {
            try {
                ResponseEntity<?> responseEntity = progress(jobId);
                ResponseWrapper body = (ResponseWrapper) responseEntity.getBody();
                if (body.isSuccess()){
                    String result = (String) body.getData().get("message");
                    if (StringUtils.isNotBlank(result)){
                        JSONObject jsonObject = new JSONObject(result);
                        int isFinished = (Integer) jsonObject.get("is_finished");
                        if (isFinished == 1){
                            try {
                                logger.info(imageId+" prediction success！ ");
                                System.out.println(imageId+" prediction success！ ");
                                LabelsForm labelsForm = new LabelsForm();
                                labelsForm.setImageId(imageId);
                                labelsForm.setDiseaseId(diseaseId);
                                //解析标注
                                JSONArray finalResult = (JSONArray) jsonObject.get("result");
                                List<LabelForm> labelList = getListByArray(finalResult);
                                labelsForm.setLabels(labelList);
                                //添加事务
                                labelService.saveMultiStampsAndUpdateAiPredict(true,labelsForm,jobId,imageId,1);
                                //通过抛出异常暂停定时任务
                            }catch (Exception e){
                            }finally {
                                //移除任务引用
                                scheduledTaskMap.remove(jobId);
                                throw new ScheduledPredictionTaskException("The prediction process is successful,throw the ScheduledPredictionTaskException to stop the ScheduledPredictionTask normally");
                            }

                        }else {
                            //任务总个数
                            int jobTaskNum = (int) jsonObject.get("job_task_num");
                            //当前已经完成的任务总个数
                            int taskFinishedNum = (int) jsonObject.get("task_finished_num");
                            double process = taskFinishedNum/(double)jobTaskNum;
                            labelService.saveMultiStampsAndUpdateAiPredict(false,null,jobId,imageId,process);
                        }
                    }else {
                        System.out.println(jobId+": result is null!");
                    }
                }else {
                    System.out.println("imageId:"+imageId+",body.isSuccess() is false,maybe an exception occurred during get the prediction process: "+body.getMessage()+",however the run method continues to execute!");
                    logger.info("imageId:"+imageId+",body.isSuccess() is false,maybe an exception occurred during get the prediction process: "+body.getMessage()+",however the run method continues to execute!");
                }
            }catch (Exception e){
                if (e instanceof ScheduledPredictionTaskException){
                    logger.info("imageId:"+imageId+",the prediction process is successful,"+e.getMessage()+",exit the run method");
                    throw new ScheduledPredictionTaskException(e.getLocalizedMessage());
                }else {
                    System.out.println(imageId+" prediction failure！ ");
                    logger.error(imageId+" prediction failure！exit the run method,scheduledPredictionTask exception occurred :",e);
                }
            }
        }
    }


    public static  List<LabelForm> getListByArray(JSONArray jsonArray) throws IOException {
        List<LabelForm> list = new ArrayList<>();
        if (jsonArray==null || jsonArray.length() == 0) {
            return list;
        }
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            ObjectMapper mapper = new ObjectMapper();
            LabelForm t = mapper.readValue(jsonObject.toString(), LabelForm.class);
            list.add(t);
        }
        return list;
    }

    @RequestMapping(value = "/cancelPrediction")
    public ResponseEntity<?> cancelPrediction(@RequestParam String jobId, @RequestParam(defaultValue = "0",required = false) Integer account){
        //用Scheduled实现的方式
        //移除任务引用
        ScheduledFuture scheduledFuture = scheduledTaskMap.get(jobId);
        if (scheduledFuture != null){
            //取消任务
            boolean success = scheduledFuture.cancel(true);
            if (success){
                scheduledTaskMap.remove(jobId);
                return cancelAutoLabel(jobId,Constant.KAFKA_TOPIC_CANCEL,null);
            }else if (account < 3){
                //重新尝试取消
                account++;
                return cancelPrediction(jobId,account);
            }else {
                ResponseBuilder builder =  ResponseBuilder.newInstance();
                builder.error();
                builder.message("三次尝试取消预测后失败");
                return builder.build();
            }
        }else {
            return cancelAutoLabel(jobId, Constant.KAFKA_TOPIC_CANCEL,null);
        }


    }

    @Override
    public void destroy() throws Exception {
        //用Scheduled实现的方式
        Set<Map.Entry<String, ScheduledFuture>> entrySet = scheduledTaskMap.entrySet();
        if (entrySet != null){
            Iterator iterator = entrySet.iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                ScheduledFuture future = (ScheduledFuture) entry.getValue();
                future.cancel(true);
            }
        }
    }

}
