package com.thorough.core.modules.pathology.model.entity;


import com.thorough.library.mybatis.persistence.model.entity.UserDateDelFlagEntity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "pathology_image_user_overdue")
public class ImageUserOverdue extends UserDateDelFlagEntity<String> {

    @Column(name = "image_id")
    private String imageId;

    @Column(name = "user_id")
    private String userId;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId == null ? null : imageId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public static String getFieldImageId(){
        return "imageId";
    }

    public static String getFieldUserId(){
        return "userId";
    }

}