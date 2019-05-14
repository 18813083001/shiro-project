
package com.thorough.library.system.service;

import com.thorough.library.constant.Constant;
import com.thorough.library.mybatis.persistence.Page;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.shiro.session.RedisSessionDAO;
import com.thorough.library.system.exception.LibraryException;
import com.thorough.library.system.model.dao.*;
import com.thorough.library.system.model.entity.*;
import com.thorough.library.system.utils.CacheUtils;
import com.thorough.library.system.utils.LogUtils;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 */
@Service
@Transactional(readOnly = true)
public class SystemService implements InitializingBean {


	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private RedisSessionDAO redisSessionDAO;
	@Autowired
	private RoleFrontMenuDao roleFrontMenuDao;
	@Autowired
	private FrontMenuDao frontMenuDao;

	public SessionDAO getSessionDao() {
		return redisSessionDAO;
	}

//	@Autowired
//	private IdentityService identityService;

	//-- User Service --//

	/**
	 * 获取用户
	 * @param id
	 * @return
	 */
	public User getUser(String id) {
		return UserUtils.get(id);
	}

	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return
	 */
	public User getUserByLoginName(String loginName) {
		return UserUtils.getByLoginName(loginName);
	}

	public Page<User> findUser(Page<User> page, User user) {
		// 设置分页参数
		user.setPage(page);
		// 执行分页查询
		page.setList(userDao.findList(user));
		return page;
	}

	/**
	 * 无分页查询人员列表
	 * @param user
	 * @return
	 */
	public List<User> findUser(User user){
		List<User> list = userDao.findList(user);
		return list;
	}

	/**
	 * 通过部门ID获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param officeId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> findUserByOfficeId(String officeId) {
		User user = new User();
		user.setOffice(new Office(officeId));
		List<User> list = userDao.findUserByOfficeId(user);
		return list;
	}

	/**
	 * 通过医院ID获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> findUserByCompanyId(String companyId) {
		User user = new User();
		user.setOffice(new Office(companyId));
		List<User> list = userDao.findUserByCompanyId(user);
		return list;
	}

	/**
	 * 通过医院ID获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param companyId
	 * @param userType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> findUserByCompanyIdAndUserType(String companyId, String userType) {
		User user = new User();
		user.setOffice(new Office(companyId));
		user.setUserType(userType);
		List<User> list = userDao.findUserByCompanyId(user);
		return list;
	}

	public List<String> getUserIdByCompanyIdAndUserType(String companyId, String userType) {
		List<User> list = findUserByCompanyIdAndUserType(companyId,userType);
		List<String> userIdList = new ArrayList<>();
		if (list != null){
			for (User user : list)
				userIdList.add(user.getId());
		}
		return userIdList;
	}

	@Transactional(readOnly = false)
	public void saveUser(User user) {
		if (StringUtils.isBlank(user.getId())){
			user.preInsert();
			userDao.insert(user);
		}else{
			// 更新用户数据
			user.preUpdate();
			userDao.update(user);
		}
		if (StringUtils.isNotBlank(user.getId())){
			if (user.getRoleList() != null && user.getRoleList().size() > 0){
				// 更新用户与角色关联
				userDao.deleteUserRole(user);
				userDao.insertUserRole(user);
			}else{
				throw new LibraryException(user.getLoginName() + "没有设置角色！");
			}

			// 清除用户缓存
			UserUtils.clearCache(user);
		}
	}

	@Transactional(readOnly = false)
	public void updateUserInfo(User user) {
		user.preUpdate();
		userDao.updateUserInfo(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void deleteUser(User user) {
		userDao.delete(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void updatePasswordById(String id, String loginName, String newPassword) {
		User user = new User(id);
		user.setPassword(entryptPassword(newPassword));
		userDao.updatePasswordById(user);
		// 清除用户缓存
		user.setLoginName(loginName);
		UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void updateUserLoginInfo(User user) {
		// 保存上次登录信息
		user.setOldLoginIp(user.getLoginIp());
		user.setOldLoginDate(user.getLoginDate());
		// 更新本次登录信息
		user.setLoginIp(StringUtils.getRemoteAddr(Servlets.getRequest()));
		user.setLoginDate(new Date());
		userDao.updateLoginInfo(user);
	}

	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		String plain = Encodes.unescapeHtml(plainPassword);
		byte[] salt = Digests.generateSalt(Constant.SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, Constant.HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+ Encodes.encodeHex(hashPassword);
	}

	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		String plain = Encodes.unescapeHtml(plainPassword);
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, Constant.HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+ Encodes.encodeHex(hashPassword));
	}

	/**
	 * 获得活动会话
	 * @return
	 */
	public Collection<Session> getActiveSessions(){
		return redisSessionDAO.getActiveSessions(false);
	}

	//-- Role Service --//

	public Role getRole(String id) {
		return roleDao.get(id);
	}

	public Role getRoleByName(String name) {
		Role r = new Role();
		r.setName(name);
		return roleDao.getByName(r);
	}

	public Role getRoleByEnname(String enname) {
		Role r = new Role();
		r.setEnname(enname);
		return roleDao.getByEnname(r);
	}

	public List<Role> findRole(Role role){
		return roleDao.findList(role);
	}

	public List<Role> findAllRole(){
		return UserUtils.getRoleList();
	}

