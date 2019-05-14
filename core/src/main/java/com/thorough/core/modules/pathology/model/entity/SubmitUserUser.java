package com.thorough.core.modules.pathology.model.entity;


import com.thorough.library.mybatis.persistence.model.entity.IdUserDateDelFlagEntity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "pathology_user_user")
public class SubmitUserUser extends IdUserDateDelFlagEntity<String> {

    @Column(name = "submit_user_id")
    private String submitUserId;

    @Column(name = "review_user_id")
    private String reviewUserId;


    public String getSubmitUserId() {
        return submitUserId;
    }

    public void setSubmitUserId(String submitUserId) {
        this.submitUserId = submitUserId == null ? null : submitUserId.trim();
    }

    public String getReviewUserId() {
        return reviewUserId;
    }

    public void setReviewUserId(String reviewUserId) {
        this.reviewUserId = reviewUserId == null ? null : reviewUserId.trim();
    }

    public static String getFieldSubmitUserId(){
        return "submitUserId";
    }

    public static String getFieldReviewUserId(){
        return "reviewUserId";
    }
}