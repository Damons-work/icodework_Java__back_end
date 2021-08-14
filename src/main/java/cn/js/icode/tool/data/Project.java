package cn.js.icode.tool.data;
import java.util.Date;

import cn.js.icode.system.data.User;
/**
 * 工程 - POJO
 * 对应数据库表：tool_project_base
 *
 * @author ICode Studio
 * @version 1.0  2018-10-08
 */
public class Project {
	/* 工程编号（关键字） */
	private Long projectId = null;
	/* 工程名称 */
	private String projectName = null;
	/* 工程说明 */
	private String remark = null;
	/* 创建人编号 */
	private Long creatorId = null;
	/* 创建人 */
	private User creator = null;
	/* 创建时间 */
	private Date createTime = null;
	/* 创建时间 （查询上线） */
	private Date createTimeTop = null;
	/* 创建时间 （查询下线） */
	private Date createTimeBottom = null;
	/**
	 * @return 工程编号
	 */
	public Long getId() {
		return projectId;
	}
	/**
	 * @param projectId 工程编号
	 */
	public void setId(Long projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return 工程名称
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @param projectName 工程名称
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	/**
	 * @return 工程说明
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @param remark 工程说明
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * @return 创建人编号
	 */
	public Long getCreatorId() {
		return creatorId;
	}
	/**
	 * @param creatorId 创建人编号
	 */
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	
	/**
	 * @return 创建人
	 */
	public User getCreator() {
		return creator;
	}
	/**
	 * @param creator 创建人
	 */
	public void setCreator(User creator) {
		this.creator = creator;
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
		if(getId() == null || obj == null || !(obj instanceof Project)) {
			return false;
		}
		return getId().equals(((Project)obj).getId());
	}
}
