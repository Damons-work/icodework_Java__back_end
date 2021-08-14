package cn.js.icode.basis.data;

import java.util.Date;

/**
 * 商户信息 - POJO
 * 对应数据库表：basis_partner_base
 * 
 * @author ICode Studio
 * @version 1.0  2018-12-02
 */
public class Partner {
	/* 商户编号（关键字） */
	private String partnerId = null;

	/* 商户名称 */
	private String partnerName = null;
	
	/* 联系人 */
	private String contactPerson = null;
	
	/* 联系电话 */
	private String phone = null;
	
	/* 电子信箱 */
	private String email = null;
	
	/* 是否有效 */
	private Boolean activeFlag = null;
	
	/* 创建时间 */
	private Date createTime = null;
	/* 创建时间 （查询上线） */
	private Date createTimeTop = null;
	/* 创建时间 （查询下线） */
	private Date createTimeBottom = null;
	
	/**
	 * @return 商户编号
	 */
	public String getId() {
		return partnerId;
	}

	/**
	 * @param partnerId 商户编号
	 */
	public void setId(String partnerId) {
		this.partnerId = partnerId;
	}

	/**
	 * @return 商户名称
	 */
	public String getPartnerName() {
		return partnerName;
	}

	/**
	 * @param partnerName 商户名称
	 */
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	
	/**
	 * @return 联系人
	 */
	public String getContactPerson() {
		return contactPerson;
	}

	/**
	 * @param contactPerson 联系人
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	
	/**
	 * @return 联系电话
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone 联系电话
	 */
	public void setPhone(String phone) {
		this.phone = phone;
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
	 * @return 是否有效
	 */
	public Boolean getActiveFlag() {
		return activeFlag;
	}

	/**
	 * @param activeFlag 是否有效
	 */
	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	
	/**
	 * @return 创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime 创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return 创建时间（查询上线）
	 */
	public Date getCreateTimeTop() {
		return createTimeTop;
	}

	/**
	 * @param createTimeTop 创建时间（查询上线）
	 */
	public void setCreateTimeTop(Date createTimeTop) {
		this.createTimeTop = createTimeTop;
	}

	/**
	 * @return 创建时间（查询下线）
	 */
	public Date getCreateTimeBottom() {
		return createTimeBottom;
	}

	/**
	 * @param createTimeBottom 创建时间（查询下线）
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
		if(getId() == null || obj == null || !(obj instanceof Partner)) {
			return false;
		}

		return getId().equals(((Partner)obj).getId());
	}
}
