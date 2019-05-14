package com.thorough.library.system.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.thorough.library.mybatis.persistence.model.entity.IdUserDateDelFlagRemarksNameEntity;
import com.thorough.library.utils.Collections3;
import com.thorough.library.mybatis.persistence.Page;
import com.thorough.library.mybatis.persistence.Pageable;
import com.thorough.library.utils.PropertyUtil;
import com.thorough.library.utils.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.List;

/**
 * 用户Entity
 */
@Table(name = "sys_user")
public class User extends IdUserDateDelFlagRemarksNameEntity<String> implements Pageable<User> {

	private static final long serialVersionUID = 1L;
	private Office company;	// 归属医院
	private Office office;	// 归属部门

	@Column(name = "company_id")
	private String companyId;	// 医院

	@Column(name = "office_id")
	private String officeId;	//科室

	@Column(name = "login_name")
	private String loginName;// 登录名

	@Column(name = "password")
	private String password;// 密码

	@Column(name = "no")
	private String no;		// 工号

	@Column(name = "email")
	private String email;	// 邮箱

	@Column(name = "phone")
	private String phone;	// 电话

	@Column(name = "mobile")
	private String mobile;	// 手机

	@Column(name = "user_type")
	private String userType;// 用户类型  1：系统管理员 ，2：医生，3：主任 ，4：专家，5：顾问

	@Column(name = "login_ip")
	private String loginIp;	// 最后登陆IP

	@Column(name = "login_date")
	private Date loginDate;	// 最后登陆日期

	@Column(name = "login_flag")
	private String loginFlag;	// 是否允许登陆

	@Column(name = "photo")
	private String photo;	// 头像

	private String oldLoginName;// 原登录名
	private String newPassword;	// 新密码

	private String oldLoginIp;	// 上次登陆IP
	private Date oldLoginDate;	// 上次登陆日期

	private Role role;	// 根据角色查询用户条件

	private List<Role> roleList = Lists.newArrayList(); // 拥有角色列表

	private String userTypeName;

	@JsonIgnore
	@XmlTransient
	private Page<User> page;

	/**
	 * 当前用户
	 */
	@JsonIgnore
	@XmlTransient
	@JSONField(serialize = false)
	protected User currentUser;

	public User() {
		super();
		this.loginFlag = PropertyUtil.YES;
	}

	public User(String id){
		super(id);
	}

	public User(String id, String loginName){
		super(id);
		this.loginName = loginName;
	}

	public User(Role role){
		super();
		this.role = role;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(String loginFlag) {
		this.loginFlag = loginFlag;
	}


	public String getId() {
		return id;
	}

	@JsonIgnore
	public Office getCompany() {
		return company;
	}

	public void setCompany(Office company) {
		this.company = company;
	}

	@JsonIgnore
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRemarks() {
		return remarks;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public String getOldLoginName() {
		return oldLoginName;
	}

	public void setOldLoginName(String oldLoginName) {
		this.oldLoginName = oldLoginName;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOldLoginIp() {
		if (oldLoginIp == null){
			return loginIp;
		}
		return oldLoginIp;
	}

	public void setOldLoginIp(String oldLoginIp) {
		this.oldLoginIp = oldLoginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOldLoginDate() {
		if (oldLoginDate == null){
			return loginDate;
		}
		return oldLoginDate;
	}

	public void setOldLoginDate(Date oldLoginDate) {
		this.oldLoginDate = oldLoginDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@JsonIgnore
	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	@JsonIgnore
	public List<String> getRoleIdList() {
		List<String> roleIdList = Lists.newArrayList();
		for (Role role : roleList) {
			roleIdList.add(role.getId());
		}
		return roleIdList;
	}

	public void setRoleIdList(List<String> roleIdList) {
		roleList = Lists.newArrayList();
		for (String roleId : roleIdList) {
			Role role = new Role();
			role.setId(roleId);
			roleList.add(role);
		}
	}

	public String getUserTypeName() {
		return userTypeName;
	}

	public void setUserTypeName(String userTypeName) {
		this.userTypeName = userTypeName;
	}

	/**
	 * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
	 */
	public String getRoleNames() {
		return Collections3.extractToString(roleList, "name", ",");
	}

	/*
	* 超级管理员
	* */

	public boolean isAdmin(){
		return (id != null && "1".equals(id)) || (id != null && "999".equals(id)) || (this.userType != null && "1".equals(this.userType));
	}

	/*
	* 是否是顾问
	* */
	public boolean isAdviser(){
		return StringUtils.isNotBlank(this.userType) && "5".equals(this.userType);
	}

	/*
	* 是否是专家
	* */
	public boolean isExpert(){
		return StringUtils.isNotBlank(this.userType) && "4".equals(this.userType);
	}

	/*
	* 是否是主任
	* */
	public boolean isDirector(){
		return StringUtils.isNotBlank(this.userType) && "3".equals(this.userType);
	}

	/*
	* 是否是医生
	* */
	public boolean isDoctor(){
		return StringUtils.isNotBlank(this.userType) && "2".equals(this.userType);
	}



	@Override
	public String toString() {
		return id;
	}

	@Override
	public Page<User> getPage() {
		if (page == null){
			page = new Page<User>();
		}
		return page;
	}

	@Override
	public Page<User> setPage(Page<User> page) {
		this.page = page;
		return page;
	}

//	public User getCurrentUser() {
//		if(currentUser == null){
//			currentUser = UserUtils.getUser();
//		}
//		return currentUser;
//	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}


}