package cn.js.icode.project.entity;

/**
 * 项目成本 - 实体
 * 对应数据库表：project_project_cost
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
public class ProjectCost {
	/* 成本编号（关键字） */
	private Long cost_id = null;

	/* 项目编号，关联project_project_base.project_id */
	private Long projectId = null;
	
	/* 所属年月，yyyyMM格式，如：202104 */
	private Integer yearMonth = null;
	
	/* 人员编号，关联project_person_base.person_id */
	private Long personId = null;
	/* 人员信息 */
	private Person person = null;
	
	/* 月底或者当前日期（是几号？） */
	private Integer toDay = null;
	
	/* 产生的成本（元） */
	private Double personCost = null;
	
	/**
	 * @return 成本编号
	 */
	public Long getId() {
		return cost_id;
	}

	/**
	 * @param cost_id 成本编号
	 */
	public void setId(Long cost_id) {
		this.cost_id = cost_id;
	}

	/**
	 * @return 项目编号，关联project_project_base.project_id
	 */
	public Long getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId 项目编号，关联project_project_base.project_id
	 */
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return 所属年月，yyyyMM格式，如：202104
	 */
	public Integer getYearMonth() {
		return yearMonth;
	}

	/**
	 * @param yearMonth 所属年月，yyyyMM格式，如：202104
	 */
	public void setYearMonth(Integer yearMonth) {
		this.yearMonth = yearMonth;
	}

	/**
	 * @return 人员编号，关联project_person_base.person_id
	 */
	public Long getPersonId() {
		return personId;
	}

	/**
	 * @param personId 人员编号，关联project_person_base.person_id
	 */
	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	/**
	 * @return 人员信息
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person 人员信息
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return 月底或者当前日期（是几号？）
	 */
	public Integer getToDay() {
		return toDay;
	}

	/**
	 * @param toDay 月底或者当前日期（是几号？）
	 */
	public void setToDay(Integer toDay) {
		this.toDay = toDay;
	}

	/**
	 * @return 产生的成本（元）
	 */
	public Double getPersonCost() {
		return personCost;
	}

	/**
	 * @param personCost 产生的成本（元）
	 */
	public void setPersonCost(Double personCost) {
		this.personCost = personCost;
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
		if(getId() == null || obj == null || !(obj instanceof ProjectCost)) {
			return false;
		}

		return getId().equals(((ProjectCost)obj).getId());
	}
}
