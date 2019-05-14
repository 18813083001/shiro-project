package com.thorough.core.modules.pathology.model.vo;

import java.util.List;

public class LabelsForm {
    private String imageId;
    /*
    * 多个标注更新为一种配置
    * */
    private String diseaseId;
    private List<LabelForm> labels;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public List<LabelForm> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelForm> labels) {
        this.labels = labels;
    }
}
