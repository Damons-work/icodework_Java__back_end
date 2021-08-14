package cn.js.icode.project.entity;

/**
 * 人员信息 - 实体
 * 对应数据库表：project_person_base
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
public class Person {
	/* 人员编号（关键字） */
	private Long person_id = null;

	/* 姓名 */
	private String personName = null;
	
	/* 成本（元） */
	private Double cost = null;
	
	/* 微信小程序id */
	private String wxOpenId = null;
	
	/* 是否有效标识 */
	private Boolean activeFlag = null;
	
	/**
	 * @return 人员编号
	 */
	public Long getId() {
		return person_id;
	}

	/**
	 * @param person_id 人员编号
	 */
	public void setId(Long person_id) {
		this.person_id = person_id;
	}

	/**
	 * @return 姓名
	 */
	public String getPersonName() {
		return personName;
	}

	/**
	 * @param personName 姓名
	 */
	public void setPersonName(String personName) {
		this.personName = personName;
	}

	/**
	 * @return 成本（元）
	 */
	public Double getCost() {
		return cost;
	}

	/**
	 * @param cost 成本（元）
	 */
	public void setCost(Double cost) {
		this.cost = cost;
	}

	/**
	 * @return 微信小程序id
	 */
	public String getWxOpenId() {
		return wxOpenId;
	}

	/**
	 * @param wxOpenId 微信小程序id
	 */
	public void setWxOpenId(String wxOpenId) {
		this.wxOpenId = wxOpenId;
	}

	/**
	 * @return 是否有效标识
	 */
	public Boolean getActiveFlag() {
		return activeFlag;
	}

	/**
	 * @param activeFlag 是否有效标识
	 */
	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
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
		if(getId() == null || obj == null || !(obj instanceof Person)) {
			return false;
		}

		return getId().equals(((Person)obj).getId());
	}
}
