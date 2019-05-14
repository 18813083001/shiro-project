
package com.thorough.library.system.model.dao;

import com.thorough.library.mybatis.persistence.model.dao.CrudDao;
import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.system.model.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户DAO接口
 */
@MyBatisDao
public interface UserDao extends CrudDao<String,User> {
	
	/**
	 * 根据登录名称查询用户
	 * @param user
	 * @return
	 */
	public User getByLoginName(User user);

	/**
	 * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<User> findUserByOfficeId(User user);

	/**
	 * 通过CompanyId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<User> findUserByCompanyId(User user);
	
	/**
	 * 查询全部用户数目
	 * @return
	 */
	public long findAllCount(User user);
	
	/**
	 * 更新用户密码
	 * @param user
	 * @return
	 */
	public int updatePasswordById(User user);
	
	/**
	 * 更新登录信息，如：登录IP、登录时间
	 * @param user
	 * @return
	 */
	public int updateLoginInfo(User user);

	/**
	 * 删除用户角色关联数据
	 * @param user
	 * @return
	 */
	public int deleteUserRole(User user);
	
	/**
	 * 插入用户角色关联数据
	 * @param user
	 * @return
	 */
	public int insertUserRole(User user);
	
	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateUserInfo(User user);

	List<String> getAllUserId();

	/**
	 * 获取用户名称
	 * @param set
	 * @return
	 */
	public List<Map<String,String>> getUserNameListByUserIdList(@Param("set") Collection<String> set);
	/**
	 * 获取用户名称
	 * @param userIds
	 * @return
	 */
	public List<Map<String,String>> getUserIdAndNameExceptUserId(@Param("userIds") List<String> userIds);

	/**
	 * 根据hospitalId找到用户，如果hospitalId为空，则返回所有用户
	 * */
	public List<Map<String,String>> getUserIdNameTypeByHospitalId(@Param("hospitalId") String hospitalId,@Param("userType")String userType);


}
