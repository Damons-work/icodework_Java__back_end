package cn.js.icode.config.data;

import java.util.Date;

import team.bangbang.common.CommonMPI;
import cn.js.icode.system.data.User;

/**
 * 审批任务 - POJO
 * 对应数据库表：config_audit_task
 * 
 * @author ICode Studio
 * @version 1.0  2018-10-31
 */
public class AuditTask {
	/* 任务编号（关键字） */
	private Long taskId = null;

	/* 申请单类型编码，关联config_item_base.ItemCode[申请单类型] */
	private String bizCode = null;
	/* 申请单类型编码，关联config_item_base.ItemCode[申请单类型] */
	private Item bizItem = null;
	
	/* 申请单编号 */
	private String bizNo = null;
	/* 申请单 */
	private Object bizData = null;
	
	/* 任务序号，从1开始 */
	private Integer taskIndex = null;
	
	/* 审批人编号，关联system_user_base.UserId */
	private Long planUserId = null;
	/* 原计划审批人 */
	private User planUser = null;
	
	/* 审批方式标识{1：并签2：会签} */
	private Integer attendFlag = null;
	
	/* 驳回时是否继续审批 */
	private Boolean continueFlag = null;
	
	/* 状态标识{1：对列中，2：待审核，3：已审核} */
	private Integer statusFlag = null;
	
	/* 实际审批人编号，关联system_user_base.UserId */
	private Long auditUserId = null;
	/* 实际审批人 */
	private User auditUser = null;
	
	/* 审批是否通过 */
	private Boolean passFlag = null;
	
	/* 审批意见 */
	private String auditComment = null;
	
	/* 审批时间 */
	private Date auditTime = null;
	/* 审批时间 （查询上线） */
	private Date auditTimeTop = null;
	/* 审批时间 （查询下线） */
	private Date auditTimeBottom = null;
	
	/* 任务创建时间 */
	private Date createTime = null;
	/* 任务创建时间 （查询上线） */
	private Date createTimeTop = null;
	/* 任务创建时间 （查询下线） */
	private Date createTimeBottom = null;
	
	/*
	 * 固定数据字典 审批方式{1：并签2：会签}
	 */
	public static final String[] attendFlags = { "并签", "会签" };

	/*
	 * 固定数据字典 状态{1：对列中，2：待审核，3：已审核}
	 */
	public static final String[] statusFlags = { "对列中", "待审核", "已审核" };

	/**
	 * @return 任务编号
	 */
	public Long getId() {
		return taskId;
	}

