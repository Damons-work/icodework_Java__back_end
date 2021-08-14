package cn.js.icode.system.data;

import java.util.HashSet;
import java.util.Set;

import cn.js.icode.basis.data.Organization;
import cn.js.icode.basis.data.Partner;
import team.bangbang.common.config.Config;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.sso.data.DataLimit;

/**
 * 系统账户 - POJO
 * 对应数据库表：system_user_base
 *
 * @author ICode Studio
 * @version 1.0  2018-09-18
 */
public class User {
	/* 账户编号（关键字） */
	private Long userId = null;
	/* 所属商户编号，关联basis_partner_base.PartnerId */
	private String partnerId = null;
	/* 所属商户 */
	private Partner partner = null;
	/* 电子信箱 */
	private String email = null;
	/* 登录密码 */
	private String password = null;
	/* 账户名称 */
	private String userName = null;
	/* 所在组织编号，关联basis_organization_base.OrganizationId */
	private Long organizationId = null;
	/* 所在组织 */
	private Organization organization = null;
	/* 有效标识 */
	private Boolean activeFlag = null;
	/* 用户所有的角色信息(Set<Long>) */
	private Set<Long> roleIds = new HashSet<Long>();
	/* 欢迎页地址 */
	private String welcomePage = null;
	/* 数据查询的范围或者反范围 */
	private DataLimit DataLimit = null;

	/**
	 * @return 账户编号
	 */
	public Long getId() {
		return userId;
	}
	/**
	 * @param userId 账户编号
	 */
	public void setId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return 所属商户编号，关联basis_partner_base.PartnerId
	 */
	public String getPartnerId() {
		return partnerId;
	}

	/**
	 * @param partnerId 所属商户编号，关联basis_partner_base.PartnerId
	 */
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	
	/**
	 * @return 所属商户
	 */
	public Partner getPartner() {
		return partner;
	}
	/**
	 * @param partner 所属商户
	 */
	public void setPartner(Partner partner) {
		this.partner = partner;
	}
	/**
	 * @return 电子信箱
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email 电子信箱
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return 登录密码
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password 登录密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return 账户名称
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName 账户名称
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return 所在组织编号，关联basis_organization_base.OrganizationId
	 */
	public Long getOrganizationId() {
		return organizationId;
	}
	/**
	 * @param organizationId 所在组织编号，关联basis_organization_base.OrganizationId
	 */
	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}
	/**
	 * @return 所在组织
	 */
	public Organization getOrganization() {
		return organization;
	}
	/**
	 * @param organization 所在组织
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	/**
	 * @return 有效标识
	 */
	public Boolean getActiveFlag() {
		return activeFlag;
	}
	/**
	 * @param activeFlag 有效标识
	 */
	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	/**
	 * @return 用户所有的角色信息(Set<Role>)
	 */
	public Set<Long> getRoleIds() {
		return roleIds;
	}
	/**
	 * 检查当前用户是否具有特殊的角色
	 *
	 * @param alias
	 *            角色编号的别名，如HR系统的别名：hr
	 * @return
	 */
	public boolean hasSpecialRole(String alias) {
		if (roleIds == null || roleIds.size() == 0) {
			return false;
		}

		// 获得这个特殊的角色编号
		String chain = Config.getProperty("system.role." + alias);
		if (chain == null) {
			return false;
		}
		String[] arr = chain.split(",");
		if (arr == null || arr.length == 0) {
			return false;
		}

		for (int i = 0; i < arr.length; i++) {
			long l = LogicUtility.parseLong(arr[i], 0L);
			if (roleIds.contains(l)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 检查该用户是否具有指定的角色
	 *
	 * @param roleId
	 *            指定的角色
	 * @return true:具有 false:不具有
	 */
	public boolean containsRole(Long roleId) {
		if (roleIds == null || roleId == null) {
			return false;
		}

		return roleIds.contains(roleId);
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

	/**
	 * @return 数据查询的范围或者反范围
	 */
	public DataLimit getDataLimit() {
		return DataLimit;
	}
	/**
	 * @param DataLimit 数据查询的范围或者反范围
	 */
	public void setDataLimit(DataLimit DataLimit) {
		this.DataLimit = DataLimit;
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
		if(getId() == null || obj == null || !(obj instanceof User)) {
			return false;
		}
		return getId().equals(((User)obj).getId());
	}
}
