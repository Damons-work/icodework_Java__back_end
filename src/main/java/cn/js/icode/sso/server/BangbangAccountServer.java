package cn.js.icode.sso.server;

import team.bangbang.sso.IAccountSSO;
import team.bangbang.sso.SSOContext;
import team.bangbang.sso.TokenBinder;
import team.bangbang.sso.data.Account;

/**
 * 单点登录服务端 - 账户信息
 *
 * @author Bangbang
 * @version 1.0 2021年6月3日
 */
public class BangbangAccountServer implements IAccountSSO {
	@Override
	public Account getAccount() {
		// 检查token票据
		String token = SSOContext.getToken();
		if (token == null || token.trim().length() == 0) {
			return null;
		}
		// 获取Token绑定的Account信息
		Account acc = TokenBinder.getAccount(token);
		return acc;
	}
}
