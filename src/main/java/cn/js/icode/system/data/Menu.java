package cn.js.icode.system.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * 菜单项 - POJO
 * 对应数据库表：system_menu_base
 *
 * @author ICode Studio
 * @version 1.0  2018-09-15
 */
public class Menu {
	/* 菜单项编号（关键字） */
	private Long menuId = null;
	/* 父菜单项编号，关联system_user_base.MenuId */
	private Long parentId = null;
	/* 父菜单项 */
	private Menu parent = null;
	/* 菜单名称 */
	private String menuName = null;
	/* 图标文件或者class */
	private String iconImage = null;
	/* 排序序号 */
	private String orderBy = null;
	/* 菜单地址，使用'-'号开头表示弹出窗口方式打开 */
	private String menuUrl = null;
	/* 帮助文档可以在其它系统中维护，链接于此 */
	private String helpUrl = null;
	/* 菜单页面功能对外提供的接口地址，推荐使用swagger地址 */
	private String apiUrl = null;
	/** 菜单功能列表 */
	List<MenuFunction> functionList = null;
	/**
	 * @return 菜单项编号
	 */
	public Long getId() {
		return menuId;
	}
	/**
	 * @param menuId 菜单项编号
	 */
	public void setId(Long menuId) {
		this.menuId = menuId;
	}
	/**
	 * @return 父菜单项编号，关联system_user_base.MenuId
	 */
	public Long getParentId() {
		return parentId;
	}
	/**
	 * @param parentId 父菜单项编号，关联system_user_base.MenuId
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return 父菜单项
	 */
	public Menu getParent() {
		return parent;
	}

	/**
	 * @param parent 父菜单项
	 */
	public void setParent(Menu parent) {
		this.parent = parent;
	}

	/**
	 * @return 菜单名称
	 */
	public String getMenuName() {
		return menuName;
	}
	/**
	 * @param menuName 菜单名称
	 */
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	/**
	 * @return 图标文件或者class
	 */
	public String getIconImage() {
		return iconImage;
	}
	/**
	 * @param iconImage 图标文件或者class
	 */
	public void setIconImage(String iconImage) {
		this.iconImage = iconImage;
	}
	/**
	 * @return 排序序号
	 */
	public String getOrderBy() {
		return orderBy;
	}
	/**
	 * @param orderBy 排序序号
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	/**
	 * @return 菜单地址，使用'-'号开头表示弹出窗口方式打开
	 */
	public String getMenuUrl() {
		return menuUrl;
	}
	/**
	 * @param menuUrl 菜单地址，使用'-'号开头表示弹出窗口方式打开
	 */
	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}
	/**
	 * @return 帮助文档可以在其它系统中维护，链接于此
	 */
	public String getHelpUrl() {
		return helpUrl;
	}
	/**
	 * @param helpUrl 帮助文档可以在其它系统中维护，链接于此
	 */
	public void setHelpUrl(String helpUrl) {
		this.helpUrl = helpUrl;
	}
	/**
	 * @return 菜单页面功能对外提供的接口地址，推荐使用swagger地址
	 */
	public String getApiUrl() {
		return apiUrl;
	}
	/**
	 * @param apiUrl 菜单页面功能对外提供的接口地址，推荐使用swagger地址
	 */
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	/**
	 * @return 菜单功能列表
	 */
	public List<MenuFunction> getFunctionList() {
		return functionList;
	}

	/**
	 * @param functionList 菜单功能列表
	 */
	public void setFunctionList(List<MenuFunction> functionList) {
		this.functionList = functionList;
	}

	/**
	 * @return 当前菜单包括的权限类型
	 */
	public Collection<Integer> getTypeFlags() {
		if(functionList == null || functionList.size() == 0) {
			return Collections.emptyList();
		}

		Collection<Integer> flags = new HashSet<Integer>();

		for(MenuFunction mf : functionList) {
			Integer f = mf.getTypeFlag();
			if(flags.contains(f)) continue;

			flags.add(f);
		}

		return flags;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (getId() == null)?0:getId().toString().hashCode();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(getId() == null || obj == null || !(obj instanceof Menu)) {
			return false;
		}
		return getId().equals(((Menu)obj).getId());
	}
}
