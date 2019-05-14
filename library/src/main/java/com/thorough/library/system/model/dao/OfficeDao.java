
package com.thorough.library.system.model.dao;

import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.TreeDao;
import com.thorough.library.system.model.entity.Office;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 机构DAO接口
 */
@MyBatisDao
public interface OfficeDao extends TreeDao<String,Office> {
    List<Office> findListByType(@Param(value = "type") int type);
    List<String> getChildIdByParentId(@Param(value = "parentId") String parentId);
}
