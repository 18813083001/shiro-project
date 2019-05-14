package com.thorough.core.modules.pathology.model.entity;

import com.thorough.library.mybatis.persistence.annotation.Primary;
import com.thorough.library.mybatis.persistence.model.entity.CommonEntity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "sys_user_brush")
public class UserBrush extends CommonEntity<String> {

    @Primary(name = "user_id")
    private String userId;

    @Column(name = "brush_size")
    private Integer brushSize;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getBrushSize() {
        return brushSize;
    }

    public void setBrushSize(Integer brushSize) {
        this.brushSize = brushSize;
    }

    @Override
    public void preInsert() {

    }

    @Override
    public void preUpdate() {

    }
}
