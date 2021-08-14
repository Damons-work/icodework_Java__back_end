package cn.weixin.component.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.List;

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
import team.bangbang.common.config.Config;
import team.bangbang.common.redis.RedisUtil;
import team.bangbang.common.utility.LogicUtility;

//************************************************************************
/**
 * 微信公众号第三方平台事件接收URL。
 * 
 * 在web.xml中的定义可以传入以下参数：
 * <ul>
 * <li>debug：布尔型，是否输出debug信息</li>
 * </ul>
 * 
 * <pre>
 * 1. 当前servlet的url-pattern必须为：
 * 		/common/weixin/component			# 事件接收URL
 * </pre>
 * 
 * @author ICode Studio
 * @version 1.0 2016-12-29
 */
// ************************************************************************
@WebServlet(urlPatterns = "/common/weixin/component")
public class ComponentServlet extends HttpServlet {
	private static final long serialVersionUID = -2528310861419613962L;
	private static Logger log = LoggerFactory.getLogger(ComponentServlet.class);
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
		// 请求的参数
		JSONObject params = readUrlParams(request);
		JSONObject body = readBody(request);

		jsonMessage.put("UrlParameter", params);

		if (params == null) {
			params = new JSONObject();
		}

		if (body == null || !body.has("xmlData")) {
			return;
		}
		jsonMessage.put("XmlData", body.getString("xmlData"));

		JSONObject jData = new JSONObject();
		for (Object key : body.keySet()) {
			Object value = body.get((String) key);
			jData.put((String) key, value);
		}

		// 合并参数
		if (params != null) {
			for (Object key : params.keySet()) {
				Object value = params.get((String) key);
				jData.put((String) key, value);
			}
		}

		JSONObject req = null;
		if (jData.has("Encrypt")) {
			req = decrypt(jData);
			jData.put("BodyDecrypt", req);
		}

		if (req == null) {
			if (debug)
				log.debug(jsonMessage.toString());
			response.getWriter().close();
			return;
		}

		// 1. 传递了ComponentVerifyTicket
		log.debug("req的结果：" + req.toString());
		if (req.has("ComponentVerifyTicket")) {
			// 刷新component_verify_ticket
			JSONObject res = refreshVerifyTicket(req);

			// 响应数据
			jsonMessage.put("Output", res);
			response.getWriter().println("success");
		}

		// 2. 取消关注
		/*
		 * <AppId><![CDATA[wxcf9db037c3f48967]]></AppId>
		 * <CreateTime>1487222328</CreateTime>
		 * <InfoType><![CDATA[unauthorized]]></InfoType>
		 * <AuthorizerAppid><![CDATA[wx2b2498f3c813b959]]></AuthorizerAppid>
		 */
		if (req.has("InfoType") && "unauthorized".equals(req.get("InfoType"))) {
			// 取消关注
			String authorizerAppId = req.has("AuthorizerAppid") ? req.getString("AuthorizerAppid") : null;

			boolean bl = unauthorize(authorizerAppId);

			// 响应数据
			if (bl) {
				response.getWriter().println("unauthorize success");
			}
		}

