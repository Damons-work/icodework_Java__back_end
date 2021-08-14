package cn.js.icode.config.data;

/**
 * 系统参数 - POJO
 * 对应数据库表：config_parameter_base
 *
 * @author ICode Studio
 * @version 1.0  2018-10-13
 */
public class Parameter {
	/* 参数编号（关键字） */
	private Long parameterId = null;
	/* 参数名称 */
	private String parameterName = null;
	/* 参数数值 */
	private String parameterValue = null;
	/* 所属模块 */
	private String module = null;
	/* 参数说明 */
	private String remark = null;
	/**
	 * @return 参数编号
	 */
	public Long getId() {
		return parameterId;
	}
	/**
	 * @param parameterId 参数编号
	 */
	public void setId(Long parameterId) {
		this.parameterId = parameterId;
	}
	/**
	 * @return 参数名称
	 */
	public String getParameterName() {
		return parameterName;
	}
	/**
	 * @param parameterName 参数名称
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	/**
	 * @return 参数数值
	 */
	public String getParameterValue() {
		return parameterValue;
	}
	/**
	 * @param parameterValue 参数数值
	 */
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
	/**
	 * @return 所属模块
	 */
	public String getModule() {
		return module;
	}
	/**
	 * @param module 所属模块
	 */
	public void setModule(String module) {
		this.module = module;
	}
	/**
	 * @return 参数说明
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @param remark 参数说明
	 */
	public void setRemark(String remark) {
		this.remark = remark;
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
		if(getId() == null || obj == null || !(obj instanceof Parameter)) {
			return false;
		}
		return getId().equals(((Parameter)obj).getId());
	}
}
