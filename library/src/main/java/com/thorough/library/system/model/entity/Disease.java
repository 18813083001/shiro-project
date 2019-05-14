package com.thorough.library.system.model.entity;


import com.thorough.library.mybatis.persistence.model.entity.IdUserDateDelFlagRemarksNameEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "pathology_disease")
public class Disease extends IdUserDateDelFlagRemarksNameEntity<String> {

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "parent_ids")
    private String parentIds;

    @Column(name = "rgb")
    private String rgb;

    @Column(name = "ai_value")
    private Integer aiValue;

    @Column(name = "category")
    private String category;

    @Column(name = "code")
    private String code;

    @Column(name = "sort")
    private Long sort=0L;

    @Column(name = "anchor")
    private Integer anchor;

    @Transient
    private List<Disease> childs;

    private String diseaseIds;

    @Override
    public void preInsert() {
        if (this.aiValue==null)
            this.aiValue = 0;
        if (this.anchor==null)
            this.anchor = 0;
        super.preInsert();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds == null ? null : parentIds.trim();
    }

    public String getRgb() {
        return rgb;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

    public List<Disease> getChilds() {
        return childs;
    }

    public void setChilds(List<Disease> childs) {
        this.childs = childs;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDiseaseIds() {
        return diseaseIds;
    }

    public void setDiseaseIds(String diseaseIds) {
        this.diseaseIds = diseaseIds;
    }

    public Integer getAiValue() {
        return aiValue;
    }

    public void setAiValue(Integer aiValue) {
        this.aiValue = aiValue;
    }

    public Integer getAnchor() {
        return anchor;
    }

    public void setAnchor(Integer anchor) {
        this.anchor = anchor;
    }

    public static String getFieldParentIds(){
        return "parentIds";
    }

    public static String getFieldParentId(){
        return "parentId";
    }

    public static String getFieldCategory(){
        return "category";
    }

    public static String getFieldAiValue(){
        return "aiValue";
    }

    public static String getFieldCode(){
        return "code";
    }

}