package com.thorough.core.modules.pathology.model.entity;

import com.thorough.library.mybatis.persistence.model.entity.IdUserDateDelFlagEntity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "pathology_image_user_comment")
public class ImageUserComment extends IdUserDateDelFlagEntity<String> {

    @Column(name = "image_id")
    private String imageId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "comment")
    private String comment;


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

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public static String getFieldImageId(){
        return "imageId";
    }

}