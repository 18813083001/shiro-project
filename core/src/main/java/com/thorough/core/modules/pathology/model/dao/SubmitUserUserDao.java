package com.thorough.core.modules.pathology.model.dao;

import com.thorough.core.modules.pathology.model.entity.SubmitUserUser;
import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.CommonDao;

@MyBatisDao
public interface SubmitUserUserDao extends CommonDao<String,SubmitUserUser> {

}