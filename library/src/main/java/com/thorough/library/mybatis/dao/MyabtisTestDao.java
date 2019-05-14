package com.thorough.library.mybatis.dao;

import com.thorough.library.mybatis.persistence.model.dao.Dao;
import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;

import java.util.List;

//@MyBatisDao
public interface MyabtisTestDao extends Dao {
    public List<MybatisTest> get();
}
