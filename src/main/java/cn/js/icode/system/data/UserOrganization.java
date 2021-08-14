package cn.js.icode.system.data;

/**
 * 账户管理的组织 - POJO
 * 对应数据库表：system_user_organization
 *
 * @author ICode Studio
 * @version 1.0  2018-09-18
 */
public class UserOrganization {
	/* 编号（关键字） */
	private Long id = null;
	/* 账户编号，关联system_user_base.UserId */
	private Long userId = null;
	/* 组织机构编号，关联basis_organization_base.OrganizationId */
	private Long organizationId = null;
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
	 * @return 组织机构编号，关联basis_organization_base.OrganizationId
	 */
	public Long getOrganizationId() {
		return organizationId;
	}
	/**
	 * @param organizationId 组织机构编号，关联basis_organization_base.OrganizationId
	 */
	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
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
		if(getId() == null || obj == null || !(obj instanceof UserOrganization)) {
			return false;
		}
		return getId().equals(((UserOrganization)obj).getId());
	}
}
