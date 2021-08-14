package cn.js.icode.oAuth.weixin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import cn.weixin.component.manager.ComponentManager;

/**
 * 第三方开放平台 - Controller
 *
 * @author ICode Studio
 * @version 1.0 2018-07-04
 */
@Controller
@RequestMapping("/weixin")
public class ComponentController {	
    /**
     * @return 获取第三方开放平台的access Token
     */
    @RequestMapping("/api/component/tokenGet.do")
	@ResponseBody
    public ResponseBase getToken() {
    	String accessToken = ComponentManager.getComponentAccessToken();
    	DataResponse<String> dto = new DataResponse<String>();
    	
    	dto.setData(accessToken);
    	
    	return dto;
	}
    
    /**
     * @return 刷新并获取第三方开放平台的access Token
     */
    @RequestMapping("/api/component/tokenRefresh.do")
	@ResponseBody
    public ResponseBase refreshToken() {
    	String accessToken = ComponentManager.getNewComponentAccessToken();
    	DataResponse<String> dto = new DataResponse<String>();
    	
    	dto.setData(accessToken);
    	
    	return dto;
	}
}