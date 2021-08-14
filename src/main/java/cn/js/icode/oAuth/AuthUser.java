package cn.js.icode.oAuth;

/**
 * 开放授权的用户信息
 *
 * @author ICode Studio
 * @version 1.0  2019-10-27
 */
public class AuthUser {
	/* 类型标识{1：微信2：支付宝} */
	private Integer typeFlag = null;
	/* unionId */
	private String unionId = null;
	/* openId */
	private String openId = null;
	/* 昵称 */
	private String nickname = null;
	/* 电子信箱 */
	private String email = null;
	/* 手机号码 */
	private String mobile = null;
	/* 性别标识{1：男2：女} */
	private Integer sexFlag = null;
	/* 国家 */
	private String country = null;
	/* 省份 */
	private String province = null;
	/* 城市 */
	private String city = null;
	/* 头像图片地址 */
	private String headimgurl = null;
	/**
	 * @return 类型标识{1：微信2：支付宝} 
	 */
	public Integer getTypeFlag() {
		return typeFlag;
	}
	/**
	 * @param typeFlag 类型标识{1：微信2：支付宝} 
	 */
	public void setTypeFlag(Integer typeFlag) {
		this.typeFlag = typeFlag;
	}
	/**
	 * @return unionId
	 */
	public String getUnionId() {
		return unionId;
	}
	/**
	 * @param unionId unionId
	 */
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	/**
	 * @return openId
	 */
	public String getOpenId() {
		return openId;
	}
	/**
	 * @param openId openId
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	/**
	 * @return 昵称
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * @param nickname 昵称
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
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
	 * @return 手机号码
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile 手机号码
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * @return 性别标识{1：男2：女} 
	 */
	public Integer getSexFlag() {
		return sexFlag;
	}
	/**
	 * @param sexFlag 性别标识{1：男2：女} 
	 */
	public void setSexFlag(Integer sexFlag) {
		this.sexFlag = sexFlag;
	}
	/**
	 * @return 国家
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country 国家
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return 省份
	 */
	public String getProvince() {
		return province;
	}
	/**
	 * @param province 省份
	 */
	public void setProvince(String province) {
		this.province = province;
	}
	/**
	 * @return 城市
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city 城市
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return 头像图片地址
	 */
	public String getHeadimgurl() {
		return headimgurl;
	}
	/**
	 * @param headimgurl 头像图片地址
	 */
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
}
