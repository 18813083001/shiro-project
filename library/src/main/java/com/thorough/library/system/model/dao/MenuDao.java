
package com.thorough.library.system.model.dao;



import com.thorough.library.mybatis.persistence.model.dao.CrudDao;
import com.thorough.library.system.model.entity.Menu;
import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;

import java.util.List;

/**
 * 菜单DAO接口
 */
@MyBatisDao
public interface MenuDao extends CrudDao<String,Menu> {

	public List<Menu> findByParentIdsLike(Menu menu);

	public List<Menu> findByUserId(Menu menu);
	
	public int updateParentIds(Menu menu);
	
	public int updateSort(Menu menu);
	
}