	/**
	 * @param taskId 任务编号
	 */
	public void setId(Long taskId) {
		this.taskId = taskId;
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
	public Item getBizItem() {
		return bizItem;
	}

	/**
	 * @param biz 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 */
	public void setBizItem(Item bizItem) {
		this.bizItem = bizItem;
	}
	
	/**
	 * @return 申请单编号
	 */
	public String getBizNo() {
		return bizNo;
	}

	/**
	 * @param bizNo 申请单编号
	 */
	public void setBizNo(String bizNo) {
		this.bizNo = bizNo;
	}
	
	/**
	 * @return 申请单
	 */
	public Object getBizData() {
		return bizData;
	}

	/**
	 * @param bizData 申请单
	 */
	public void setBizData(Object bizData) {
		this.bizData = bizData;
	}

	/**
	 * @return 任务序号，从1开始
	 */
	public Integer getTaskIndex() {
		return taskIndex;
	}

	/**
	 * @param taskIndex 任务序号，从1开始
	 */
	public void setTaskIndex(Integer taskIndex) {
		this.taskIndex = taskIndex;
	}
	
	/**
	 * @return 审批人编号，关联system_user_base.UserId
	 */
	public Long getPlanUserId() {
		return planUserId;
	}

	/**
	 * @param planUserId 审批人编号，关联system_user_base.UserId
	 */
	public void setPlanUserId(Long planUserId) {
		this.planUserId = planUserId;
	}
	
	/**
	 * @return 原计划审批人
	 */
	public User getPlanUser() {
		return planUser;
	}

	/**
	 * @param planUser 原计划审批人
	 */
	public void setPlanUser(User planUser) {
		this.planUser = planUser;
	}

	/**
	 * @return 审批方式标识{1：并签2：会签}
	 */
	public Integer getAttendFlag() {
		return attendFlag;
	}

	/**
	 * @param attendFlag 审批方式标识{1：并签2：会签}
	 */
	public void setAttendFlag(Integer attendFlag) {
		this.attendFlag = attendFlag;
	}
	
	/**
	 * @return 审批方式{1：并签2：会签}名称
	 */
	public String getAttendName() {
		Integer nFlag = getAttendFlag();
		return CommonMPI.getDictionaryName(attendFlags, nFlag);
	}	
	
	/**
	 * @return 驳回时是否继续审批
	 */
	public Boolean getContinueFlag() {
		return continueFlag;
	}

	/**
	 * @param continueFlag 驳回时是否继续审批
	 */
	public void setContinueFlag(Boolean continueFlag) {
		this.continueFlag = continueFlag;
	}
	
	/**
	 * @return 状态标识{1：对列中，2：待审核，3：已审核}
	 */
	public Integer getStatusFlag() {
		return statusFlag;
	}

	/**
	 * @param statusFlag 状态标识{1：对列中，2：待审核，3：已审核}
	 */
	public void setStatusFlag(Integer statusFlag) {
		this.statusFlag = statusFlag;
	}
	
	/**
	 * @return 状态{1：对列中，2：待审核，3：已审核}名称
	 */
	public String getStatusName() {
		Integer nFlag = getStatusFlag();
		return CommonMPI.getDictionaryName(statusFlags, nFlag);
	}	
	
	/**
	 * @return 实际审批人编号，关联system_user_base.UserId
	 */
	public Long getAuditUserId() {
		return auditUserId;
	}

	/**
	 * @param auditUserId 实际审批人编号，关联system_user_base.UserId
	 */
	public void setAuditUserId(Long auditUserId) {
		this.auditUserId = auditUserId;
	}
	
	/**
	 * @return 实际审批人
	 */
	public User getAuditUser() {
		return auditUser;
	}

	/**
	 * @param auditUser 实际审批人
	 */
	public void setAuditUser(User auditUser) {
		this.auditUser = auditUser;
	}

	/**
	 * @return 审批是否通过
	 */
	public Boolean getPassFlag() {
		return passFlag;
	}

	/**
	 * @param passFlag 审批是否通过
	 */
	public void setPassFlag(Boolean passFlag) {
		this.passFlag = passFlag;
	}
	
	/**
	 * @return 审批意见
	 */
	public String getAuditComment() {
		return auditComment;
	}

	/**
	 * @param auditComment 审批意见
	 */
	public void setAuditComment(String auditComment) {
		this.auditComment = auditComment;
	}
	
	/**
	 * @return 审批时间
	 */
	public Date getAuditTime() {
		return auditTime;
	}

	/**
	 * @param auditTime 审批时间
	 */
	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	/**
	 * @return 审批时间（查询上线）
	 */
	public Date getAuditTimeTop() {
		return auditTimeTop;
	}

	/**
	 * @param auditTimeTop 审批时间（查询上线）
	 */
	public void setAuditTimeTop(Date auditTimeTop) {
		this.auditTimeTop = auditTimeTop;
	}

	/**
	 * @return 审批时间（查询下线）
	 */
	public Date getAuditTimeBottom() {
		return auditTimeBottom;
	}

	/**
	 * @param auditTimeBottom 审批时间（查询下线）
	 */
	public void setAuditTimeBottom(Date auditTimeBottom) {
		this.auditTimeBottom = auditTimeBottom;
	}
	
	/**
	 * @return 任务创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime 任务创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return 任务创建时间（查询上线）
	 */
	public Date getCreateTimeTop() {
		return createTimeTop;
	}

	/**
	 * @param createTimeTop 任务创建时间（查询上线）
	 */
	public void setCreateTimeTop(Date createTimeTop) {
		this.createTimeTop = createTimeTop;
	}

	/**
	 * @return 任务创建时间（查询下线）
	 */
	public Date getCreateTimeBottom() {
		return createTimeBottom;
	}

	/**
	 * @param createTimeBottom 任务创建时间（查询下线）
	 */
	public void setCreateTimeBottom(Date createTimeBottom) {
		this.createTimeBottom = createTimeBottom;
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
		if(getId() == null || obj == null || !(obj instanceof AuditTask)) {
			return false;
		}

		return getId().equals(((AuditTask)obj).getId());
	}
}
