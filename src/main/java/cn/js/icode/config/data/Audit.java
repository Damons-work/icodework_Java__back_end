package cn.js.icode.config.data;

import java.util.ArrayList;
import java.util.List;

import cn.js.icode.basis.data.Organization;
import cn.js.icode.system.data.User;

/**
 * 审批流程 - POJO
 * 对应数据库表：config_audit_base
 * 
 * @author ICode Studio
 * @version 1.0  2018-10-30
 */
public class Audit {
	/* 流程编号（关键字） */
	private Long auditId = null;

	/* 申请单类型编码，关联config_item_base.ItemCode[申请单类型] */
	private String bizCode = null;
	/* 申请单类型编码，关联config_item_base.ItemCode[申请单类型] */
	private Item biz = null;
	
	/* 适用组织机构的编号，关联basis_organization_base.OrganizationId */
	private Long organizationId = null;
	/* 适用组织 */
	private Organization organization = null;
	
	/* 申请人编号，关联system_user_base.UserId */
	private Long userId = null;
	/* 申请人 */
	private User user = null;
	
	/* 有效标识 */
	private Boolean activeFlag = null;
	
	/* 环节数量 */
	private Integer itemCount = null;
	/* 环节信息 */
	private List<AuditItem> auditItemList = new ArrayList<AuditItem>();
	
	/**
	 * @return 流程编号
	 */
	public Long getId() {
		return auditId;
	}

	/**
	 * @param auditId 流程编号
	 */
	public void setId(Long auditId) {
		this.auditId = auditId;
	}

	/**
	 * @return 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 */
	public String getBizCode() {
		return bizCode;
	}

	/**
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 */
	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}
	
	/**
	 * @return 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 */
	public Item getBiz() {
		return biz;
	}

	/**
	 * @param biz 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 */
	public void setBiz(Item biz) {
		this.biz = biz;
	}
	
	/**
	 * @return 适用组织机构的编号，关联basis_organization_base.OrganizationId
	 */
	public Long getOrganizationId() {
		return organizationId;
	}

	/**
	 * @param organizationId 适用组织机构的编号，关联basis_organization_base.OrganizationId
	 */
	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}
	
	/**
	 * @return 适用组织
	 */
	public Organization getOrganization() {
		return organization;
	}

	/**
	 * @param 适用组织
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	/**
	 * @return 申请人编号，关联system_user_base.UserId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId 申请人编号，关联system_user_base.UserId
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	/**
	 * @return 申请人
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user 申请人
	 */
	public void setUser(User user) {
		this.user = user;
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
	 * @return 环节数量
	 */
	public Integer getItemCount() {
		return itemCount;
	}

	/**
	 * @param itemCount 环节数量
	 */
	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}
	
	/**
	 * @return 环节信息
	 */
	public List<AuditItem> getAuditItemList() {
		return auditItemList;
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
		if(getId() == null || obj == null || !(obj instanceof Audit)) {
			return false;
		}

		return getId().equals(((Audit)obj).getId());
	}
}
