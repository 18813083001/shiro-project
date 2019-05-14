package com.thorough.core.modules.pathology.model.vo;


import java.util.List;

public class OwnershipTranserParamVo {
    List<ImageIdUserIdVo> imageIdUserIdVos;
    String transferUserId;

    public List<ImageIdUserIdVo> getImageIdUserIdVos() {
        return imageIdUserIdVos;
    }

    public void setImageIdUserIdVos(List<ImageIdUserIdVo> imageIdUserIdVos) {
        this.imageIdUserIdVos = imageIdUserIdVos;
    }

    public String getTransferUserId() {
        return transferUserId;
    }

    public void setTransferUserId(String transferUserId) {
        this.transferUserId = transferUserId;
    }

}

