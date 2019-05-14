
package com.thorough.library.system.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thorough.library.mybatis.persistence.model.entity.TreeEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.List;

/**
 * 菜单Entity
 */
@Table(name = "sys_menu")
public class Menu extends TreeEntity<String,Menu> {

	private static final long serialVersionUID = 1L;
	private Menu parent;	// 父级菜单

	@Column(name = "parent_ids")
	private String parentIds; // 所有父级编号

	@Column(name = "href")
	private String href; 	// 链接

	@Column(name = "target")
	private String target; 	// 目标（ mainFrame、_blank、_self、_parent、_top）

	@Column(name = "icon")
	private String icon; 	// 图标

	@Column(name = "is_show")
	private String isShow; 	// 是否在菜单中显示（1：显示；0：不显示）

	@Column(name = "permission")
	private String permission; // 权限标识

	private String userId;

	public Menu(){
		super();
		this.sort = 30;
		this.isShow = "1";
	}

	public Menu(String id){
		super(id);
	}

	@JsonBackReference
	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}

	@JsonIgnore
	public static void sortList(List<Menu> list, List<Menu> sourcelist, String parentId, boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			Menu e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						Menu child = sourcelist.get(j);
						if (child.getParent()!=null && child.getParent().getId()!=null
								&& child.getParent().getId().equals(e.getId())){
							sortList(list, sourcelist, e.getId(), true);
							break;
						}
					}
				}
			}
		}
	}

	@JsonIgnore
	public static String getRootId(){
		return "1";
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return name;
	}
}