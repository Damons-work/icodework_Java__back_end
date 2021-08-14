package cn.weixin.component.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import team.bangbang.common.config.Config;
import team.bangbang.common.redis.RedisUtil;
import cn.weixin.popular.api.ComponentAPI;
import cn.weixin.popular.bean.component.ComponentAccessToken;

/**
 * 第三方授权平台的Token管理
 * 
 * @author ICode Studio
 * @version 1.0 2016-12-28
 */
public class ComponentManager {
	private static Log log = LogFactory.getLog(ComponentManager.class);

	// 第三方平台的componentAppId
	public static final String COMPONENT_APPID = Config.getProperty("open.weixin.appId");
	// 第三方平台的密钥
	public static final String COMPONENT_APPSECRET = Config.getProperty("open.weixin.appSecret");

	public static final String KEY_COMPONENT_VERIFY_TICKET = "component_verify_ticket:";

	public static final String KEY_COMPONENT_ACCESS_TOKEN = "component_access_token:";

	private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
	/* 调度线程 */
	private static Future<?> future = null;

	/**
	 * 初始化第三方平台的 token 刷新，每100分钟刷新一次。
	 * 
	 * @return 是否成功
	 */
	public synchronized static boolean init() {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return false;
		}
		
		// 删除之前Token线程
		remove();

		// 定时刷新获得AUTHORIZER_ACCESS_TOKEN
		future = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				try {
					getNewComponentAccessToken();
				} catch (Exception e) {
					log.error("AUTHORIZER_ACCESS_TOKEN refurbish error : ", e);
				}
			}
		}, 0, 100, TimeUnit.MINUTES);

		return true;
	}

	/**
	 * 删除并停止第三方平台的Token线程
	 * 
	 * @param partnerId
	 *            商户编号
	 */
	public static void remove() {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return;
		}
		
		if (future != null && !future.isCancelled()) {
			future.cancel(true);
			future = null;
		}

		// 删除缓存的Token
		RedisUtil.setString(KEY_COMPONENT_ACCESS_TOKEN + ComponentManager.COMPONENT_APPID, null, 0);
	}

	/**
	 * 重新获取第三方平台token
	 * 
	 * @return 新的第三方平台token
	 */
	public static String getNewComponentAccessToken() {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return null;
		}
		
		// 从Redis获取component_verify_ticket
		String vt = RedisUtil.getString(KEY_COMPONENT_VERIFY_TICKET + ComponentManager.COMPONENT_APPID);

		if (vt == null || vt.trim().length() == 0) {
			log.error("COMPONENT_VERIFY_TICKET is not specified, skip getting component_token");
			return null;
		}
		String componentAccessToken = "";
		try {
			ComponentAccessToken token = ComponentAPI.api_component_token(ComponentManager.COMPONENT_APPID,
					ComponentManager.COMPONENT_APPSECRET, vt);

			componentAccessToken = token.getComponent_access_token();
			if (componentAccessToken != null && componentAccessToken.trim().length() > 0) {
				RedisUtil.setString(KEY_COMPONENT_ACCESS_TOKEN + ComponentManager.COMPONENT_APPID, componentAccessToken,
						119 * 60);
				log.info("COMPONENT_ACCESS_TOKEN refurbish OK!");
			} else {
				log.info("COMPONENT_ACCESS_TOKEN refurbish error : " + JSONObject.toJSONString(token));
			}
		} catch (Exception e) {
			log.error("COMPONENT ACCESS_TOKEN refurbish error");
			e.printStackTrace();
		}

		return componentAccessToken;
	}

	/**
	 * 取消 token 刷新
	 */
	public static void destroyed() {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return;
		}
		
		scheduledExecutorService.shutdownNow();
		try {
			while (!scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS)) {
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			// (Re-)Cancel if current thread also interrupted
			scheduledExecutorService.shutdownNow();
		}

		scheduledExecutorService = null;

		log.info("destroyed");
	}

	/**
	 * 获取第三方平台的 access_token
	 * 
	 * @return 第三方平台的 access_token
	 */
	public static String getComponentAccessToken() {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return null;
		}
		
		String componentAccessToken = RedisUtil
				.getString(KEY_COMPONENT_ACCESS_TOKEN + ComponentManager.COMPONENT_APPID);
		// 如果token 为空就立马去获取不需要等线程来扫描
		if (componentAccessToken == null || componentAccessToken.trim().length() == 0) {
			ComponentManager.getNewComponentAccessToken();
			componentAccessToken = RedisUtil.getString(KEY_COMPONENT_ACCESS_TOKEN + ComponentManager.COMPONENT_APPID);
		}

		return componentAccessToken;
	}
}
