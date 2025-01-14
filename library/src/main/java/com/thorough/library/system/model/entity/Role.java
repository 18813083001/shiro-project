
package com.thorough.library.system.model.entity;

import com.google.common.collect.Lists;
import com.thorough.library.mybatis.persistence.model.entity.IdUserDateDelFlagRemarksEntity;
import com.thorough.library.utils.PropertyUtil;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;

/**
 * 角色Entity
 */
@Table(name = "sys_role")
public class Role extends IdUserDateDelFlagRemarksEntity<String> {

	private static final long serialVersionUID = 1L;
	private Office office;	// 归属机构

	@Column(name = "name")
	private String name; 	// 角色名称

	@Column(name = "enname")
	private String enname;	// 英文名称

	@Column(name = "role_type")
	private String roleType;// 权限类型

	@Column(name = "data_scope")
	private String dataScope;// 数据范围

	private String oldName; 	// 原角色名称
	private String oldEnname;	// 原英文名称

	@Column(name = "is_sys")
	private String sysData; 		//是否是系统数据`````````

	@Column(name = "useable")
	private String useable; 		//是否是可用

	private User user;		// 根据用户ID查询角色列表

	private List<Menu> menuList = Lists.newArrayList(); // 拥有菜单列表
	private List<Office> officeList = Lists.newArrayList(); // 按明细设置数据范围
	private List<FrontMenu> frontMenuList = Lists.newArrayList();

	private List<String> frontMenuIdList = Lists.newArrayList();

	// 数据范围（1：所有数据；2：所在医院及以下数据；3：所在医院数据；4：所在部门及以下数据；5：所在部门数据；8：仅本人数据；9：按明细设置）
	public static final String DATA_SCOPE_ALL = "1";
	public static final String DATA_SCOPE_COMPANY_AND_CHILD = "2";
	public static final String DATA_SCOPE_COMPANY = "3";
	public static final String DATA_SCOPE_OFFICE_AND_CHILD = "4";
	public static final String DATA_SCOPE_OFFICE = "5";
	public static final String DATA_SCOPE_SELF = "8";
	public static final String DATA_SCOPE_CUSTOM = "9";

	public Role() {
		super();
		this.dataScope = DATA_SCOPE_SELF;
		this.useable= PropertyUtil.YES;
	}

	public Role(String id){
		super(id);
	}

	public Role(User user) {
		this();
		this.user = user;
	}

	public String getUseable() {
		return useable;
	}

	public void setUseable(String useable) {
		this.useable = useable;
	}

	public String getSysData() {
		return sysData;
	}

	public void setSysData(String sysData) {
		this.sysData = sysData;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnname() {
		return enname;
	}

	public void setEnname(String enname) {
		this.enname = enname;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getDataScope() {
		return dataScope;
	}

	public void setDataScope(String dataScope) {
		this.dataScope = dataScope;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getOldEnname() {
		return oldEnname;
	}

	public void setOldEnname(String oldEnname) {
		this.oldEnname = oldEnname;
	}

	public List<Menu> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<Menu> menuList) {
		this.menuList = menuList;
	}

	public List<String> getMenuIdList() {
		List<String> menuIdList = Lists.newArrayList();
		for (Menu menu : menuList) {
			menuIdList.add(menu.getId());
		}
		return menuIdList;
	}

	public void setMenuIdList(List<String> menuIdList) {
		menuList = Lists.newArrayList();
		for (String menuId : menuIdList) {
			Menu menu = new Menu();
			menu.setId(menuId);
			menuList.add(menu);
		}
	}


	public List<FrontMenu> getFrontMenuList() {
		return frontMenuList;
	}

	public void setFrontMenuList(List<FrontMenu> frontMenuList) {
		this.frontMenuList = frontMenuList;
	}

	public List<String> getFrontMenuIdList() {
		return frontMenuIdList;
	}

	public void setFrontMenuIdList(List<FrontMenu> frontMenuList) {
		if (frontMenuList != null){
			this.frontMenuIdList = Lists.newArrayList();
			for(FrontMenu frontMenu:frontMenuList){
				String id = frontMenu.getId();
				frontMenuIdList.add(id);
			}
		}
	}

	public String getFrontMenuIds(){
		return StringUtils.join(getFrontMenuIdList(),",");
	}

	public void setFrontMenuIds(String frontMenuIds){
		frontMenuIdList = Lists.newArrayList();
		if (StringUtils.isNotBlank(frontMenuIds)){
			frontMenuIdList = Arrays.asList(frontMenuIds.split(","));
		}
	}

	public String getMenuIds() {
		return StringUtils.join(getMenuIdList(), ",");
	}

	public void setMenuIds(String menuIds) {
		menuList = Lists.newArrayList();
		if (menuIds != null){
			String[] ids = StringUtils.split(menuIds, ",");
			setMenuIdList(Lists.newArrayList(ids));
		}
	}

	public List<Office> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<Office> officeList) {
		this.officeList = officeList;
	}

	public List<String> getOfficeIdList() {
		List<String> officeIdList = Lists.newArrayList();
		for (Office office : officeList) {
			officeIdList.add(office.getId());
		}
		return officeIdList;
	}

	public void setOfficeIdList(List<String> officeIdList) {
		officeList = Lists.newArrayList();
		for (String officeId : officeIdList) {
			Office office = new Office();
			office.setId(officeId);
			officeList.add(office);
		}
	}

	public String getOfficeIds() {
		return StringUtils.join(getOfficeIdList(), ",");
	}

	public void setOfficeIds(String officeIds) {
		officeList = Lists.newArrayList();
		if (officeIds != null){
			String[] ids = StringUtils.split(officeIds, ",");
			setOfficeIdList(Lists.newArrayList(ids));
		}
	}

	/**
	 * 获取权限字符串列表
	 */
	public List<String> getPermissions() {
		List<String> permissions = Lists.newArrayList();
		for (Menu menu : menuList) {
			if (menu.getPermission()!=null && !"".equals(menu.getPermission())){
				permissions.add(menu.getPermission());
			}
		}
		return permissions;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


}
