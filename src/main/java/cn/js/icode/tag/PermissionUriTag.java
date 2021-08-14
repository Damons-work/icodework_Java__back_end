package cn.js.icode.tag;

import java.io.IOException;
import java.util.Map;

import org.beetl.core.Tag;

import team.bangbang.sso.IFunctionLimitSSO;
import team.bangbang.sso.SSOContext;

/**
 * 检查是否有指定地址的访问权限
 * 
 * 使用方法为：
 * 
 * <pre>
 * <![CDATA[
 * 
 * 	<#canVisit uri="/system/roleAdd.do">
 * 		<button>新增</button>
 *	</#canVisit>
 * 
 * ]]>
 * 
 * </pre>
 * 
 * @author ICode Studio
 * @version 1.0 2018年10月5日
 * @version 1.1 2019-05-17 复用PermissionFilter中的判断权限方法
 */
public class PermissionUriTag extends Tag {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.beetl.core.Tag#render()
	 */
	public void render() {
		Map<?, ?> attrs = (Map<?, ?>) args[1];
		// 获取applicationId参数
		String applicationId = (String) attrs.get("applicationId");
		// 获取uri参数
		String uri = (String) attrs.get("uri");

		// 获得当前用户
		IFunctionLimitSSO flSSO = SSOContext.getFunctionLimitSSO();
		if (flSSO == null || !flSSO.canVisit(applicationId, null, uri)) {
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
