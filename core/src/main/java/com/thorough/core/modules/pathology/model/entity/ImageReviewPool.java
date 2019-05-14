package com.thorough.core.modules.pathology.model.entity;

import com.thorough.library.mybatis.persistence.Page;
import com.thorough.library.mybatis.persistence.Pageable;
import com.thorough.library.mybatis.persistence.annotation.Primary;
import com.thorough.library.mybatis.persistence.model.entity.UserDateDelFlagEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.List;

@Table(name = "pathology_image_review_pool")
public class ImageReviewPool extends UserDateDelFlagEntity<String> implements Pageable<ImageReviewPool> {

    @Primary(name ="image_id")
    private String imageId;

    @Column(name = "review_stage")
    private Integer reviewStage;

    @Column(name = "hospital_id")
    private String hospitalId;

    @Column(name = "department_id")
    private String departmentId;

    @Column(name = "disease_id")
    private String diseaseId;

    private List<String> diseaseIdList;

    private String diseaseName;

    private String hospitalName;

    /*创建人*/
    private String userName;

    /*
    * 用来查询的
    * */
    private String name;

    private Page<ImageReviewPool> page;

    @Override
    public void preInsert(){
        super.preInsert();
        if (this.reviewStage == null)
            this.reviewStage=10;
    }


    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId == null ? null : imageId.trim();
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

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public List<String> getDiseaseIdList() {
        return diseaseIdList;
    }

    public void setDiseaseIdList(List<String> diseaseIdList) {
        this.diseaseIdList = diseaseIdList;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getFieldHospitalId(){
        return "hospitalId";
    }

    public static String getFieldImageId(){
        return "imageId";
    }

    public static String getFieldDiseaseId(){
        return "diseaseId";
    }

    @Override
    public Page<ImageReviewPool> getPage() {
        return page;
    }


    @Override
    public Page<ImageReviewPool> setPage(Page<ImageReviewPool> page) {
        this.page = page;
        return page;
    }
}