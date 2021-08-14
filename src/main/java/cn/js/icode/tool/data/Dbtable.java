package cn.js.icode.tool.data;
import java.util.Date;
import java.util.List;
/**
 * 数据表 - POJO
 * 对应数据库表：tool_dbtable_base
 *
 * @author ICode Studio
 * @version 1.0  2018-10-21
 */
public class Dbtable {
	/* 表编号（关键字） */
	private Long dbtableId = null;
	/* 工程编号，关联tool_project_base.ProjectId */
	private Long projectId = null;
	/* 工程信息 */
	private Project project = null;
	/* Code包前缀 */
	private String packagePrefix = null;
	/* 数据表名称 */
	private String tableName = null;
	/* 数据表说明 */
	private String tableDescription = null;	
	/* 评估的基本工作量(人.天) */
	private Double workload = null;
	/* 创建人编号 */
	private Long creatorId = null;
	/* 创建时间 */
	private Date createTime = null;
	/* 创建时间 （查询上线） */
	private Date createTimeTop = null;
	/* 创建时间 （查询下线） */
	private Date createTimeBottom = null;
	/* 字段列表 */
	private List<DbtableField> fieldList = null;
	
	/**
	 * @return 表编号
	 */
	public Long getId() {
		return dbtableId;
	}
	/**
	 * @param dbtableId 表编号
	 */
	public void setId(Long dbtableId) {
		this.dbtableId = dbtableId;
	}
	/**
	 * @return 工程编号，关联tool_project_base.ProjectId
	 */
	public Long getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId 工程编号，关联tool_project_base.ProjectId
	 */
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return 工程信息
	 */
	public Project getProject() {
		return project;
	}
	/**
	 * @param project 工程信息
	 */
	public void setProject(Project project) {
		this.project = project;
	}
	/**
	 * @return Code包前缀
	 */
	public String getPackagePrefix() {
		return packagePrefix;
	}
	/**
	 * @param packagePrefix Code包前缀
	 */
	public void setPackagePrefix(String packagePrefix) {
		this.packagePrefix = packagePrefix;
	}
	/**
	 * @return 数据表名称
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName 数据表名称
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return 数据表说明
	 */
	public String getTableDescription() {
		return tableDescription;
	}
	/**
	 * @param tableDescription 数据表说明
	 */
	public void setTableDescription(String tableDescription) {
		this.tableDescription = tableDescription;
	}	
	/**
	 * @return 评估的基本工作量(人.天)
	 */
	public Double getWorkload() {
		return workload;
	}
	/**
	 * @param workload 评估的基本工作量(人.天)
	 */
	public void setWorkload(Double workload) {
		this.workload = workload;
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
	
	/**
	 * @return 字段列表
	 */
	public List<DbtableField> getFieldList() {
		return fieldList;
	}
	/**
	 * @param fieldList 字段列表
	 */
	public void setFieldList(List<DbtableField> fieldList) {
		this.fieldList = fieldList;
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
		if(getId() == null || obj == null || !(obj instanceof Dbtable)) {
			return false;
		}
		return getId().equals(((Dbtable)obj).getId());
	}
}
