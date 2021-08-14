package cn.js.icode.sso.server;

import cn.js.icode.system.service.UserService;
import team.bangbang.common.config.Config;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.sso.IDataLimitSSO;
import team.bangbang.sso.SSOContext;
import team.bangbang.sso.TokenBinder;
import team.bangbang.sso.data.Account;
import team.bangbang.sso.data.DataLimit;

/**
 * 单点登录服务端 - 数据权限
 *
 * @author Bangbang
 * @version 1.0  2021年6月3日
 */
public class BangbangDataLimitServer implements IDataLimitSSO {	
	@Override
	public DataLimit getDataLimit(String applicationId) {
		if (applicationId == null) {
			applicationId = Config.getProperty("sso.application.id");
		}
		if (applicationId == null) {
			return null;
		}
		applicationId = applicationId.trim();
		
		// 检查token票据
		String token = SSOContext.getToken();
		if (token == null || token.trim().length() == 0) {
			return null;
		}
		
		// 获取Token绑定的Account信息
		Account acc = TokenBinder.getAccount(token);
		if (acc == null) {
			// 身份信息已过期
			return null;
		}
		
		// 身份ID
		long id = LogicUtility.parseLong(acc.getId(), 0L);
		
		// 获取Token绑定的数据权限
		DataLimit dl = TokenBinder.getDataLimit(token);
		if (dl == null) {
			// 缓存中不存在，在数据库中查询
			// 查询数据库，形成DataLimit
			dl = UserService.getDataLimit(id);
			if (dl == null) {
				// 身份信息已过期
				return null;
			}
			
			// 将数据权限与token绑定保存
			TokenBinder.saveDataLimit(token, dl);
		}
		
		return dl;
	}
}
