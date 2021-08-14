package cn.js.icode.oAuth.weixin.data;
import java.util.Date;

/**
 * 公众号/小程序授权信息 - POJO
 * 对应数据库表：weixin_authorizer_grant
 *
 * @author ICode Studio
 * @version 1.0  2018-07-01
 */
public class AuthorizerGrant {
	/* 授权编号（关键字） */
	private String grantId = null;
	/* 微信公众号/小程序AppId */
	private String authorizerId = null;
	/* 微信公众号/小程序名称 */
	private String authorizerName = null;
	/* 微信公众号/小程序徽标地址 */
	private String headImage = null;
	/* 刷新Token */
	private String refreshToken = null;
	/* 授权时间 */
	private Date grantTime = null;
	/* 授权时间 （查询上线） */
	private Date grantTimeTop = null;
	/* 授权时间 （查询下线） */
	private Date grantTimeBottom = null;
	
	/**
	 * @return 授权编号
	 */
	public String getId() {
		return grantId;
	}
	/**
	 * @param grantId 授权编号
	 */
	public void setId(String grantId) {
		this.grantId = grantId;
	}
	/**
	 * @return 微信公众号/小程序AppId
	 */
	public String getAuthorizerId() {
		return authorizerId;
	}
	/**
	 * @param authorizerId 微信公众号/小程序AppId
	 */
	public void setAuthorizerId(String authorizerId) {
		this.authorizerId = authorizerId;
	}
	/**
	 * @return 微信公众号/小程序名称
	 */
	public String getAuthorizerName() {
		return authorizerName;
	}
	/**
	 * @param authorizerName 微信公众号/小程序名称
	 */
	public void setAuthorizerName(String authorizerName) {
		this.authorizerName = authorizerName;
	}
	/**
	 * @return 微信公众号/小程序徽标地址
	 */
	public String getHeadImage() {
		return headImage;
	}
	/**
	 * @param headImage 微信公众号/小程序徽标地址
	 */
	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}
	/**
	 * @return 刷新Token
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	/**
	 * @param refreshToken 刷新Token
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	/**
	 * @return 授权时间
	 */
	public Date getGrantTime() {
		return grantTime;
	}
	/**
	 * @param grantTime 授权时间
	 */
	public void setGrantTime(Date grantTime) {
		this.grantTime = grantTime;
	}
	/**
	 * @return 授权时间（查询上线）
	 */
	public Date getGrantTimeTop() {
		return grantTimeTop;
	}
	/**
	 * @param grantTimeTop 授权时间（查询上线）
	 */
	public void setGrantTimeTop(Date grantTimeTop) {
		this.grantTimeTop = grantTimeTop;
	}
	/**
	 * @return 授权时间（查询下线）
	 */
	public Date getGrantTimeBottom() {
		return grantTimeBottom;
	}
	/**
	 * @param grantTimeBottom 授权时间（查询下线）
	 */
	public void setGrantTimeBottom(Date grantTimeBottom) {
		this.grantTimeBottom = grantTimeBottom;
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
		if(getId() == null || obj == null || !(obj instanceof AuthorizerGrant)) {
			return false;
		}
		return getId().equals(((AuthorizerGrant)obj).getId());
	}
}
