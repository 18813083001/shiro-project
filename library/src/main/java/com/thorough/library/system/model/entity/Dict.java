
package com.thorough.library.system.model.entity;


import com.thorough.library.mybatis.persistence.model.entity.IdUserDateDelFlagRemarksEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * 字典Entity
 */
@Table(name = "sys_dict")
public class Dict extends IdUserDateDelFlagRemarksEntity<String> {

	private static final long serialVersionUID = 1L;

	@Column(name = "value")
	private String value;	// 数据值

	@Column(name = "label")
	private String label;	// 标签名

	@Column(name = "type")
	private String type;	// 类型

	@Column(name = "description")
	private String description;// 描述

	@Column(name = "sort")
	private Integer sort;	// 排序

	@Column(name = "parent_id")
	private String parentId;//父Id

	public Dict() {
		super();
	}
	
	public Dict(String id){
		super(id);
	}
	
	public Dict(String value, String label){
		this.value = value;
		this.label = label;
	}
	
	@XmlAttribute
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlAttribute
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	@Override
	public String toString() {
		return label;
	}
}