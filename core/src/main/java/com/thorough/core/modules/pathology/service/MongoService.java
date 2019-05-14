package com.thorough.core.modules.pathology.service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.model.entity.Label;
import com.thorough.core.modules.pathology.model.vo.LabelForm;
import com.thorough.core.modules.pathology.model.vo.LabelsForm;
import com.thorough.core.modules.sys.service.CoreDiseaseService;
import com.thorough.library.specification.service.BaseService;
import com.thorough.library.system.exception.LibraryException;
import com.thorough.library.system.model.entity.Disease;
import com.thorough.library.utils.DateUtils;
import com.thorough.library.utils.StringUtils;
import org.bson.Document;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MongoService implements BaseService {
    @Autowired
    MongoDatabase mongoDatabase;
    @Autowired
    ImageService imageService;
    @Autowired
    CoreDiseaseService coreDiseaseService;

    @Value("${mongo.collection}")
    String labelCollection;


    public Document getLabelById(String labelId){

        MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);

//        MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);

        FindIterable<Document> documents =
                collection.find( new Document("id", new Document("$eq", labelId)));

        MongoCursor mongoCursor =  documents.iterator();

        while (mongoCursor.hasNext())
        {
//            System.out.println("hh");
            Document document = (Document)
                    mongoCursor.next();
            return document;
        }
        return null;
    }

    public List<Document> getLabelsFromMongo(String imageId){
        Image image = imageService.selectByPrimaryKey(imageId);
        if (image == null)
            return null;
        List<Document> list = getAllLabelFromMongo(imageId);
        return list;
    }

    public List<Document> getAllLabelFromMongo(String imageId){
        MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);
        FindIterable<Document> documents =
                collection.find( new Document("image_id", new Document("$eq", imageId)));
        List list = new ArrayList();
        if (documents != null){
            int i= 1;
            Date date = new Date();
            System.out.println("start:"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());

            MongoCursor mongoCursor =  documents.iterator();
            while (mongoCursor.hasNext()){
                Document document = (Document)
                        mongoCursor.next();
                list.add(document);
            }
            mongoCursor.close();
            System.out.println("imageId:"+imageId+"，标注"+i+"条");
            date = new Date();
            System.out.println("finish:"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
        }
        return list;
    }


    public List<Document> getLabelsInfoFromMongo(String imageId, boolean sort){
        Image image = imageService.selectByPrimaryKey(imageId);
        if (image == null)
            return null;
        MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);
        FindIterable<Document> documents =
                collection.find( new Document("image_id", new Document("$eq", imageId)))
                        .projection(
                                new Document("name", 1)
                                .append("result",1)
                                .append("create_date", 1)
                                .append("name",1)
                                .append("update_date",1)
                                .append("name",1)
                                .append("create_by",1)
                                .append("description",1)
                                .append("patient_id",1)
                                .append("shape_type",1)
                                .append("image_id",1)
                                .append("remarks",1)
                                .append("disease_id",1)
                                .append("marker_type",1)
                                .append("update_by",1)
                                .append("del_flag",1)
                                .append("guid",1)
                                .append("id",1)
                                .append("shape_name",1));
        if (sort){
            documents.sort(new BasicDBObject("create_date",-1));
            documents.limit(1);
        }


        List list = new ArrayList();
        if (documents != null){
            int i= 1;
            Date date = new Date();
            System.out.println("start:"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());

            MongoCursor mongoCursor =  documents.iterator();
            while (mongoCursor.hasNext()){
                Document document = (Document)
                        mongoCursor.next();
                list.add(document);
            }
            mongoCursor.close();
            System.out.println("imageId:"+imageId+"，标注"+i+"条");
            date = new Date();
            System.out.println("finish:"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
        }
        return list;
    }



    public String getImageId(String labelId){
        MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);
        FindIterable<Document> documents =
                collection.find( new Document("id", new Document("$eq", labelId)))
                        .projection(
                                new Document("image_id", 1));
        MongoCursor mongoCursor =  documents.iterator();
        while (mongoCursor.hasNext()){
            Document document = (Document) mongoCursor.next();
            return (String) document.get("image_id");
        }
        return null;
    }

    public long deleteByLabelId(String labelId){
        MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);
        DeleteResult deleteResult = collection.deleteMany(new Document("id", new Document("$eq", labelId)));
        return deleteResult.getDeletedCount();
    }

    public long batchDelete(List labelIdList){
        MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);
        DeleteResult result = collection.deleteMany(Filters.in("id",labelIdList));
        return result.getDeletedCount();
    }

    public long countLabel(String labelId){
        MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);
        long count = collection.countDocuments(new Document("id", new Document("$eq", labelId)));
        return count;
    }

    public long countByImageId(String imageId){
        MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);
        long count = collection.countDocuments(new Document("image_id", new Document("$eq", imageId)));
        return count;
    }

    public void batchInsertLabel(List<Label> labelList){
        if (labelList != null && labelList.size() > 0) {
            List<Document> list = new ArrayList<>();
            for (Label label: labelList){
                Document document = new Document();
                document.put("result",label.getResult());
                document.put("content", Document.parse(label.getContent()));
                document.put("create_date", DateUtils.formatDateTime(label.getCreateDate()));
                document.put("name",label.getName());
                document.put("update_date",DateUtils.formatDateTime(label.getUpdateDate()));
                document.put("name",label.getReviewerId()!=null?label.getReviewerId():"");
                document.put("create_by",label.getCreateBy());
                document.put("description",label.getDescription());
                document.put("patient_id",label.getPatientId()!=null?label.getPatientId():"");
                document.put("shape_type",label.getShapeType());
                document.put("image_id",label.getImageId());
                document.put("remarks",label.getRemarks()!=null?label.getRemarks():"");
                document.put("disease_id",label.getDiseaseId());
                document.put("marker_type",label.getMarkerType());
                document.put("update_by",label.getUpdateBy());
                document.put("del_flag",label.getDelFlag());
                document.put("guid",label.getGuid());
                document.put("id",label.getId());
                document.put("shape_name",label.getShapeName()!=null?label.getShapeName():"");
                list.add(document);
            }
            MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);
            collection.insertMany(list);
        }
    }

    @Transactional(readOnly = false)
    public String saveSingleLabelMongo(JSONObject jsonObject){
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
            throw  new LibraryException("imageId is null");
        }
        String diseaseId;
        try {
            diseaseId = jsonObject.getString("diseaseId");
        }catch (Exception e){
            e.printStackTrace();
            throw  new LibraryException("diseaseId is null");
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
        label.preInsert();
        MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);

        Document document = new Document();
        document.put("result",label.getResult());
        document.put("content", Document.parse(label.getContent()));
        document.put("create_date", DateUtils.formatDateTime(label.getCreateDate()));
        document.put("name",label.getName());
        document.put("update_date",DateUtils.formatDateTime(label.getUpdateDate()));
        document.put("name",label.getReviewerId()!=null?label.getReviewerId():"");
        document.put("create_by",label.getCreateBy());
        document.put("description",label.getDescription());
        document.put("patient_id",label.getPatientId()!=null?label.getPatientId():"");
        document.put("shape_type",label.getShapeType());
        document.put("image_id",label.getImageId());
        document.put("remarks",label.getRemarks()!=null?label.getRemarks():"");
        document.put("disease_id",label.getDiseaseId());
        document.put("marker_type",label.getMarkerType());
        document.put("update_by",label.getUpdateBy());
        document.put("del_flag",label.getDelFlag());
        document.put("guid",label.getGuid());
        document.put("id",label.getId());
        document.put("shape_name",label.getShapeName()!=null?label.getShapeName():"");
        collection.insertOne(document);
        return label.getId();
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
            List labelIdList = new ArrayList();
            for(LabelForm form:labelList){
                if (StringUtils.isNotBlank(form.getId())){
                    labelIdList.add(form.getId());
                }
            }
            List<String> failure = new ArrayList<>();
            long count = 0;
            try {
                if (labelIdList.size() > 0){
                    MongoCollection<Document> collection = mongoDatabase.getCollection(labelCollection);
                    Document data = new Document();
                    data.append("$set", new Document().append("disease_id", diseaseId));
                    UpdateResult updateResult = collection.updateMany(Filters.in("id",labelIdList),data);
                    if (updateResult.getModifiedCount() == 0)
                        throw new Exception("更新失败");
                    count = updateResult.getModifiedCount();
                }

            }catch (Exception e){
                failure.add(e.getMessage());
            }
            map.put("success",labelIdList);
            map.put("failure",failure);
            return map;
        }
    }

    public Map<String, Object> findLastLabelConfigByImageId(String imageId){
        List<Document> list = getLabelsInfoFromMongo(imageId,true);
        Map<String,Object> config = new HashMap<>();
        if(list != null && list.size() > 0){
            Document document = list.get(0);
            String diseaseId = (String) document.get("disease_id");
            Disease disease = coreDiseaseService.selectByPrimaryKey(diseaseId);
            if (disease != null){
                config.put("diseaseId",diseaseId);
                config.put("diseaseParentId",disease.getParentId());
                config.put("diseaseParentIds",disease.getParentIds());
            }else
                System.out.println(" don`t find disease by id:"+diseaseId);
        }
        return config;
    }


}
