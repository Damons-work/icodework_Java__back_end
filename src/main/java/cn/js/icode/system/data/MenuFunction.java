package cn.js.icode.system.data;

import cn.js.icode.system.service.MenuFunctionService;

/**
 * 菜单功能 - POJO
 * 对应数据库表：system_menu_function
 *
 * @author ICode Studio
 * @version 1.0  2018-09-17
 */
public class MenuFunction {
	/* 编号（关键字） */
	private Long id = null;
	/* 菜单编号，关联system_menu_base.MenuId */
	private Long menuId = null;
	/* 访问地址 */
	private String functionUrl = null;
	/* 类型标识{1:查看2:新增3:修改4:删除5:导入6:导出7:审核8:扩展A9:扩展B} */
	private Integer typeFlag = null;
	/**
	 * @return 编号
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id 编号
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return 菜单编号，关联system_menu_base.MenuId
	 */
	public Long getMenuId() {
		return menuId;
	}
	/**
	 * @param menuId 菜单编号，关联system_menu_base.MenuId
	 */
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	/**
	 * @return 访问地址
	 */
	public String getFunctionUrl() {
		return functionUrl;
	}
	/**
	 * @param functionUrl 访问地址
	 */
	public void setFunctionUrl(String functionUrl) {
		this.functionUrl = functionUrl;
	}
	/**
	 * @return 类型标识{1:查看2:新增3:修改4:删除5:导入6:导出7:审核8:扩展A9:扩展B}
	 */
	public Integer getTypeFlag() {
		return typeFlag;
	}
	/**
	 * @param typeFlag 类型标识{1:查看2:新增3:修改4:删除5:导入6:导出7:审核8:扩展A9:扩展B}
	 */
	public void setTypeFlag(Integer typeFlag) {
		this.typeFlag = typeFlag;
	}
	/**
	 * @return 类型{1:查看2:新增3:修改4:删除5:导入6:导出7:审核8:扩展A9:扩展B}名称
	 */
	public String getTypeName() {
		Integer nFlag = getTypeFlag();
		return MenuFunctionService.getTypeName(nFlag);
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
		if(getId() == null || obj == null || !(obj instanceof MenuFunction)) {
			return false;
		}
		return getId().equals(((MenuFunction)obj).getId());
	}
}
