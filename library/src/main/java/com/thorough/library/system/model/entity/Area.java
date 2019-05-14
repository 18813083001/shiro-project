
package com.thorough.library.system.model.entity;

import com.thorough.library.mybatis.persistence.model.entity.TreeEntity;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 区域Entity
 */
@Table(name = "sys_area")
public class Area extends TreeEntity<String,Area> {

	private static final long serialVersionUID = 1L;
	@Column(name = "code")
	private String code; 	// 区域编码
	@Column(name = "type")
	private String type; 	// 区域类型（1：国家；2：省份、直辖市；3：地市；4：区县）

	public Area(){
		super();
		this.sort = 30;
	}

	public Area(String id){
		super(id);
	}

	public Area getParent() {
		return parent;
	}

	public void setParent(Area parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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