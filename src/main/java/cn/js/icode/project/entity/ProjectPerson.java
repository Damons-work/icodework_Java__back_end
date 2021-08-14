package cn.js.icode.project.entity;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 项目内参与人员 - 实体
 * 对应数据库表：project_project_person
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
public class ProjectPerson {
	/* 编号（关键字） */
	private Long id = null;

	/* 项目编号 */
	private Long projectId = null;
	
	/* 参与人员编号，关联project_person_base.person_id */
	private Long personId = null;
	/* 参与人员信息 */
	private Person person = null;
	
	/* 是否是钉子标识 */
	private Boolean isLeaderFlag = null;
	
	/* 投入时间百分比 */
	private Double timePercent = null;
	
	/* 开始日期 */
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date beginDate = null;
	/* 开始日期 （查询范围下限） */
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date beginDateBottom = null;
	/* 开始日期 （查询范围上限） */
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date beginDateTop = null;
	
	/* 结束日期 */
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date endDate = null;
	/* 结束日期 （查询范围下限） */
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date endDateBottom = null;
	/* 结束日期 （查询范围上限） */
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date endDateTop = null;
	
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
	 * @return 项目编号
	 */
	public Long getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId 项目编号
	 */
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return 参与人员编号，关联project_person_base.person_id
	 */
	public Long getPersonId() {
		return personId;
	}

	/**
	 * @param personId 参与人员编号，关联project_person_base.person_id
	 */
	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	/**
	 * @return 参与人员信息
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person 参与人员信息
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return 是否是钉子标识
	 */
	public Boolean getIsLeaderFlag() {
		return isLeaderFlag;
	}

	/**
	 * @param isLeaderFlag 是否是钉子标识
	 */
	public void setIsLeaderFlag(Boolean isLeaderFlag) {
		this.isLeaderFlag = isLeaderFlag;
	}

	/**
	 * @return 投入时间百分比
	 */
	public Double getTimePercent() {
		return timePercent;
	}

	/**
	 * @param timePercent 投入时间百分比
	 */
	public void setTimePercent(Double timePercent) {
		this.timePercent = timePercent;
	}

	/**
	 * @return 开始日期
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate 开始日期
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * @return 开始日期（查询范围下限）
	 */
	public Date getBeginDateBottom() {
		return beginDateBottom;
	}

	/**
	 * @param beginDateBottom 开始日期（查询范围下限）
	 */
	public void setBeginDateBottom(Date beginDateBottom) {
		this.beginDateBottom = beginDateBottom;
	}

	/**
	 * @return 开始日期（查询范围上限）
	 */
	public Date getBeginDateTop() {
		return beginDateTop;
	}

	/**
	 * @param beginDateTop 开始日期（查询范围上限）
	 */
	public void setBeginDateTop(Date beginDateTop) {
		this.beginDateTop = beginDateTop;
	}
	
	/**
	 * @return 结束日期
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate 结束日期
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return 结束日期（查询范围下限）
	 */
	public Date getEndDateBottom() {
		return endDateBottom;
	}

	/**
	 * @param endDateBottom 结束日期（查询范围下限）
	 */
	public void setEndDateBottom(Date endDateBottom) {
		this.endDateBottom = endDateBottom;
	}

	/**
	 * @return 结束日期（查询范围上限）
	 */
	public Date getEndDateTop() {
		return endDateTop;
	}

	/**
	 * @param endDateTop 结束日期（查询范围上限）
	 */
	public void setEndDateTop(Date endDateTop) {
		this.endDateTop = endDateTop;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (getId() == null)?0:getId().toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(getId() == null || obj == null || !(obj instanceof ProjectPerson)) {
			return false;
		}

		return getId().equals(((ProjectPerson)obj).getId());
	}
}