	@Transactional(readOnly = false)
	public void saveRole(Role role) {
		if (StringUtils.isBlank(role.getId())){
			role.preInsert();
			roleDao.insert(role);
		}else{
			role.preUpdate();
			roleDao.update(role);
		}
		// 更新角色与菜单关联
		roleDao.deleteRoleMenu(role);
		if (role.getMenuList().size() > 0){
			roleDao.insertRoleMenu(role);
		}
		// 更新角色与部门关联
		roleDao.deleteRoleOffice(role);
		if (role.getOfficeList().size() > 0){
			roleDao.insertRoleOffice(role);
		}
//		// 更新角色与配置项关联
//		roleDiseaseDao.deleteRoleDisease(role.getId());
//		if (role.getDiseaseIdList().size() > 0){
//			Map<String,Object> map = new HashMap<>();
//			map.put("roleId",role.getId());
//			map.put("diseaseList",role.getDiseaseIdList());
//			roleDiseaseDao.insertRoleDisease(map);
//		}
		// 更新角色与前端菜单
		roleFrontMenuDao.deleteRoleFrontMenu(role);
		if (role.getFrontMenuIdList()!=null && role.getFrontMenuIdList().size() > 0){
			roleFrontMenuDao.insertRoleFrontMenu(role);
		}

		// 清除拥有该角色的用户缓存
		UserUtils.removeCache(Constant.CACHE_ROLE_LIST);
	}

	@Transactional(readOnly = false)
	public void deleteRole(Role role) {
		roleDao.delete(role);
		// 清除用户角色缓存
		UserUtils.removeCache(Constant.CACHE_ROLE_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public Boolean outUserInRole(Role role, User user) {
		List<Role> roles = user.getRoleList();
		for (Role e : roles){
			if (e.getId().equals(role.getId())){
				roles.remove(e);
				saveUser(user);
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = false)
	public User assignUserToRole(Role role, User user) {
		if (user == null){
			return null;
		}
		List<String> roleIds = user.getRoleIdList();
		if (roleIds.contains(role.getId())) {
			return null;
		}
		user.getRoleList().add(role);
		saveUser(user);
		return user;
	}


	//-- Menu Service --//

	public Menu getMenu(String id) {
		return menuDao.get(id);
	}

	public List<Menu> findAllMenu(){
		return UserUtils.getMenuList();
	}

	public List<FrontMenu> getAllFrontMenu(){
		return UserUtils.getFrontMenuList();
	}

	@Transactional(readOnly = false)
	public void saveMenu(Menu menu) {

		// 获取父节点实体
		menu.setParent(this.getMenu(menu.getParent().getId()));

		// 获取修改前的parentIds，用于更新子节点的parentIds
		String oldParentIds = menu.getParentIds();

		// 设置新的父节点串
		menu.setParentIds(menu.getParent().getParentIds()+menu.getParent().getId()+",");

		// 保存或更新实体
		if (StringUtils.isBlank(menu.getId())){
			menu.preInsert();
			menuDao.insert(menu);
		}else{
			menu.preUpdate();
			menuDao.update(menu);
		}

		// 更新子节点 parentIds
		Menu m = new Menu();
		m.setParentIds("%,"+menu.getId()+",%");
		List<Menu> list = menuDao.findByParentIdsLike(m);
		for (Menu e : list){
			e.setParentIds(e.getParentIds().replace(oldParentIds, menu.getParentIds()));
			menuDao.updateParentIds(e);
		}
		// 清除用户菜单缓存
		UserUtils.removeCache(Constant.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
		// 清除日志相关缓存
		CacheUtils.remove(Constant.CACHE_MENU_NAME_PATH_MAP);
	}

	@Transactional(readOnly = false)
	public void updateMenuSort(Menu menu) {
		menuDao.updateSort(menu);
		// 清除用户菜单缓存
		UserUtils.removeCache(Constant.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
		// 清除日志相关缓存
		CacheUtils.remove(Constant.CACHE_MENU_NAME_PATH_MAP);
	}

	@Transactional(readOnly = false)
	public void deleteMenu(Menu menu) {
		menuDao.delete(menu);
		// 清除用户菜单缓存
		UserUtils.removeCache(Constant.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
		// 清除日志相关缓存
		CacheUtils.remove(Constant.CACHE_MENU_NAME_PATH_MAP);
	}

	@Transactional(readOnly = false)
	public void deleteFrontMenu(FrontMenu frontMenu){
		CommonExample example = new CommonExample(FrontMenu.class);
		example.createCriteria().andEqualTo(FrontMenu.getFieldId(),frontMenu.getId());
		example.or().andLike(FrontMenu.getFiledsParentIds(),"%,"+frontMenu.getId()+",%");
		frontMenuDao.deleteByExample(example);
		UserUtils.removeCache(Constant.CACHE_FRONT_MENU_LIST);
	}


	/**
	 * 获取Key加载信息
	 */
	public static boolean printKeyLoadMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n======================================================================\r\n");
		sb.append("\r\n    欢迎使用 "+ PropertyUtil.getProperty("productName")+"  \r\n");
		sb.append("\r\n======================================================================\r\n");
		System.out.println(sb.toString());
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	///////////////// Synchronized to the Activiti //////////////////


	///////////////// Synchronized to the Activiti end //////////////////

}
