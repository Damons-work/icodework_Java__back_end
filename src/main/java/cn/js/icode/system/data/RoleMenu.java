package cn.js.icode.system.data;

/**
 * 角色管理的菜单项 - POJO
 * 对应数据库表：system_role_menu
 *
 * @author ICode Studio
 * @version 1.0  2018-09-17
 */
public class RoleMenu {
	/* 编号（关键字） */
	private Long id = null;
	/* 角色编号，关联system_role_base.RoleId */
	private Long roleId = null;
	/* 菜单项编号，关联system_menu_base.MenuId */
	private Long menuId = null;
	/* 菜单项 */
	private Menu menu = null;
	/* 权限标识（示例：0b100101101） */
	private Integer permissionFlag = null;
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
	 * @return 角色编号，关联system_role_base.RoleId
	 */
	public Long getRoleId() {
		return roleId;
	}
	/**
	 * @param roleId 角色编号，关联system_role_base.RoleId
	 */
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	/**
	 * @return 菜单项编号，关联system_menu_base.MenuId
	 */
	public Long getMenuId() {
		return menuId;
	}
	/**
	 * @param menuId 菜单项编号，关联system_menu_base.MenuId
	 */
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	/**
	 * @return 菜单项
	 */
	public Menu getMenu() {
		return menu;
	}
	/**
	 * @param menu 菜单项
	 */
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	/**
	 * @return 权限标识（示例：0b100101101）
	 */
	public Integer getPermissionFlag() {
		return permissionFlag;
	}
	/**
	 * @param permissionFlag 权限标识（示例：0b100101101）
	 */
	public void setPermissionFlag(Integer permissionFlag) {
		this.permissionFlag = permissionFlag;
	}

	/**
	 * 指定权限位是否有权限，即查看权限标识二进制数据的第x位是否是1
	 *
	 * @param nIndex 权限位，二进制从右向左的位序号。0表示第1位，表示所有权限都具备。
	 * @return true: 第x位是1，有权限   false: 第x位是0，没有权限
	 */
	public boolean getCan(int nIndex) {
		boolean bl = false;
		Integer nFlag = getPermissionFlag();
		if(nFlag == null) {
			return bl;
		}

		int mask = 1 << nIndex;
		return (nFlag.intValue() & mask) == mask;
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
		if(getId() == null || obj == null || !(obj instanceof RoleMenu)) {
			return false;
		}
		return getId().equals(((RoleMenu)obj).getId());
	}
}
