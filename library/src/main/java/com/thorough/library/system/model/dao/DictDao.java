
package com.thorough.library.system.model.dao;



import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.CrudDao;
import com.thorough.library.system.model.entity.Dict;

import java.util.List;

/**
 * 字典DAO接口
 */
@MyBatisDao
public interface DictDao extends CrudDao<String,Dict> {

	public List<String> findTypeList(Dict dict);
	
}
