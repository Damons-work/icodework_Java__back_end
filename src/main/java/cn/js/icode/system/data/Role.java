package cn.js.icode.system.data;
import cn.js.icode.system.service.RoleService;

/**
 * 角色 - POJO
 * 对应数据库表：system_role_base
 *
 * @author ICode Studio
 * @version 1.0  2018-09-17
 */
public class Role {
	/* 角色编号（关键字） */
	private Long roleId = null;
	/* 角色名称 */
	private String roleName = null;
	/* 类别标识{1：权限角色2：审核角色} */
	private Integer typeFlag = null;
	/* 角色说明 */
	private String remark = null;
	/* 用户数量 */
	private Integer userCount = null;
	/* 欢迎页地址 */
	private String welcomePage = null;
	/**
	 * @return 角色编号
	 */
	public Long getId() {
		return roleId;
	}
	/**
	 * @param roleId 角色编号
	 */
	public void setId(Long roleId) {
		this.roleId = roleId;
	}
	/**
	 * @return 角色名称
	 */
	public String getRoleName() {
		return roleName;
	}
	/**
	 * @param roleName 角色名称
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	/**
	 * @return 类别标识{1：权限角色2：审核角色}
	 */
	public Integer getTypeFlag() {
		return typeFlag;
	}
	/**
	 * @param typeFlag 类别标识{1：权限角色2：审核角色}
	 */
	public void setTypeFlag(Integer typeFlag) {
		this.typeFlag = typeFlag;
	}
	/**
	 * @return 类别{1：权限角色2：审核角色}名称
	 */
	public String getTypeName() {
		Integer nFlag = getTypeFlag();
		return RoleService.getTypeName(nFlag);
	}
	/**
	 * @return 角色说明
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @param remark 角色说明
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * @return 用户数量
	 */
	public Integer getUserCount() {
		return userCount;
	}
	/**
	 * @param userCount 用户数量
	 */
	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}
	/**
	 * @return 欢迎页地址
	 */
	public String getWelcomePage() {
		return welcomePage;
	}

	/**
	 * @param welcomePage 欢迎页地址
	 */
	public void setWelcomePage(String welcomePage) {
		this.welcomePage = welcomePage;
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
		if(getId() == null || obj == null || !(obj instanceof Role)) {
			return false;
		}
		return getId().equals(((Role)obj).getId());
	}
}
