package cn.weixin.component.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import team.bangbang.common.config.Config;
import team.bangbang.common.exception.BizException;
import team.bangbang.common.redis.RedisUtil;
import cn.js.icode.oAuth.weixin.data.AuthorizerGrant;
import cn.js.icode.oAuth.weixin.service.AuthorizerGrantService;
import cn.weixin.popular.api.ComponentAPI;
import cn.weixin.popular.bean.component.AuthorizerAccessToken;

/**
 * 授权公众号的Token管理
 * 
 * @author ICode Studio
 * @version 1.0 2016-12-28
 */
@Component
public class AuthorizerManager {
	private static Log log = LogFactory.getLog(AuthorizerManager.class);
	private static ConcurrentHashMap<String, Future<?>> futureMap = new ConcurrentHashMap<String, Future<?>>(); 
	
	public static final String AUTHORIZER_REFRESH_TOKEN = "authorizer_refresh_token";
	public static final String KEY_AUTHORIZER_ACCESS_TOKEN_PREFIX = "authorizer_access_token:";
	public static final String KEY_ACCESS_TOKEN_TIME = "access_token_time:";
	/** 授权token */
	public static final String ALI_AUTHORIZER_TOKEN = "ALI_AUTHORIZER_TOKEN";
	/** 授权刷新token，用于下次重新获取新的token */
	public static final String ALI_REFLUSH_TOKEN = "ALI_REFLUSH_TOKEN";

	private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    
	/**
	 * 初始化token 刷新，每100分钟刷新一次。
	 * 
	 * @param grantId 授权编号
	 * @return 是否成功
	 */
	public synchronized static boolean init(final String grantId) {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return false;
		}
		
		// 删除之前Token线程
		remove(grantId);
		
		// 定时刷新获得AUTHORIZER_ACCESS_TOKEN
		Future<?> future = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				try {
					refreshAccessToken(grantId);
				} catch (Exception e) {
					log.error("AUTHORIZER_ACCESS_TOKEN refurbish error : ", e);
				}
			}
		}, 0, 100, TimeUnit.MINUTES);
		
		futureMap.put(grantId, future);

		return true;
	}
	
	/**
	 * 删除并停止指定公众号的Token线程
	 * 
	 * @param grantId 授权编号
	 */
	public static void remove(String grantId) {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return;
		}
		
		// 获取之前的线程
		Future<?> future = futureMap.get(grantId);
		if(future != null && !future.isCancelled()) {
			future.cancel(true);
		}
		
		// 删除缓存的Token
		RedisUtil.setString(KEY_AUTHORIZER_ACCESS_TOKEN_PREFIX + grantId, null, 0);
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
	 * 获得公众号access token
	 * 
	 * @param appId 公众号AppId
	 * 
	 * @return 指定的公众号的access token
	 */
	public static String getAccessTokenByAppId(String appId) {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return null;
		}
		
		// 获取grantId
		String grantId = RedisUtil.getString(AuthorizerGrantService.KEY_AUTHORIZERID_GRANT_PREFIX + appId);
		
		return getAccessToken(grantId);
	}

	/**
	 * 获得公众号授权令牌
	 * 
	 * @param grantId 授权编号
	 * 
	 * @return 指定的公众号的Weixin Token
	 */
	public synchronized static String getAccessToken(String grantId) {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return null;
		}
		
	    String authorizerAccessToken = RedisUtil.getString(KEY_AUTHORIZER_ACCESS_TOKEN_PREFIX + grantId);
		if (authorizerAccessToken == null || authorizerAccessToken.trim().length() == 0) {
		    try {
                refreshAccessToken(grantId);
                authorizerAccessToken = RedisUtil.getString(KEY_AUTHORIZER_ACCESS_TOKEN_PREFIX + grantId);
            } catch (Exception e) {
                log.error(e);
            }
		}

		return authorizerAccessToken;
	}

	/**
	 * 刷新当前公众号授权令牌，将公众号授权令牌返回
	 * 
	 * @param grantId 授权编号
	 * 
	 * @return 将公众号授权令牌返回
	 * @throws BizException
	 */
	public synchronized static String refreshAccessToken(String grantId) {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return null;
		}
		
		// 第三方平台的componentAppId
		String componentAppId = ComponentManager.COMPONENT_APPID;
		// String componentAppSecret = ComponentManager.COMPONENT_APPSECRET;
		// 取得公众号信息
		AuthorizerGrant ag = null;
		try {
			ag = AuthorizerGrantService.getObject(grantId);
		} catch (Exception ex) {
			log.error(ex);
		}
		
		if(ag == null) {
			log.error("refreshAccessToken() ：AuthorizerGrant is null with GrantId : " + grantId);
			return null;
		}
		
		String refresh_token = ag.getRefreshToken();

		if (refresh_token == null || refresh_token.trim().length() == 0) {
			log.error("refreshAuthorizerAccessToken() ：no refresh_token ");
			return null;
		}
		
		// 第三方平台的component_access_token
		String component_access_token = ComponentManager.getComponentAccessToken();

		// 使用refresh_token获取authorizer_access_token
		AuthorizerAccessToken aat = ComponentAPI.api_authorizer_token(component_access_token, componentAppId,
				ag.getAuthorizerId(), refresh_token);
		if (!aat.isSuccess()) {
			// 可能是刚刚刷新了component_access_token，再次重新获取component_access_token
			component_access_token = ComponentManager.getNewComponentAccessToken();
			// 使用refresh_token获取authorizer_access_token
			aat = ComponentAPI.api_authorizer_token(component_access_token, componentAppId, ag.getAuthorizerId(),
					refresh_token);
		}

		if (!aat.isSuccess()) {
			log.error("ComponentAPI.api_authorizer_token(\"" + component_access_token + "\", \"" + componentAppId
					+ "\", \"" + ag.getAuthorizerId() + "\", \"" + refresh_token + "\") ： " + JSONObject.toJSONString(aat));

			return null;
		}

		log.info("ComponentAPI.api_authorizer_token(\"" + component_access_token + "\", \"" + componentAppId
				+ "\", \"" + ag.getAuthorizerId() + "\", \"" + refresh_token + "\") ： " + JSONObject.toJSONString(aat));
		
		// 保存新的refresh_token
		if (!refresh_token.equals(aat.getAuthorizer_refresh_token())) {
			ag.setRefreshToken(aat.getAuthorizer_refresh_token());
			// 将公众号信息保存到数据库中
			try {
				// 修改条件
				AuthorizerGrant where = new AuthorizerGrant();
				where.setId(grantId);
				
				// 修改数据
				AuthorizerGrant data = new AuthorizerGrant();
				data.setRefreshToken(aat.getAuthorizer_refresh_token());
				AuthorizerGrantService.update(where, null, data);
			} catch (Exception ex) {
				log.error(ex);
			}
		}

		String s_att = aat.getAuthorizer_access_token();


		if (s_att != null && s_att.trim().length() > 0) {
			RedisUtil.setString(KEY_AUTHORIZER_ACCESS_TOKEN_PREFIX + grantId, s_att, 119*60);
			log.info("AUTHORIZER_ACCESS_TOKEN refurbish OK: "  + s_att);
		} else {
			RedisUtil.setString(KEY_AUTHORIZER_ACCESS_TOKEN_PREFIX + grantId, null, 0);
			log.info("AUTHORIZER_ACCESS_TOKEN refurbish error(no token)");
		}
		
		return s_att;
	}
}
