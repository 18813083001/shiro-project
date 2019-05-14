
package com.thorough.library.system.model.dao;


import com.thorough.library.mybatis.persistence.model.dao.CrudDao;
import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.system.model.entity.Role;

/**
 * 角色DAO接口
 */
@MyBatisDao
public interface RoleDao extends CrudDao<String,Role> {

	public Role getByName(Role role);
	
	public Role getByEnname(Role role);

	/**
	 * 维护角色与菜单权限关系
	 * @param role
	 * @return
	 */
	public int deleteRoleMenu(Role role);

	public int insertRoleMenu(Role role);
	
	/**
	 * 维护角色与医院部门关系
	 * @param role
	 * @return
	 */
	public int deleteRoleOffice(Role role);

	public int insertRoleOffice(Role role);

}
