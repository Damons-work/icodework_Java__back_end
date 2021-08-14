package cn.weixin.component.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
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

import cn.weixin.component.manager.ComponentManager;
import cn.weixin.mp.aes.AesException;
import cn.weixin.mp.aes.WXBizMsgCrypt;
import team.bangbang.common.config.Config;
import team.bangbang.common.utility.LogicUtility;

//************************************************************************
/**
 * 微信公众号消息与事件接收URL。
 * 
 * 在web.xml中的定义可以传入以下参数：
 * <ul>
 * <li>debug：布尔型，是否输出debug信息</li>
 * </ul>
 * 
 * <pre>
 * 1. 当前servlet的url-pattern必须为：
 * 		/common/weixin/authorizer/$APPID$	# 公众号消息与事件接收URL，传递AppId
 * </pre>
 * 
 * @author ICode Studio
 * @version 1.0 2016-12-29
 */
// ************************************************************************
@WebServlet(urlPatterns = "/common/weixin/authorizer/*")
public class AuthorizerServlet extends HttpServlet {
	private static final long serialVersionUID = -2528310861419613962L;
	private static Logger log = LoggerFactory.getLogger(AuthorizerServlet.class);
	private static WXBizMsgCrypt wxbmc = null;

	/* 是否为debug状态 */
	private boolean debug = true;

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

		// 获取请求的$APPID$
		String appId = getAppId(request);
		// 公众账号设置
		// AuthorizerGrant ag = getAuthorizerGrant(appId);
//		if (debug) {
//			log.info("AppId：" + appId);
//		}

		jsonMessage.put("AppId", appId);

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

//		if (debug) {
//			log.info("地址参数：" + params);
//			log.info("Body参数：" + body);
//			if (xmlData != null) {
//				log.info("XmlData：" + xmlData);
//			}
//		}

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

		if (jData.has("Encrypt") && xmlData != null) {
		    jData.put("componentAppId", ComponentManager.COMPONENT_APPID);
			JSONObject jcontent = decrypt(jData, xmlData);
			jData.put("BodyDecrypt", jcontent);
			jsonMessage.put("BodyDecrypt", jcontent);
		}

		// 3. 支付结果接收
		if (jData.has("transaction_id") && xmlData != null) {
			response.getWriter().println("");
			if(debug) log.debug(jsonMessage.toString());
			return;
		}

		// 10000. 最后处理接收到的用户消息
		if (jData.has("BodyDecrypt") && jData.getJSONObject("BodyDecrypt").has("MsgType")) {
			response.getWriter().println("");
			if(debug) log.debug(jsonMessage.toString());
			return;
		}
		
		if(debug) log.debug(jsonMessage.toString());
		response.getWriter().println("");
		response.getWriter().close();
	}

	/*
	private AuthorizerGrant getAuthorizerGrant(String appId) {
		// 获取grantId
		String grantId = RedisUtil.getString(AuthorizerGrantService.KEY_AUTHORIZERID_GRANT_PREFIX + appId);
		
		AuthorizerGrant ag = null;
		try {
			ag = authorizerGrantService.getObject(grantId);
		} catch (Exception ex) {
		}
		
		return ag;
	}*/

	/**
	 * 获得回调请求的appId
	 * 
	 * /common/weixin/authorizer/$APPID$
	 * 
	 * @param request
	 *            HTTP请求
	 * @return 回调请求的appId
	 */
	private String getAppId(HttpServletRequest request) {
		// 当前访问的地址（contextMenu之后的字符串部分）
		// /common/weixin/authorizer/$APPID$
		String strURI = request.getRequestURI().trim().substring(request.getContextPath().length());
		if (strURI.length() < 2)
			return null;

		// System.out.println(strURI);
		// 去除左右两边界的/
		strURI = strURI.replaceAll("^/+|/+$", "");

		int nIndex = strURI.lastIndexOf("/");
		if (nIndex <= 0)
			return null;

		String appId = strURI.substring(nIndex + 1);
		if (appId.startsWith("wx")) {
			return appId;
		}

		return null;
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

	private static JSONObject decrypt(JSONObject jData, String xml) {
		JSONObject result = new JSONObject();

		if (!jData.has("Encrypt")) {
			result.put("errmsg", "no encrypt data (0x02)");
			return result;
		}

		// String encrypt = jData.getString("Encrypt");

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
//			log.info("xml is : " + xml);
			// 获得解密后的内容
			String s = mc.decryptMsg(msgSignature, timeStamp, nonce, xml,componentAppId);
			// if (debug) {
			// log.info(s);
			// }
			result = XML.toJSONObject(s);
			result = result.getJSONObject("xml");
		} catch (Exception e) {
			result.put("errmsg", e.getMessage());
			return result;
		}

		return result;
	}

	private static WXBizMsgCrypt getWXBizMsgCrypt(String componentAppId) {
		if (wxbmc != null) {
			return wxbmc;
		}

		// 第三方平台的appid
//		String componentAppId = ComponentManager.COMPONENT_APPID;
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