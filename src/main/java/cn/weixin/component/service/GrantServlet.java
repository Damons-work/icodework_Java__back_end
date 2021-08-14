package cn.weixin.component.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.js.icode.oAuth.weixin.data.AuthorizerGrant;
import cn.js.icode.oAuth.weixin.service.AuthorizerGrantService;
import cn.weixin.component.manager.AuthorizerManager;
import cn.weixin.component.manager.ComponentManager;
import cn.weixin.mp.aes.AesException;
import cn.weixin.mp.aes.WXBizMsgCrypt;
import cn.weixin.popular.api.ComponentAPI;
import cn.weixin.popular.bean.component.ApiGetAuthorizerInfoResult;
import cn.weixin.popular.bean.component.ApiGetAuthorizerInfoResult.Authorizer_info;
import cn.weixin.popular.bean.component.ApiQueryAuthResult;
import cn.weixin.popular.bean.component.ApiQueryAuthResult.Authorization_info;
import team.bangbang.common.CommonMPI;
import team.bangbang.common.config.Config;
import team.bangbang.common.utility.LogicUtility;

//************************************************************************
/**
 * 微信公众号第三方平台授权事件接收URL
 * 
 * 在web.xml中的定义可以传入以下参数：
 * <ul>
 * <li>debug：布尔型，是否输出debug信息</li>
 * <li>resultUrl：成功跳转页面</li>
 * </ul>
 * 
 * <pre>
 * 1. 当前servlet的url-pattern必须为：
 * 		/common/weixin/grant			# 授权事件接收URL
 * </pre>
 * 
 * @author ICode Studio
 * @version 1.0 2016-12-29
 */
// ************************************************************************
@WebServlet(urlPatterns = "/common/weixin/grant")
public class GrantServlet extends HttpServlet {
	private static final long serialVersionUID = -2528310861419613962L;
	private static Logger log = LoggerFactory.getLogger(GrantServlet.class);
	private static WXBizMsgCrypt wxbmc = null;
	/* 是否为debug状态 */
	private static boolean debug = false;

	/**
	 * 处理回调请求
	 * 
	 * @param request
	 *            HTTP请求
	 * @param response
	 *            HTTP响应
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 日志消息
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("FromIp", getRemoteIP(request));
		jsonMessage.put("EnterTime", System.currentTimeMillis());

		// 请求的参数
		JSONObject params = readUrlParams(request);
		JSONObject body = readBody(request);

		jsonMessage.put("UrlParameter", params);
		jsonMessage.put("BodyData", body);

		if (params == null) {
			params = new JSONObject();
		}

		if (body == null) {
			body = new JSONObject();
		}
		String xmlData = null;
		if (body.has("xmlData")) {
			xmlData = body.getString("xmlData");
			body.remove("xmlData");
			// jsonMessage.put("xmlData", xmlData);
		}

		if (debug) {
			log.debug("地址参数：" + params);
			log.debug("Body参数：" + body);
			if (xmlData != null) {
				log.debug("XmlData：" + xmlData);
			}
		}

		JSONObject jData = new JSONObject();
		// 合并参数
		if (params != null) {
			for (Object key : params.keySet()) {
				Object value = params.get((String) key);
				jData.put((String) key, value);
			}
		}

		if (body != null) {
			for (Object key : body.keySet()) {
				Object value = body.get((String) key);
				jData.put((String) key, value);
			}
		}
		// 第三方平台的componentAppId
		if (jData.has("Encrypt")) {
			jData.put("componentAppId", ComponentManager.COMPONENT_APPID);
			JSONObject jcontent = decrypt(jData);
			jData.put("BodyDecrypt", jcontent);
			jsonMessage.put("BodyDecrypt", jcontent);
		}

		// 推送了auth_code
		// 调用/common/grant
		String auth_code = (jData.has("auth_code") ? jData.getString("auth_code") : null);
		if (auth_code == null || auth_code.trim().length() == 0) {
			return;
		}

		// 第三方平台的component_access_token
		String component_access_token = ComponentManager.getComponentAccessToken();

		// 使用授权码获取access_token
		ApiQueryAuthResult aar = ComponentAPI.api_query_auth(component_access_token, ComponentManager.COMPONENT_APPID, auth_code);

		Authorization_info ai = aar.getAuthorization_info();

		jsonMessage.put("AUTHORIZER_REFRESH_TOKEN", ai.getAuthorizer_refresh_token());

		// 获得公众号的信息
		ApiGetAuthorizerInfoResult agair = ComponentAPI.api_get_authorizer_info(component_access_token, ComponentManager.COMPONENT_APPID,
				ai.getAuthorizer_appid());

		if (!agair.isSuccess()) {
			return;
		}

		Authorizer_info ai2 = agair.getAuthorizer_info();

		// 检查该公众号是否已经绑定
		AuthorizerGrant form = new AuthorizerGrant();
		form.setAuthorizerId(ai.getAuthorizer_appid());
		
		AuthorizerGrant unit = AuthorizerGrantService.getObject(form, null);
		AuthorizerGrant ag = new AuthorizerGrant();
		
		if(unit == null) {
			ag.setAuthorizerId(ai.getAuthorizer_appid());
			ag.setAuthorizerName(ai2.getNick_name());
			ag.setHeadImage(ai2.getHead_img());
			ag.setRefreshToken(ai.getAuthorizer_refresh_token());
			ag.setGrantTime(new Date());
	
			// 将授权信息保存到数据库中
			AuthorizerGrantService.insert(ag);
		} else {
			ag.setId(unit.getId());
			// 已经存在
			ag.setAuthorizerId(ai.getAuthorizer_appid());
			ag.setAuthorizerName(ai2.getNick_name());
			ag.setHeadImage(ai2.getHead_img());
			ag.setRefreshToken(ai.getAuthorizer_refresh_token());
			ag.setGrantTime(new Date());
	
			// 将授权信息保存到数据库中
			AuthorizerGrantService.update(ag, null, ag);
		}

		AuthorizerManager.init(ag.getId());

		// 转向到绑定成功页面
		String url = CommonMPI.getApplicationUrl(request) + "weixin/afterGrant.do?grantId=" + ag.getId();
		response.sendRedirect(url);

		jsonMessage.put("Authorizer", JSONObject.valueToString(ai2));
		if (debug) {
			log.debug(jsonMessage.toString());
		}
	}

	/**
	 * 读取HTTP请求GET参数（转化为JSON格式）
	 * 
	 * @param request
	 *            HTTP请求
	 * @return HTTP请求GET参数（转化为JSON格式）
	 * @throws IOException
	 */
	private JSONObject readUrlParams(HttpServletRequest request) throws IOException {
		// 获取GET请求参数
		JSONObject params = new JSONObject();
		// 地址参数
		Enumeration<String> er = request.getParameterNames();
		while (er != null && er.hasMoreElements()) {
			String key = er.nextElement();
			String value = request.getParameter(key);
			params.put(key, value);
		}

		return params;
	}

