package com.thorough.core.modules.pathology.model.vo;


import java.util.List;

public class AgainAllocationVo {

    private List<String> toUserIdList;
    private String spinnerId;
    private List<ImageUserIdVo> imageUserIdVoList;

    public List<String> getToUserIdList() {
        return toUserIdList;
    }

    public void setToUserIdList(List<String> toUserIdList) {
        this.toUserIdList = toUserIdList;
    }

    public String getSpinnerId() {
        return spinnerId;
    }

    public void setSpinnerId(String spinnerId) {
        this.spinnerId = spinnerId;
    }

    public List<ImageUserIdVo> getImageUserIdVoList() {
        return imageUserIdVoList;
    }

    public void setImageUserIdVoList(List<ImageUserIdVo> imageUserIdVoList) {
        this.imageUserIdVoList = imageUserIdVoList;
    }
}

