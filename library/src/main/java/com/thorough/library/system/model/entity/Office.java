
package com.thorough.library.system.model.entity;


import com.thorough.library.mybatis.persistence.model.entity.TreeEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.List;

/**
 * 机构Entity
 */
@Table(name = "sys_office")
public class Office extends TreeEntity<String,Office> {

	private static final long serialVersionUID = 1L;

	private Area area;		// 归属区域

	@Column(name = "code")
	private String code; 	// 机构编码

	@Column(name = "type")
	private String type; 	// 机构类型（1：医院；2：部门；3：小组）

	@Column(name = "grade")
	private String grade; 	// 机构等级（1：一级；2：二级；3：三级；4：四级）

	@Column(name = "address")
	private String address; // 联系地址

	@Column(name = "zip_code")
	private String zipCode; // 邮政编码

	@Column(name = "master")
	private String master; 	// 负责人

	@Column(name = "phone")
	private String phone; 	// 电话

	@Column(name = "fax")
	private String fax; 	// 传真

	@Column(name = "email")
	private String email; 	// 邮箱

	@Column(name = "USEABLE")
	private String useable;//是否可用

	private User primaryPerson;//主负责人

	private User deputyPerson;//副负责人

	private List<String> childDeptList;//快速添加子部门

	public Office(){
		super();
//		this.sort = 30;
		this.type = "2";
	}

	public Office(String id){
		super(id);
	}

	public List<String> getChildDeptList() {
		return childDeptList;
	}

	public void setChildDeptList(List<String> childDeptList) {
		this.childDeptList = childDeptList;
	}

	public String getUseable() {
		return useable;
	}

	public void setUseable(String useable) {
		this.useable = useable;
	}

	public User getPrimaryPerson() {
		return primaryPerson;
	}

	public void setPrimaryPerson(User primaryPerson) {
		this.primaryPerson = primaryPerson;
	}

	public User getDeputyPerson() {
		return deputyPerson;
	}

	public void setDeputyPerson(User deputyPerson) {
		this.deputyPerson = deputyPerson;
	}

	public Office getParent() {
		return parent;
	}

	public void setParent(Office parent) {
		this.parent = parent;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return name;
	}
}