package cn.js.icode.oAuth.weixin.micro;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import team.bangbang.common.data.StatusCode;
import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import cn.js.icode.oAuth.AuthUser;
import cn.js.icode.oAuth.weixin.data.AuthorizerGrant;
import cn.js.icode.oAuth.weixin.service.AuthorizerGrantService;
import cn.weixin.component.manager.ComponentManager;
import cn.weixin.popular.api.SnsAPI;
import cn.weixin.popular.bean.sns.SnsToken;
import cn.weixin.popular.bean.user.User;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 微信用户信息 - 微服务
 *
 * @author ICode Studio
 * @version 1.0  2019-10-27
 */
@ApiIgnore
@RestController
@CrossOrigin(allowCredentials="true", allowedHeaders="*")
@RequestMapping("/api/weixin")
public final class WeixinUserMicro {
	/**************************************************************************
	 * ！！除非设计、指导人员有特别说明，否则此处不得随意增加、修改、删除！！
	 * －－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
	 * 
	 *************************************************************************/

	/**
	 * 获取公众号/小程序关注者信息 - 第1步：获取code
	 *
	 * @param grantId   公众号/小程序授权编号
	 * @param returnUrl 获取用户信息后转向的页面，不能带有参数
	 *
	 * @return 获取code的地址
	 */
	@RequestMapping("/codeGetter")
	public ResponseBase getCodeGetter(String grantId, String returnUrl) {
		// 根据grantId获取公众号/小程序的appId
		AuthorizerGrant ag = AuthorizerGrantService.getObject(grantId);

		if (ag == null) {
			ResponseBase rb = new ResponseBase(StatusCode.DATA_NOT_FOUND, "指定的公众号/小程序授权不存在：（GrantId：" + grantId + "）");
			return rb;
		}

		String grantUrl = SnsAPI.connectOauth2Authorize(ag.getAuthorizerId(), returnUrl, true, "getopenid",
				ComponentManager.COMPONENT_APPID);

		DataResponse<String> rb = new DataResponse<String>();
		rb.setData(grantUrl);
		System.out.println("0x01. Code getter url : " + grantUrl);

		return rb;
	}
	
	/**
	 * 获取公众号/小程序关注者信息 - 第3步：获取openId及用户信息
	 *
	 * @param grantId 公众号/小程序授权编号
	 * @param code    换取openId的code码
	 *
	 * @return 用户信息
	 */
	@RequestMapping("/openId")
	public ResponseBase getOpenId(String grantId, String code) {
		System.out.println("0x02. grantId : " + grantId + " code : " + code);
		if (grantId == null || code == null) {
			ResponseBase rb = new ResponseBase(StatusCode.REQUEST_DATA_EXPECTED, "code码为null");
			return rb;
		}

		// 根据grantId获取公众号/小程序的appId
		AuthorizerGrant ag = AuthorizerGrantService.getObject(grantId);
		if (ag == null) {
			ResponseBase rb = new ResponseBase(StatusCode.DATA_NOT_FOUND, "指定的公众号/小程序授权不存在：（GrantId：" + grantId + "）");
			return rb;
		}

		SnsToken sns = SnsAPI.oauth2ComponentAccessToken(ag.getAuthorizerId(), code, ComponentManager.COMPONENT_APPID,
				ComponentManager.getComponentAccessToken());

		System.out.println("0x03. sns : " + JSONObject.toJSONString(sns));

		// 如果成功直接拿到openId返回
		if (!sns.isSuccess()) {
			ResponseBase rb = new ResponseBase(StatusCode.DATA_STATUS_ERROR, "获取openid失败！" + JSONObject.toJSONString(sns));
			return rb;
		}
		
		DataResponse<AuthUser> rb = new DataResponse<AuthUser>(StatusCode.SUCCESS, "成功");
		AuthUser au = new AuthUser();
		// 类型标识{1：微信2：支付宝} 
		au.setTypeFlag(1);
		au.setOpenId(sns.getOpenid());
		
		rb.setData(au);
		
		return rb;
	}

	/**
	 * 获取公众号/小程序关注者信息 - 第3步：获取openId及用户信息
	 *
	 * @param grantId 公众号/小程序授权编号
	 * @param code    换取openId的code码
	 *
	 * @return 用户信息
	 */
	@RequestMapping("/userInfo")
	public ResponseBase getUserInfo(String grantId, String code) {
		System.out.println("0x02. grantId : " + grantId + " code : " + code);
		if (grantId == null || code == null) {
			ResponseBase rb = new ResponseBase(StatusCode.REQUEST_DATA_EXPECTED, "code码为null");
			return rb;
		}

		// 根据grantId获取公众号/小程序的appId
		AuthorizerGrant ag = AuthorizerGrantService.getObject(grantId);
		if (ag == null) {
			ResponseBase rb = new ResponseBase(StatusCode.DATA_NOT_FOUND, "指定的公众号/小程序授权不存在：（GrantId：" + grantId + "）");
			return rb;
		}

		SnsToken sns = SnsAPI.oauth2ComponentAccessToken(ag.getAuthorizerId(), code, ComponentManager.COMPONENT_APPID,
				ComponentManager.getComponentAccessToken());

		System.out.println("0x03. sns : " + JSONObject.toJSONString(sns));

		// 如果成功直接拿到openId返回
		if (!sns.isSuccess()) {
			ResponseBase rb = new ResponseBase(StatusCode.DATA_STATUS_ERROR, "获取openid失败！" + JSONObject.toJSONString(sns));
			return rb;
		}

		// 获取了openId
		// 使用openId获取用户信息
		String accessToken = sns.getAccess_token();
		String openid = sns.getOpenid();

		User user = SnsAPI.userinfo(accessToken, openid, "zh_CN");

		System.out.println("0x04. sns : " + JSONObject.toJSONString(user));
		if (user == null) {
			user = new User();
			user.setErrcode("88");
		}

		if (!user.isSuccess()) {
			user.setOpenid(openid);
		}
		
		DataResponse<AuthUser> rb = new DataResponse<AuthUser>(StatusCode.SUCCESS, "成功");
		
		AuthUser au = new AuthUser();
		// 类型标识{1：微信2：支付宝} 
		au.setTypeFlag(1);
		au.setUnionId(user.getUnionid());
		au.setOpenId(sns.getOpenid());
		au.setCity(user.getCity());
		au.setCountry(user.getCountry());
		au.setHeadimgurl(user.getHeadimgurl());
		au.setNickname(user.getNickname());
		au.setProvince(user.getProvince());
		au.setSexFlag(user.getSex());
		
		rb.setData(au);

		return rb;
	}
}
