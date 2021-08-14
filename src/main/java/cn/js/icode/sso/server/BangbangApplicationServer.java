package cn.js.icode.sso.server;

import java.util.Collections;
import java.util.List;

import team.bangbang.sso.IApplicationSSO;
import team.bangbang.sso.data.Application;

/**
 * 单点登录服务端 - 应用系统信息
 *
 * @author Bangbang
 * @version 1.0  2021年6月3日
 */
public class BangbangApplicationServer implements IApplicationSSO {
	@Override
	public Application getApplication(String applicationId) {
		return null;
	}

	@Override
	public List<Application> getApplicationList() {
		return Collections.emptyList();
	}
}
