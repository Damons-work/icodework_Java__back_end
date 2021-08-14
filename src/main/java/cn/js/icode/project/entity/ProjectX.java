package cn.js.icode.project.entity;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.KeyValue;

/**
 * 项目信息 - 实体
 * 对应数据库表：project_project_x
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
public class ProjectX {
	/* 项目编号（关键字） */
	private Long project_id = null;

	/* 项目名称 */
	private String projectName = null;
	
	/* 税后金额（元） */
	private Double afterTax = null;
	
	/* 状态标识{1:进行中，2:已完成，3：已删除} */
	private Integer statusFlag = null;
	
	/* 钉子编号，关联project_person_base.person_id */
	private Long leaderId = null;
	/* 钉子信息 */
	private Person leader = null;
	
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
	/* 参与人员列表 */
	private List<ProjectPerson> projectPersonList = null;
	
	/*
	 * 固定数据字典 状态{1:进行中，2:已完成，3：已删除}
	 */
	public static final String[] statusFlags = { "进行中", "已完成", "已删除" };

	/**
	 * @return 项目编号
	 */
	public Long getId() {
		return project_id;
	}

	/**
	 * @param project_id 项目编号
	 */
	public void setId(Long project_id) {
		this.project_id = project_id;
	}

	/**
	 * @return 项目名称
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName 项目名称
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return 税后金额（元）
	 */
	public Double getAfterTax() {
		return afterTax;
	}

	/**
	 * @param afterTax 税后金额（元）
	 */
	public void setAfterTax(Double afterTax) {
		this.afterTax = afterTax;
	}

	/**
	 * @return 状态标识{1:进行中，2:已完成，3：已删除}
	 */
	public Integer getStatusFlag() {
		return statusFlag;
	}

	/**
	 * @param statusFlag 状态标识{1:进行中，2:已完成，3：已删除}
	 */
	public void setStatusFlag(Integer statusFlag) {
		this.statusFlag = statusFlag;
	}

	/**
	 * @return 静态数据字典：状态{1:进行中，2:已完成，3：已删除}
	 */
	public KeyValue getStatus() {
		Integer nFlag = getStatusFlag();
		String value = CommonMPI.getDictionaryName(statusFlags, nFlag);

		return new KeyValue(nFlag, value);
	}
	
	/**
	 * @return 钉子编号，关联project_person_base.person_id
	 */
	public Long getLeaderId() {
		return leaderId;
	}

	/**
	 * @param leaderId 钉子编号，关联project_person_base.person_id
	 */
	public void setLeaderId(Long leaderId) {
		this.leaderId = leaderId;
	}

	/**
	 * @return 钉子信息
	 */
	public Person getLeader() {
		return leader;
	}

	/**
	 * @param 钉子信息
	 */
	public void setLeader(Person leader) {
		this.leader = leader;
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
	
	/**
	 * @return 参与人员列表
	 */
	public List<ProjectPerson> getProjectPersonList() {
		return projectPersonList;
	}

	/**
	 * @param projectPersonList 参与人员列表
	 */
	public void setProjectPersonList(List<ProjectPerson> projectPersonList) {
		this.projectPersonList = projectPersonList;
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
		if(getId() == null || obj == null || !(obj instanceof ProjectX)) {
			return false;
		}

		return getId().equals(((ProjectX)obj).getId());
	}
}
