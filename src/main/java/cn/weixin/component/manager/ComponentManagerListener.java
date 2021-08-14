package cn.weixin.component.manager;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import team.bangbang.common.config.Config;
import cn.js.icode.oAuth.weixin.data.AuthorizerGrant;
import cn.js.icode.oAuth.weixin.service.AuthorizerGrantService;

@WebListener
public class ComponentManagerListener implements ServletContextListener {
	private static Log log = LogFactory.getLog(ComponentManagerListener.class);

	public void contextInitialized(ServletContextEvent sce) {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return;
		}
		
		// WEB容器 初始化时调用
		ComponentManager.init();

		// 初始化各个公众号/小程序
		try {
			// 获取所有绑定
			List<AuthorizerGrant> agList = AuthorizerGrantService.list(null, null, null);

			if (agList == null || agList.isEmpty()) {
				return;
			}

			for (AuthorizerGrant ag : agList) {
				System.out.println(".........start thread for getting authorizer access token：" + ag.getId());
				AuthorizerManager.init(ag.getId());
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		// 非正式环境，不做token的维护
		if(!Config.getActiveProfile().equalsIgnoreCase("prod")) {
			return;
		}
		
		// WEB容器 关闭时调用
		AuthorizerManager.destroyed();
		ComponentManager.destroyed();
		System.out.println(".........destroy ComponentManager........");
	}
}
