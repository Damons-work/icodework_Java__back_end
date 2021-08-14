package cn.weixin.popular.api;

public abstract class BaseAPI {
	protected static final String BASE_URI = "https://api.weixin.qq.com";
	protected static final String MEDIA_URI = "http://file.api.weixin.qq.com";
	protected static final String MP_URI = "https://mp.weixin.qq.com";
	protected static final String MCH_URI = "https://api.mch.weixin.qq.com";
	protected static final String OPEN_URI = "https://open.weixin.qq.com";
	
	protected static final String PARAM_ACCESS_TOKEN = "access_token";
	
	/**
	 * 获取 access token param name 名称
	 * 
	 * 2.6.0
	 * @return access_token or authorizer_access_token
	 */
	protected static String getATPN(){
		return PARAM_ACCESS_TOKEN;
	}
}
