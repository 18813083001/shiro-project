package com.thorough.library.system.model.dao;


import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.CommonDao;
import com.thorough.library.system.model.entity.FrontMenu;

@MyBatisDao
public interface FrontMenuDao extends CommonDao<String,FrontMenu> {

}