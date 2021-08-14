package cn.js.icode.api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.sso.TokenBinder;
import team.bangbang.sso.data.Account;

/**
 * API服务 - 权限
 *
 * @author ICode Studio
 * @version 1.0  2018-10-05
 */
@RestController
@RequestMapping("/api/permission")
@Api("权限API")
public class PermissionRestful {
	/**
	 * 检查指定的账户是否可以访问指定的地址
	 * 
	 * @param applicationId 子系统编号
	 * @param token 身份票据
	 * @param url 指定的地址
	 * @return 指定的账户是否可以访问指定的地址
	 */
	@RequestMapping(value="/canVisit")
	@ApiOperation(value="检查指定的账户是否可以访问指定的地址", notes="该账户必须当前在登录状态")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query", name = "applicationId", value = "子系统编号", required = false, dataType = "String"),
		@ApiImplicitParam(paramType="query", name = "token", value = "身份票据", required = true, dataType = "String"),
		@ApiImplicitParam(paramType="query", name = "uri", value = "指定的uri地址，此处的地址是context名称后面的地址，以/符号开始", required = true, dataType = "String")
	})
    public ResponseBase canVisit(String applicationId, String token, String uri) {
		// 身份信息
		Account acc = TokenBinder.getAccount(token);
		if (acc == null) {
    		return ResponseBase.AUTHENTICATION_IDENTITY_MISS;
		}
    	// 获取当前账户可以访问的菜单地址
    	boolean bl = acc.hasPermission(applicationId, uri);
    	if (bl) {
    		return ResponseBase.SUCCESS;
    	}
    	
        return ResponseBase.REQUEST_PERMISSION_DENIED;
    }
}