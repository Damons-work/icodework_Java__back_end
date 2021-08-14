package cn.js.icode.tag;

import java.io.IOException;
import java.util.Map;

import org.beetl.core.Tag;

import team.bangbang.common.config.Config;
import team.bangbang.sso.IAccountSSO;
import team.bangbang.sso.SSOContext;
import team.bangbang.sso.data.Account;

/**
 * 检查是否有指定角色的访问权限
 * 
 * 使用方法为：
 * 
 * <pre>
 * <![CDATA[
 * 
 * 	<#hasRole roleKey="role.id.admin">
 * 		<button>新增</button>
 *	</#hasRole>
 * 
 * ]]>
 * 
 * </pre>
 * 
 * @author ICode Studio
 * @version 1.0 2018年10月5日
 * @version 1.1 2019-05-17 复用PermissionFilter中的判断权限方法
 */
public class PermissionRoleTag extends Tag {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.beetl.core.Tag#render()
	 */
	public void render() {
		Map<?, ?> attrs = (Map<?, ?>) args[1];
		// 获取roleKey参数
		String roleKey = (String) attrs.get("roleKey");
		// 获得该角色key配置的roleCode
		String roleCode = Config.getProperty(roleKey);
		if (roleCode == null || roleCode.trim().length() == 0) {
			// 无权限访问
			return;
		}

		// 获得当前用户
		IAccountSSO accSSO = SSOContext.getAccountSSO();
		Account acc = (accSSO == null ? null : accSSO.getAccount());
		
		if (acc == null || !acc.hasRoleCode(roleCode)) {
			// 无权限访问
			return;
		}

		// 有权限访问
		// 获取body内容
		String value = this.getBodyContent().getBody();

		// 输出body内容
		try {
			this.ctx.byteWriter.writeString(value);
		} catch (IOException e) {

		}
	}
}
