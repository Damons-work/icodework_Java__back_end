package cn.weixin.popular.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.weixin.popular.bean.sns.SnsToken;
import cn.weixin.popular.bean.user.User;
import team.bangbang.common.net.http.HttpClient;
import team.bangbang.common.net.http.ResponseHandler;

/**
 * 网页授权
 * 
 * @author LiYi
 *
 */
public class SnsAPI extends BaseAPI {
	private static Logger log = LoggerFactory.getLogger(SnsAPI.class);
	private static HttpClient hc = new HttpClient();

	/**
	 * 通过code换取网页授权access_token (第三方平台开发)
	 * 
	 * @param appid
	 *            appid
	 * @param code
	 *            code
	 * @param component_appid
	 *            服务开发方的appid
	 * @param component_access_token
	 *            服务开发方的access_token
	 * @return SnsToken
	 */
	public static SnsToken oauth2ComponentAccessToken(String appid, String code, String component_appid,
			String component_access_token) {
		ResponseHandler rh = null;
		String url = BASE_URI + "/sns/oauth2/component/access_token";

		try {
			Map<String, String> param = new HashMap<String, String>();
			param.put("appid", appid);
			param.put("code", code);
			param.put("grant_type", "authorization_code");
			param.put("component_appid", component_appid);
			param.put("component_access_token", component_access_token);

			rh = hc.post(url, param);

		} catch (IOException ex) {
			log.error(ex.getMessage());
		}

		return (rh == null ? null : rh.toJavaObject(SnsToken.class));
	}

	/**
	 * 生成网页授权 URL
	 * 
	 * @param appid
	 *            appid
	 * @param redirect_uri
	 *            自动URLEncoder
	 * @param snsapi_userinfo
	 *            snsapi_userinfo
	 * @param state
	 *            可以为空
	 * @return url
	 */
	public static String connectOauth2Authorize(String appid, String redirect_uri, boolean snsapi_userinfo,
			String state) {
		return connectOauth2Authorize(appid, redirect_uri, snsapi_userinfo, state, null);
	}

	/**
	 * 生成网页授权 URL (第三方平台开发)
	 * 
	 * @param appid
	 *            appid
	 * @param redirect_uri
	 *            自动URLEncoder
	 * @param snsapi_userinfo
	 *            snsapi_userinfo
	 * @param state
	 *            可以为空
	 * @param component_appid
	 *            第三方平台开发，可以为空。 服务方的appid，在申请创建公众号服务成功后，可在公众号服务详情页找到
	 * @return url
	 */
	public static String connectOauth2Authorize(String appid, String redirect_uri, boolean snsapi_userinfo,
			String state, String component_appid) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(OPEN_URI + "/connect/oauth2/authorize?").append("appid=").append(appid).append("&redirect_uri=")
					.append(URLEncoder.encode(redirect_uri, "utf-8")).append("&response_type=code").append("&scope=")
					.append(snsapi_userinfo ? "snsapi_userinfo" : "snsapi_base").append("&state=")
					.append(state == null ? "" : state);
			if (component_appid != null) {
				sb.append("&component_appid=").append(component_appid);
			}
			sb.append("#wechat_redirect");
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 拉取用户信息(需scope为 snsapi_userinfo)
	 * 
	 * @param access_token
	 *            网页授权access_token
	 * @param openid
	 *            openid
	 * @param lang
	 *            国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
	 * @return User
	 */
	public static User userinfo(String access_token, String openid, String lang) {
		ResponseHandler rh = null;
		String url = BASE_URI + "/sns/userinfo?" + PARAM_ACCESS_TOKEN + "=" + access_token + "&openid=" + openid
				+ "&lang=" + lang;

		try {
			rh = hc.get(url);
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}

		return (rh == null ? null : rh.toJavaObject(User.class));
	}

	/**
	 * 生成网页授权 URL (网站应用微信登录) 2.5.3
	 * 
	 * @param appid
	 *            appid
	 * @param redirect_uri
	 *            自动URLEncoder
	 * @param state
	 *            可以为空
	 * @return url
	 */
	public static String connectQrconnect(String appid, String redirect_uri, String state) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(OPEN_URI + "/connect/qrconnect?").append("appid=").append(appid).append("&redirect_uri=")
					.append(URLEncoder.encode(redirect_uri, "utf-8")).append("&response_type=code")
					.append("&scope=snsapi_login").append("&state=").append(state == null ? "" : state)
					.append("#wechat_redirect");
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
