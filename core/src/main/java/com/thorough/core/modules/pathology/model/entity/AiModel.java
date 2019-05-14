package com.thorough.core.modules.pathology.model.entity;



import com.thorough.library.mybatis.persistence.model.entity.IdUserDateDelFlagEntity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "pathology_ai_model")
public class AiModel extends IdUserDateDelFlagEntity<String> {

    @Column(name = "diseaseId")
    private String diseaseId;

    @Column(name = "regionName")
    private String regionName;

    @Column(name = "labelTypeId")
    private String labelTypeId;

    @Column(name = "labelTypeName")
    private String labelTypeName;

    @Column(name = "model")
    private String model;

    @Column(name = "lavel")
    private Integer lavel;

    @Column(name = "version")
    private String version;

    @Column(name = "model_type")
    private String modelType;

    @Column(name = "describes")
    private String describes;

    @Column(name = "available")
    private String available;

    @Override
    public void preUpdate() {
        super.preUpdate();
    }


    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getLabelTypeId() {
        return labelTypeId;
    }

    public void setLabelTypeId(String labelTypeId) {
        this.labelTypeId = labelTypeId;
    }

    public String getLabelTypeName() {
        return labelTypeName;
    }

    public void setLabelTypeName(String labelTypeName) {
        this.labelTypeName = labelTypeName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getLavel() {
        return lavel;
    }

    public void setLavel(Integer lavel) {
        this.lavel = lavel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public static String getFieldDiseaseid(){
        return "diseaseId";
    }

    public static String getFieldAvailable(){
        return "available";
    }
}