		if (debug) {
			log.debug(jsonMessage.toString());
		}
		response.getWriter().close();
	}

	private boolean unauthorize(String authorizerAppId) {
		// 删除appid - GrantId绑定关系
		RedisUtil.setString(AuthorizerGrantService.KEY_AUTHORIZERID_GRANT_PREFIX + authorizerAppId, null, 0);

		try {
			// 获得grantId，防止一个公众号的多次授权，此处将多次授权的数据全部清理
			// 条件
			AuthorizerGrant where = new AuthorizerGrant();
			where.setAuthorizerId(authorizerAppId);
			List<AuthorizerGrant> agList = AuthorizerGrantService.list(where, null, null);

			// 删除数据库中的授权记录
			AuthorizerGrantService.delete(where, null);
			
			// 停止token的维护
			for(int i = 0; agList != null && i < agList.size(); i++) {
				String grantId = agList.get(i).getId();
				AuthorizerManager.remove(grantId);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		return true;
	}

	/**
	 * 刷新component_verify_ticket
	 * 
	 * @param componentVerifyTicket
	 *            刷新Ticket
	 * 
	 * @return 操作结果
	 */
	private JSONObject refreshVerifyTicket(JSONObject req) {
		JSONObject result = new JSONObject();
		String componentVerifyTicket = req.getString("ComponentVerifyTicket");
		String appId = req.getString("AppId");
		if (componentVerifyTicket == null || componentVerifyTicket.trim().length() == 0) {
			return result;
		}

		// 加密的信息解密
		try {
			if (componentVerifyTicket != null && componentVerifyTicket.trim().length() > 0) {
				// 保存component_verify_ticket到Redis
				RedisUtil.setString(ComponentManager.KEY_COMPONENT_VERIFY_TICKET + appId, componentVerifyTicket, 0);
			}

			// 如果token 为空就立马去获取不需要等线程来扫描
			String at = RedisUtil.getString(ComponentManager.KEY_COMPONENT_ACCESS_TOKEN + appId);
			if (at == null || at.trim().length() == 0) {
				ComponentManager.getNewComponentAccessToken();
			}

			result.put("component_verify_ticket", componentVerifyTicket);
			result.put("init_component_access_token_result", true);

			return result;
		} catch (Exception e) {
			result.put("errmsg", e.getMessage());
			return result;
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

	public static void main(String[] args) {
		JSONObject json1 = new JSONObject();
		json1.put("AppId", "wx4de456331bf8c3d1");
		json1.put("xmlData",
				"<xml>\r\n" + 
				"<AppId><![CDATA[wx4de456331bf8c3d1]]></AppId>\r\n" + 
				"    <Encrypt><![CDATA[/BZvN51Hf33Qs4e6+Q43bbld8QJ7+aQtogkPIBjouqMuIYfBjQm2vGA7R+m7Sz+v7foFvm7MAUjd9VDFvi50Tmd0Sfj1sMdQ56xaKFYO6UAi4MV073pBAcqHgrn2bNdOD2cKmpogl5DZqeBsSecXeoComGPShbfR7Q6csc9vLNSoXi78c6XDVOr2uJB/6nrWtV/Cx3YAjUWoep095lu9IhO8UW7NLm5q4XmhFR8I+p1A5CbiAzDJltW8FGS6oSs2DRBmjnbCt/sztmSgWFcIQ3cf0iurMd+YZqo13KowOM6nN17I/7TDIg58skCF55CnR5d5ex7GtJBIgvSi/MlyzOfU5Gjeif+MV8mLWbDfRG/q3BrUyV3/h5Ste3qeoh4yy+IZ7cxbnPMiDF9hAivSIcIg9zugfoH7cCy6zt3w/kqad9GqJH0FJ2KVlaCvvjB+r4ATpXwR+3W+P7KS6cBh2g==]]>\r\n" + 
				"</Encrypt>\r\n" + 
				"</xml>");
		json1.put("EnterTime", "1530605813720");
		json1.put("FromIp", "140.207.54.79");
		json1.put("RestfulFunction", "service.weixin.component");
		json1.put("encrypt_type", "aes");
		json1.put("msg_signature", "addf9df5968fdfd2a29ba65363435587a550a2fa");
		json1.put("signature", "13f5ab9ce8e3971c162801196157c5aff4ed3fb5");
		json1.put("nonce", "1639117052");
		json1.put("timestamp", "1530605813");
		ComponentServlet c = new ComponentServlet();
		c.decrypt(json1);
	}

	private JSONObject decrypt(JSONObject jData) {
		JSONObject result = new JSONObject();
		if (!jData.has("xmlData")) {
			result.put("errmsg", "no encrypt xml data (0x02)");
			return result;
		}

		String xml = jData.getString("xmlData");
		// 加密的信息解密
		try {
			// msgSignature
			String msgSignature = jData.getString("msg_signature");
			// 时间戳
			String timeStamp = jData.getString("timestamp");
			// 随机字符串
			String nonce = jData.getString("nonce");

			String componentAppId = jData.getString("AppId");
			WXBizMsgCrypt mc = getWXBizMsgCrypt(componentAppId);
			// 获得解密后的内容
			String s = mc.decryptMsg(msgSignature, timeStamp, nonce, xml, componentAppId);
			result = XML.toJSONObject(s);
			result = result.getJSONObject("xml");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			result.put("errmsg", e.getMessage());
			return result;
		}

		return result;
	}

	private WXBizMsgCrypt getWXBizMsgCrypt(String componentAppId) {
		if (wxbmc != null) {
			return wxbmc;
		}

		// 第三方平台的appid
		// String componentAppId = ComponentManager.COMPONENT_APPID;
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