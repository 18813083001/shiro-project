package com.thorough.core.modules.pathology.model.vo;


import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class InputImageVo implements Serializable{

    private String hospitalId;
    private Integer reviewStage;
    private String spinnerId;
    private String diseaseId;
    private String tct;
    private String path;
    private String remarks;
    private MultipartFile heFile;
//    private List<String> imageNameHeList;

    /**
     * 是否包含免疫组化 0否，1是
     * */
    private Integer ihc;
    private String hospitalIdIhc;
    private Integer reviewStageIhc;
    private String spinnerIdIhc;
    private String diseaseIdIhc;
    private String pathIhc;
    private String remarksIhc;
    private MultipartFile fileIhc;
//    private List<String> imageNameListIhc;




    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getReviewStage() {
        return reviewStage;
    }

    public void setReviewStage(Integer reviewStage) {
        this.reviewStage = reviewStage;
    }

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getTct() {
        return tct;
    }

    public void setTct(String tct) {
        this.tct = tct;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

//    public List<String> getImageNameHeList() {
//        return imageNameHeList;
//    }
//
//    public void setImageNameHeList(List<String> imageNameHeList) {
//        this.imageNameHeList = imageNameHeList;
//    }


    public String getSpinnerId() {
        return spinnerId;
    }

    public void setSpinnerId(String spinnerId) {
        this.spinnerId = spinnerId;
    }

    public Integer getIhc() {
        return ihc;
    }

    public void setIhc(Integer ihc) {
        this.ihc = ihc;
    }

    public MultipartFile getHeFile() {
        return heFile;
    }

    public void setHeFile(MultipartFile heFile) {
        this.heFile = heFile;
    }

    public MultipartFile getFileIhc() {
        return fileIhc;
    }

    public void setFileIhc(MultipartFile fileIhc) {
        this.fileIhc = fileIhc;
    }

//    public List<String> getImageNameListIhc() {
//        return imageNameListIhc;
//    }
//
//    public void setImageNameListIhc(List<String> imageNameListIhc) {
//        this.imageNameListIhc = imageNameListIhc;
//    }

    public String getHospitalIdIhc() {
        return hospitalIdIhc;
    }

    public void setHospitalIdIhc(String hospitalIdIhc) {
        this.hospitalIdIhc = hospitalIdIhc;
    }

    public Integer getReviewStageIhc() {
        return reviewStageIhc;
    }

    public void setReviewStageIhc(Integer reviewStageIhc) {
        this.reviewStageIhc = reviewStageIhc;
    }

    public String getSpinnerIdIhc() {
        return spinnerIdIhc;
    }

    public void setSpinnerIdIhc(String spinnerIdIhc) {
        this.spinnerIdIhc = spinnerIdIhc;
    }

    public String getDiseaseIdIhc() {
        return diseaseIdIhc;
    }

    public void setDiseaseIdIhc(String diseaseIdIhc) {
        this.diseaseIdIhc = diseaseIdIhc;
    }

    public String getPathIhc() {
        return pathIhc;
    }

    public void setPathIhc(String pathIhc) {
        this.pathIhc = pathIhc;
    }

    public String getRemarksIhc() {
        return remarksIhc;
    }

    public void setRemarksIhc(String remarksIhc) {
        this.remarksIhc = remarksIhc;
    }
}
