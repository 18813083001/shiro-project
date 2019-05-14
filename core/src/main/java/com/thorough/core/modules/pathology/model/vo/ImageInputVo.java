package com.thorough.core.modules.pathology.model.vo;


import java.io.Serializable;
import java.util.List;

public class ImageInputVo implements Serializable{

    private Integer allocation;
    private List<ImageVo> inputImageList;

    public Integer getAllocation() {
        return allocation;
    }

    public void setAllocation(Integer allocation) {
        this.allocation = allocation;
    }

    public List<ImageVo> getInputImageList() {
        return inputImageList;
    }

    public void setInputImageList(List<ImageVo> inputImageList) {
        this.inputImageList = inputImageList;
    }
}
