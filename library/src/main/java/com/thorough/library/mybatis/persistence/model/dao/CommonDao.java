
package com.thorough.library.mybatis.persistence.model.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 常用dao
 */
public interface CommonDao<P,T> extends Dao{
    long countByExample(CommonExample example);

    long deleteByExample(CommonExample example);

    int deleteByPrimaryKey(P id);

    int insert(Object object);

    int insertSelective(Object record);

    List<T> selectByExample(CommonExample example);

    T selectByPrimaryKey(P id);

    int updateByExampleSelective(@Param("record") Object record, @Param("example") CommonExample example);

    int updateByExample(@Param("record") Object record, @Param("example") CommonExample example);

    int updateByPrimaryKeySelective(Object record);

    int updateByPrimaryKey(Object record);
}