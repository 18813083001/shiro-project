package com.thorough.core.modules.pathology.model.vo;


import java.util.List;

public class SwitchAllocationVo{

    private Integer allocation;
    private List<String> imageIdList;

    public Integer getAllocation() {
        return allocation;
    }

    public void setAllocation(Integer allocation) {
        this.allocation = allocation;
    }

    public List<String> getImageIdList() {
        return imageIdList;
    }

    public void setImageIdList(List<String> imageIdList) {
        this.imageIdList = imageIdList;
    }
}
