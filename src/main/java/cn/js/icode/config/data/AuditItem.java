package cn.js.icode.config.data;

import team.bangbang.common.CommonMPI;
import cn.js.icode.system.data.Role;

/**
 * 审批环节 - POJO
 * 对应数据库表：config_audit_item
 * 
 * @author ICode Studio
 * @version 1.0  2018-10-30
 */
public class AuditItem {
	/* 环节编号（关键字） */
	private Long itemId = null;

	/* 审批流程编号，关联config_audit_base.AuditId */
	private Long auditId = null;
	
	/* 审批角色编号，关联system_role_base.RoleId */
	private Long choiceRoleId = null;
	/* 审批角色 */
	private Role choiceRole = null;
	
	/* 审批人员编号串，以半角逗号间隔 */
	private String choiceIds = null;
	/* 审批人员名称串 */
	private String choiceNames = null;
	
	/* 审批标识{1：并签2：会签} */
	private Integer attendFlag = null;
	
	/* 驳回是否可以继续审批 */
	private Boolean continueFlag = null;
	
	/* 任务序号（从1开始） */
	private Integer taskIndex = null;
	
	/*
	 * 固定数据字典 审批{1：并签2：会签}
	 */
	public static final String[] attendFlags = { "并签", "会签" };

	/**
	 * @return 环节编号
	 */
	public Long getId() {
		return itemId;
	}

	/**
	 * @param itemId 环节编号
	 */
	public void setId(Long itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return 审批流程编号，关联config_audit_base.AuditId
	 */
	public Long getAuditId() {
		return auditId;
	}

	/**
	 * @param auditId 审批流程编号，关联config_audit_base.AuditId
	 */
	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}
	
	/**
	 * @return 审批角色编号，关联system_role_base.RoleId
	 */
	public Long getChoiceRoleId() {
		return choiceRoleId;
	}

	/**
	 * @param choiceRoleId 审批角色编号，关联system_role_base.RoleId
	 */
	public void setChoiceRoleId(Long choiceRoleId) {
		this.choiceRoleId = choiceRoleId;
	}

	/**
	 * @return 审批角色
	 */
	public Role getChoiceRole() {
		return choiceRole;
	}

	/**
	 * @param choiceRole 审批角色
	 */
	public void setChoiceRole(Role choiceRole) {
		this.choiceRole = choiceRole;
	}
	
	/**
	 * @return 审批人员编号串，以半角逗号间隔
	 */
	public String getChoiceIds() {
		return choiceIds;
	}

	/**
	 * @param choiceIds 审批人员编号串，以半角逗号间隔
	 */
	public void setChoiceIds(String choiceIds) {
		this.choiceIds = choiceIds;
	}
	
	/**
	 * @return 审批人员名称串
	 */
	public String getChoiceNames() {
		return choiceNames;
	}

	/**
	 * @param choiceNames 审批人员名称串
	 */
	public void setChoiceNames(String choiceNames) {
		this.choiceNames = choiceNames;
	}

	/**
	 * @return 审批标识{1：并签2：会签}
	 */
	public Integer getAttendFlag() {
		return attendFlag;
	}

	/**
	 * @param attendFlag 审批标识{1：并签2：会签}
	 */
	public void setAttendFlag(Integer attendFlag) {
		this.attendFlag = attendFlag;
	}
	
	/**
	 * @return 审批{1：并签2：会签}名称
	 */
	public String getAttendName() {
		Integer nFlag = getAttendFlag();
		return CommonMPI.getDictionaryName(attendFlags, nFlag);
	}	
	
	/**
	 * @return 驳回是否可以继续审批
	 */
	public Boolean getContinueFlag() {
		return continueFlag;
	}

	/**
	 * @param continueFlag 驳回是否可以继续审批
	 */
	public void setContinueFlag(Boolean continueFlag) {
		this.continueFlag = continueFlag;
	}
	
	/**
	 * @return 任务序号（从1开始）
	 */
	public Integer getTaskIndex() {
		return taskIndex;
	}

	/**
	 * @param taskIndex 任务序号（从1开始）
	 */
	public void setTaskIndex(Integer taskIndex) {
		this.taskIndex = taskIndex;
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
		if(getId() == null || obj == null || !(obj instanceof AuditItem)) {
			return false;
		}

		return getId().equals(((AuditItem)obj).getId());
	}
}
