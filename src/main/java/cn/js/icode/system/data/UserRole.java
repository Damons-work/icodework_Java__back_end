package cn.js.icode.system.data;

/**
 * 账户具有的角色 - POJO
 * 对应数据库表：system_user_role
 *
 * @author ICode Studio
 * @version 1.0  2018-09-18
 */
public class UserRole {
	/* 编号（关键字） */
	private Long id = null;
	/* 账户编号，关联system_user_base.UserId */
	private Long userId = null;
	/* 角色编号，关联system_role_base.RoleId */
	private Long roleId = null;
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
	 * @return 账户编号，关联system_user_base.UserId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId 账户编号，关联system_user_base.UserId
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
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
		if(getId() == null || obj == null || !(obj instanceof UserRole)) {
			return false;
		}
		return getId().equals(((UserRole)obj).getId());
	}
}
