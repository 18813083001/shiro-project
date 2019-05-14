package com.thorough.core.modules.pathology.model.entity;


import com.thorough.library.mybatis.persistence.model.entity.IdUserDateDelFlagRemarksNameEntity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "pathology_feedback")
public class Feedback extends IdUserDateDelFlagRemarksNameEntity<String> {

    @Column(name = "message")
    private String message;

    @Column(name = "user_name")
    private String userName;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

}