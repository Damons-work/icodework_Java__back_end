package cn.js.icode.oAuth.weixin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import cn.js.icode.oAuth.weixin.data.AuthorizerGrant;
import cn.js.icode.oAuth.weixin.service.AuthorizerGrantService;
import team.bangbang.spring.parameter.EntityParam;
import cn.weixin.component.manager.AuthorizerManager;
import cn.weixin.component.manager.ComponentManager;
import cn.weixin.popular.api.ComponentAPI;
import cn.weixin.popular.bean.component.PreAuthCode;

/**
 * 公众号/小程序授权信息 - Controller
 *
 * @author ICode Studio
 * @version 1.0 2018-07-01
 */
@Controller
@RequestMapping("/weixin")
public class AuthorizerController {	
    /**
     * @param grantId 授权编号
     * 
     * @return 获取公众号/小程序的access Token
     */
    @RequestMapping("/api/authorizer/tokenGet.do")
	@ResponseBody
    public ResponseBase getToken(String grantId) {
    	String accessToken = AuthorizerManager.getAccessToken(grantId);
    	DataResponse<String> dto = new DataResponse<String>();
    	
    	dto.setData(accessToken);
    	
    	return dto;
	}
    
    /**
     * @param grantId 授权编号
     * 
     * @return 刷新并获取第三方开放平台的access Token
     */
    @RequestMapping("/api/authorizer/tokenRefresh.do")
	@ResponseBody
    public ResponseBase refreshToken(String grantId) {
    	String accessToken = AuthorizerManager.refreshAccessToken(grantId);
    	DataResponse<String> dto = new DataResponse<String>();
    	
    	dto.setData(accessToken);
    	
    	return dto;
	}
    
    /**
     * 公众号/小程序授权信息列表
     *
     * @return 列表页面
     */
    @RequestMapping("/authorizerGrantList.do")
    public ModelAndView doList() {
        ModelAndView view = new ModelAndView("weixin/authorizerGrantList");
        // 查询所有的授权
        List<AuthorizerGrant> authorizerGrantList = AuthorizerGrantService.list(null, null, null);
        view.addObject(authorizerGrantList);
        
        return view;
	}
    
    /**
     * 生成公众号/小程序授权的链接，放到页面上以二维码形式展现
     *
     * @param request HTTP请求
     *
     * @return 列表页面
     */
    @RequestMapping("/beforeGrant.do")
    public ModelAndView beforeGrant(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("weixin/beforeGrant");
        
        // 生成公众号/小程序授权的链接，放到页面上以二维码形式展现
		// 获得第三方平台的access_token
		String component_access_token = ComponentManager.getComponentAccessToken();
		if (component_access_token == null || component_access_token.trim().length() == 0) {
			view.addObject("message", "Component Access Token 尚未生成，请稍候！需要等待10分钟左右再弄他吧！");
			return view;
		}
		
		// 获取预授权码
		PreAuthCode pac = ComponentAPI.api_create_preauthcode(component_access_token, ComponentManager.COMPONENT_APPID);
		if (!pac.isSuccess() && pac.getErrmsg().contains("access_token")) {
			component_access_token = ComponentManager.getNewComponentAccessToken();
			pac = ComponentAPI.api_create_preauthcode(component_access_token, ComponentManager.COMPONENT_APPID);
			if (!pac.isSuccess()) {
				view.addObject("message", "获取预授权码失败！");
				return view;
			}
		}
		
		// 生成公众号授权URL，授权结果推送到/common/weixin/grant，参见cn.weixin.component.service.GrantServlet
		String redirectUrl = CommonMPI.getApplicationUrl(request) + "common/weixin/grant";
		String grantUrl = ComponentAPI.componentloginpage(ComponentManager.COMPONENT_APPID, pac.getPre_auth_code(), redirectUrl);
		
		view.addObject("grantUrl", grantUrl);
		
		return view;
    }

    /**
     * 授权完成
     * 
     * @param grantId 授权编号
     *
     * @return 授权结果页面
     */
    @RequestMapping("/afterGrant.do")
    public ModelAndView afterGrant(String grantId) {
        ModelAndView view = new ModelAndView("weixin/afterGrant");
        
        // 获取刚才的授权信息
        AuthorizerGrant authorizerGrant = AuthorizerGrantService.getObject(grantId);
        
		if(authorizerGrant != null) {
			view.addObject("authorizerGrant", authorizerGrant);
		}
		
		return view;
    }
    
	/**
	 * 新增页面数据提交
	 *
	 * @param authorizerGrant
	 *            公众号/小程序授权信息
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/authorizerGrantAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam AuthorizerGrant authorizerGrant) {
		int result = AuthorizerGrantService.insert(authorizerGrant);
		if (result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}
		
		return ResponseBase.SUCCESS;
	}

	/**
	 * 修改页面显示
	 *
	 * @param grantId
	 *            等级编号
	 *
	 * @return 修改页面
	 */
	@GetMapping("/authorizerGrantUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value = "id") String grantId) {
		ModelAndView view = new ModelAndView("weixin/authorizerGrantUpdate");
		// 查询条件
		AuthorizerGrant where = new AuthorizerGrant();
		where.setId(grantId);
		AuthorizerGrant authorizerGrant = AuthorizerGrantService.getObject(where, null);
		if (authorizerGrant != null)
			view.addObject(authorizerGrant);
		return view;
	}

	/**
	 * 修改页面数据提交
	 *
	 * @param authorizerGrant
	 *            公众号/小程序授权信息
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/authorizerGrantUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam AuthorizerGrant authorizerGrant) {
		// 第1个参数authorizerGrant，取关键字段authorizerGrant.grantId为条件
		// 第3个参数authorizerGrant，取authorizerGrant内关键字段以外其它属性数据
		int result = AuthorizerGrantService.update(authorizerGrant, null, authorizerGrant);
		if (result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		return ResponseBase.SUCCESS;
	}

	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 *
	 * @param grantId
	 *            等级编号
	 *
	 * @return 删除结果
	 */
	@PostMapping(value = "/authorizerGrantDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value = "id") String grantId) {
		// 限定条件
		AuthorizerGrant where = new AuthorizerGrant();
		where.setId(grantId);
		int result = AuthorizerGrantService.delete(where, null);
		if (result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		return ResponseBase.SUCCESS;
	}

	/**
	 * 展示页面
	 *
	 * @param grantId
	 *            关键字段的值
	 *
	 * @return 展示页面
	 */
	@RequestMapping("/authorizerGrantView.do")
	public ModelAndView doView(@RequestParam(value = "id") String grantId) {
		ModelAndView view = new ModelAndView("weixin/authorizerGrantView");
		// 查询条件
		AuthorizerGrant where = new AuthorizerGrant();
		where.setId(grantId);
		AuthorizerGrant authorizerGrant = AuthorizerGrantService.getObject(where, null);
		if (authorizerGrant != null)
			view.addObject(authorizerGrant);
		return view;
	}
}