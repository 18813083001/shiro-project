package com.thorough.library.mybatis.dao;

import com.thorough.library.mybatis.persistence.model.entity.Entity;

import java.util.Date;

public class MybatisTest implements Entity{

    private String id;
    private Integer type;
    private String typical;
    private String title;
    private String createBy;
    private Date createDate;
    private String remoteAddr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypical() {
        return typical;
    }

    public void setTypical(String typical) {
        this.typical = typical;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }
}
