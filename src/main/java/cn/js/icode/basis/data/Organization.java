package cn.js.icode.basis.data;

import cn.js.icode.basis.service.OrganizationService;

/**
 * 组织机构 - POJO
 * 对应数据库表：basis_organization_base
 *
 * @author ICode Studio
 * @version 1.0  2018-09-23
 */
public class Organization {
	/* 组织机构编号（关键字） */
	private Long organizationId = null;
	/* 父组织编号，关联basis_organization_base.OrganizationId */
	private Long parentId = null;
	/* 父组织 */
	private Organization parent = null;
	/* 类型标识{1：集团2：公司3：部门4：小组} */
	private Integer typeFlag = null;
	/* 组织编码，同时用于排序(ASC方式) */
	private String organizationCode = null;
	/* 组织名称 */
	private String organizationName = null;
	/* 简称 */
	private String briefName = null;
	/* 有效标识 */
	private Boolean activeFlag = null;
	/**
	 * @return 组织机构编号
	 */
	public Long getId() {
		return organizationId;
	}
	/**
	 * @param organizationId 组织机构编号
	 */
	public void setId(Long organizationId) {
		this.organizationId = organizationId;
	}
	/**
	 * @return 父组织编号，关联basis_organization_base.OrganizationId
	 */
	public Long getParentId() {
		return parentId;
	}
	/**
	 * @param parentId 父组织编号，关联basis_organization_base.OrganizationId
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return 父组织
	 */
	public Organization getParent() {
		return parent;
	}

	/**
	 * @param parent 父组织
	 */
	public void setParent(Organization parent) {
		this.parent = parent;
	}

	/**
	 * @return 类型标识{1：集团2：公司3：部门4：小组}
	 */
	public Integer getTypeFlag() {
		return typeFlag;
	}
	/**
	 * @param typeFlag 类型标识{1：集团2：公司3：部门4：小组}
	 */
	public void setTypeFlag(Integer typeFlag) {
		this.typeFlag = typeFlag;
	}
	/**
	 * @return 类型{1：集团2：公司3：部门4：小组}名称
	 */
	public String getTypeName() {
		Integer nFlag = getTypeFlag();
		return OrganizationService.getTypeName(nFlag);
	}
	/**
	 * @return 组织编码，同时用于排序(ASC方式)
	 */
	public String getOrganizationCode() {
		return organizationCode;
	}
	/**
	 * @param organizationCode 组织编码，同时用于排序(ASC方式)
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}
	/**
	 * @return 组织名称
	 */
	public String getOrganizationName() {
		return organizationName;
	}
	/**
	 * @param organizationName 组织名称
	 */
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	/**
	 * @return 全路径名称
	 */
	public String getFullName() {
		return OrganizationService.getFullName(getId());
	}
	
	/**
	 * @return 简称
	 */
	public String getBriefName() {
		return briefName;
	}
	/**
	 * @param briefName 简称
	 */
	public void setBriefName(String briefName) {
		this.briefName = briefName;
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
		if(getId() == null || obj == null || !(obj instanceof Organization)) {
			return false;
		}
		return getId().equals(((Organization)obj).getId());
	}
}
