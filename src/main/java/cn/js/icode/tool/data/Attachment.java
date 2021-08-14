package cn.js.icode.tool.data;

/**
 * 附件信息 - POJO
 * 对应数据库表：tool_attachment_base
 * 
 * @author ICode Studio
 * @version 1.0  2020-05-27
 */
public class Attachment {
	/* 附件编号（关键字） */
	private Long attachmentId = null;

	/* 业务记录编号 */
	private String bizId = null;
	
	/* 附件标题 */
	private String title = null;
	
	/* 附件地址 */
	private String fileUrl = null;
	
	/* 附件序号，从1开始，用于排序 */
	private Integer fileNo = null;
	
	/**
	 * @return 附件编号
	 */
	public Long getId() {
		return attachmentId;
	}

	/**
	 * @param attachmentId 附件编号
	 */
	public void setId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	/**
	 * @return 业务记录编号
	 */
	public String getBizId() {
		return bizId;
	}

	/**
	 * @param bizId 业务记录编号
	 */
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	
	/**
	 * @return 附件标题
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title 附件标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return 附件地址
	 */
	public String getFileUrl() {
		return fileUrl;
	}

	/**
	 * @param fileUrl 附件地址
	 */
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	
	/**
	 * @return 附件序号，从1开始，用于排序
	 */
	public Integer getFileNo() {
		return fileNo;
	}

	/**
	 * @param fileNo 附件序号，从1开始，用于排序
	 */
	public void setFileNo(Integer fileNo) {
		this.fileNo = fileNo;
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
		if(getId() == null || obj == null || !(obj instanceof Attachment)) {
			return false;
		}

		return getId().equals(((Attachment)obj).getId());
	}
}
