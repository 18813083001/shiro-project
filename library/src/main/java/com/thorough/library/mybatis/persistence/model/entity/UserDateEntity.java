package com.thorough.library.mybatis.persistence.model.entity;


import javax.persistence.Column;
import java.util.Date;


public class UserDateEntity extends CommonEntity{

    @Column(name = "create_by")
    protected String createBy;    // 创建者

    @Column(name = "create_date")
    protected Date createDate;    // 创建日期

    @Column(name = "update_by")
    protected String updateBy;    // 更新者

    @Column(name = "update_date")
    protected Date updateDate;    // 更新日期

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

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public static String getFieldCreateBy() {
        return "createBy";
    }

    public static String getFieldCreateDate() {
        return "createDate";
    }

    public static String getFieldUpdateBy() {
        return "updateBy";
    }

    public static String getFieldUpdateDate() {
        return "updateDate";
    }


    /**
     * 插入之前执行方法，需要手动调用
     */
    @Override
    public void preInsert(){
        this.updateDate = new Date();
        this.createDate = this.updateDate;
    }

    /**
     * 更新之前执行方法，需要手动调用
     */
    @Override
    public void preUpdate(){
        this.updateDate = new Date();
    }
}