	/**
	 * 读取HTTP请求BODY部分的数据（JSON格式）
	 * 
	 * @param request
	 *            HTTP请求
	 * @return HTTP请求BODY部分的数据（JSON格式）
	 * @throws IOException
	 */
	private JSONObject readBody(HttpServletRequest request) throws IOException {
		// HTTP请求BODY部分的数据（JSON格式）
		JSONObject json = null;

		// 读取HTTP请求body部分的数据
		InputStream is = null;
		// body部分的数据
		byte[] body = null;
		try {
			is = request.getInputStream();
			if (is == null) {
				return json;
			}

			body = LogicUtility.readInputStream(is);
		} catch (Exception ex) {
		} finally {
			if (is != null)
				is.close();
		}

		if (body == null || body.length == 0) {
			return json;
		}

		// 格式: json或者xml
		String s2 = new String(body, "UTF-8");
		// URLDecoder.decode(s, "UTF-8");

		// 尝试以json方式解析
		try {
			json = new JSONObject(s2);
		} catch (Exception ex) {
		}

		try {
			if (json == null) {
				// 尝试以xml方式解析
				json = XML.toJSONObject(s2);

				if (json.has("xml") && json.keySet().size() == 1) {
					// return json.getJSONObject("xml").getString("Encrypt");
					json = json.getJSONObject("xml");

					// xml文档，添加到返回结果中
					json.put("xmlData", s2);
				}
			}
		} catch (Exception ex) {
		}
		// 解析QueryString
		if (json == null || json.keySet().isEmpty()) {
			String[] ss = LogicUtility.splitString(s2, "&");
			if (ss != null && ss.length > 0) {
				json = new JSONObject();
				for (int i = 0; i < ss.length; i++) {
					String temp = ss[i];
					if (temp == null || temp.indexOf("=") <= 0) {
						continue;
					}
					int index = temp.indexOf("=");
					String key = temp.substring(0, index);
					String value = URLDecoder.decode(temp.substring(index + 1), "UTF-8");
					json.put(key, value);
				}
			}
		}

		return json;
	}

	private JSONObject decrypt(JSONObject jData) {
		JSONObject result = new JSONObject();

		if (!jData.has("Encrypt")) {
			result.put("errmsg", "no encrypt data (0x02)");
			return result;
		}

		String encrypt = jData.getString("Encrypt");

		// 加密的信息解密
		try {
			// msgSignature
			String msgSignature = jData.getString("msg_signature");
			// 时间戳
			String timeStamp = jData.getString("timestamp");
			// 随机字符串
			String nonce = jData.getString("nonce");
			String componentAppId = jData.getString("componentAppId");

			WXBizMsgCrypt mc = getWXBizMsgCrypt(componentAppId);
			// 获得解密后的内容
			String s = mc.decryptMsg(msgSignature, timeStamp, nonce, encrypt, componentAppId);
			// if (debug) {
			// log.debug(s);
			// }
			result = XML.toJSONObject(s);
			result = result.getJSONObject("xml");
		} catch (Exception e) {
			result.put("errmsg", e.getMessage());
			return result;
		}

		return result;
	}

	private WXBizMsgCrypt getWXBizMsgCrypt(String componentAppId) {
		if (wxbmc != null) {
			return wxbmc;
		}

		// 第三方平台的消息校验token
		String vtoken = Config.getProperty("open.weixin.message.verify.token");
		// 第三方平台的消息加密KEY
		String ekey = Config.getProperty("open.weixin.message.encrypt.key");
		try {
			wxbmc = new WXBizMsgCrypt(vtoken, ekey, componentAppId);
		} catch (AesException e) {
			e.printStackTrace();
		}

		return wxbmc;
	}

	private String getRemoteIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}

		return request.getHeader("x-forwarded-for");
	}
}