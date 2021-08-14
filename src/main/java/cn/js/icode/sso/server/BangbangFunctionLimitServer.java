package cn.js.icode.sso.server;

import java.util.Collections;
import java.util.List;

import team.bangbang.common.config.Config;
import team.bangbang.sso.IFunctionLimitSSO;
import team.bangbang.sso.SSOContext;
import team.bangbang.sso.TokenBinder;
import team.bangbang.sso.data.Account;
import team.bangbang.sso.data.Menu;

/**
 * 单点登录服务端 - 功能权限
 *
 * @author Bangbang
 * @version 1.0  2021年6月3日
 */
public class BangbangFunctionLimitServer implements IFunctionLimitSSO {
	@Override
	public List<Menu> getMenu(String applicationId) {
		// 不从接口调用，从现有的后端程序获得菜单信息
		return Collections.emptyList();
	}

	@Override
	public boolean canVisit(String applicationId, String code, String uri) {
		// 资源/系统编号
		if (applicationId == null) {
			applicationId = Config.getProperty("sso.application.id");
		}
		if (applicationId == null) {
			return false;
		}
		applicationId = applicationId.trim();
		
		// 检查token票据
		String token = SSOContext.getToken();
		if (token == null || token.trim().length() == 0) {
			return false;
		}

		// 身份信息
		Account acc = TokenBinder.getAccount(token);
		if (acc == null) {
			return false;
		}

		// 当前请求是否可以访问
		// 是否传递了权限编码code
		String codeOrUri = code;
		if (codeOrUri == null || codeOrUri.trim().length() == 0) {
			// 使用请求URI进行权限校验
			codeOrUri = uri;
		}

		// 校验权限编码code、请求URI
		return acc.hasPermission(applicationId, codeOrUri);
	}
}
