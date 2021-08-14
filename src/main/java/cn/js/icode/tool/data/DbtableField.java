package cn.js.icode.tool.data;

/**
 * 数据表字段 - POJO
 * 对应数据库表：tool_dbtable_field
 *
 * @author ICode Studio
 * @version 1.0  2018-10-21
 */
public class DbtableField {
	/* 字段编号（关键字） */
	private Long fieldId = null;
	/* 数据表编号，关联tool_dbtable_base.DbtableId */
	private Long dbtableId = null;
	/* 字段名称 */
	private String fieldName = null;
	/* 字段类型 */
	private String fieldType = null;
	/* 字段长度 */
	private String fieldLength = null;
	/* 是否主键 */
	private Boolean isPk = null;
	/* 是否非空 */
	private Boolean isNotNull = null;
	/* 字段描述 */
	private String fieldDescription = null;
	/* 排序数字 */
	private Integer orderBy = null;
	/**
	 * @return 字段编号
	 */
	public Long getId() {
		return fieldId;
	}
	/**
	 * @param fieldId 字段编号
	 */
	public void setId(Long fieldId) {
		this.fieldId = fieldId;
	}
	/**
	 * @return 数据表编号，关联tool_dbtable_base.DbtableId
	 */
	public Long getDbtableId() {
		return dbtableId;
	}
	/**
	 * @param dbtableId 数据表编号，关联tool_dbtable_base.DbtableId
	 */
	public void setDbtableId(Long dbtableId) {
		this.dbtableId = dbtableId;
	}
	/**
	 * @return 字段名称
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @return 属性名称
	 */
	public String getPropertyName() {
		// 以_进行拆分，每个首字母大写
		if (fieldName == null || fieldName.trim().length() == 0) {
			return fieldName;
		}
		
		String[] ss = fieldName.split("_");
		StringBuffer sb = new StringBuffer();
		for(int i = 0; ss != null && i < ss.length; i++) {
			String s = ss[i];
			if (s == null || s.length() == 0) continue;
			if (s.length() == 1) {
				sb.append(s.toUpperCase());
			} else {
				sb.append(s.substring(0, 1).toUpperCase() ).append(s.substring(1));
			}
		}
		return sb.toString();
	}
	/**
	 * @param fieldName 字段名称
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * @return 字段类型
	 */
	public String getFieldType() {
		return fieldType;
	}
	/**
	 * @param fieldType 字段类型
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	/**
	 * @return 字段长度
	 */
	public String getFieldLength() {
		return fieldLength;
	}
	/**
	 * @param fieldLength 字段长度
	 */
	public void setFieldLength(String fieldLength) {
		this.fieldLength = fieldLength;
	}
	/**
	 * @return 是否主键
	 */
	public Boolean getIsPk() {
		return isPk;
	}
	/**
	 * @param isPk 是否主键
	 */
	public void setIsPk(Boolean isPk) {
		this.isPk = isPk;
	}
	/**
	 * @return 是否非空
	 */
	public Boolean getIsNotNull() {
		return isNotNull;
	}
	/**
	 * @param isNotNull 是否非空
	 */
	public void setIsNotNull(Boolean isNotNull) {
		this.isNotNull = isNotNull;
	}
	/**
	 * @return 字段描述
	 */
	public String getFieldDescription() {
		return fieldDescription;
	}
	/**
	 * @param fieldDescription 字段描述
	 */
	public void setFieldDescription(String fieldDescription) {
		this.fieldDescription = fieldDescription;
	}
	/**
	 * @return 排序数字
	 */
	public Integer getOrderBy() {
		return orderBy;
	}
	/**
	 * @param orderBy 排序数字
	 */
	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}
	
	/**
	 * @return 当前字段是否是固定数据字典字段
	 */
	public boolean isFixDict() {
		if(getFieldName() == null || getFieldType() == null) {
			return false;
		}
		
		return getFieldType().equals("integer") && getPropertyName().endsWith("Flag");
	}
	
	/**
	 * @return 当前字段是否是动态数据字典字段
	 */
	public boolean isDynamicDict() {
		if(getFieldName() == null || getFieldType() == null) {
			return false;
		}
		
		return getFieldType().equals("string") && getPropertyName().endsWith("Code");
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
		if(getId() == null || obj == null || !(obj instanceof DbtableField)) {
			return false;
		}
		return getId().equals(((DbtableField)obj).getId());
	}
}
