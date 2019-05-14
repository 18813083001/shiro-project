package com.thorough.core.modules.pathology.model.entity;


import com.thorough.library.mybatis.persistence.model.entity.IdUserDateDelFlagRemarksNameEntity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "pathology_label")
public class Label extends IdUserDateDelFlagRemarksNameEntity<String> {

    @Column(name = "guid")
    private String guid;

    @Column(name = "shape_type")
    private String shapeType;

    @Column(name = "shape_name")
    private String shapeName;

    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "disease_id")
    private String diseaseId;

    @Column(name = "image_id")
    private String imageId;

    @Column(name = "result")
    private String result;

    @Column(name = "description")
    private String description;

    @Column(name = "marker_type")
    private Integer markerType;

    @Column(name = "reviewer_id")
    private String reviewerId;

    @Column(name = "review_status_doctor")
    private Integer reviewStatusDoctor;

    @Column(name = "review_status")
    private Byte reviewStatus;

    @Column(name = "review_status_expert")
    private Integer reviewStatusExpert;

    @Column(name = "review_status_adviser")
    private Integer reviewStatusAdviser;

    @Column(name = "review_status_backup1")
    private Integer reviewStatusBackup1;

    @Column(name = "review_status_backup2")
    private Integer reviewStatusBackup2;

    @Column(name = "content")
    private String content;

    @Override
    public void preInsert() {
        super.preInsert();
        if (reviewStatusDoctor == null)
            this.reviewStatusDoctor = 0;
        if (reviewStatus == null)
            this.reviewStatus = 0;
        if (reviewStatusExpert == null)
            this.reviewStatusExpert = 0;
        if (reviewStatusAdviser == null)
            this.reviewStatusAdviser = 0;
        if (reviewStatusBackup1 == null)
            this.reviewStatusBackup1 = 0;
        if (reviewStatusBackup2 == null)
            this.reviewStatusBackup2 = 0;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getShapeType() {
        return shapeType;
    }

    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
    }

    public String getShapeName() {
        return shapeName;
    }

    public void setShapeName(String shapeName) {
        this.shapeName = shapeName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMarkerType() {
        return markerType;
    }

    public void setMarkerType(Integer markerType) {
        this.markerType = markerType;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Byte getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Byte reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getReviewStatusDoctor() {
        return reviewStatusDoctor;
    }

    public void setReviewStatusDoctor(Integer reviewStatusDoctor) {
        this.reviewStatusDoctor = reviewStatusDoctor;
    }

    public Integer getReviewStatusExpert() {
        return reviewStatusExpert;
    }

    public void setReviewStatusExpert(Integer reviewStatusExpert) {
        this.reviewStatusExpert = reviewStatusExpert;
    }

    public Integer getReviewStatusAdviser() {
        return reviewStatusAdviser;
    }

    public void setReviewStatusAdviser(Integer reviewStatusAdviser) {
        this.reviewStatusAdviser = reviewStatusAdviser;
    }

    public Integer getReviewStatusBackup1() {
        return reviewStatusBackup1;
    }

    public void setReviewStatusBackup1(Integer reviewStatusBackup1) {
        this.reviewStatusBackup1 = reviewStatusBackup1;
    }

    public Integer getReviewStatusBackup2() {
        return reviewStatusBackup2;
    }

    public void setReviewStatusBackup2(Integer reviewStatusBackup2) {
        this.reviewStatusBackup2 = reviewStatusBackup2;
    }

    public static String getFieldImageId(){
        return "imageId";
    }

    public static String getFieldGuid(){
        return "guid";
    }


}