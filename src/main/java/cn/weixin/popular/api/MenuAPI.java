package cn.weixin.popular.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.weixin.popular.bean.BaseResult;
import cn.weixin.popular.bean.menu.Menu;
import cn.weixin.popular.bean.menu.MenuButtons;
import cn.weixin.popular.bean.menu.TrymatchResult;
import cn.weixin.popular.bean.menu.selfmenu.CurrentSelfmenuInfo;
import team.bangbang.common.net.http.HttpClient;
import team.bangbang.common.net.http.ResponseHandler;

/**
 * 菜单API
 * 
 * @author LiYi
 *
 */
public class MenuAPI extends BaseAPI {
	private static Logger log = LoggerFactory.getLogger(MenuAPI.class);
	private static HttpClient hc = new HttpClient();

	/**
	 * 创建菜单
	 * 
	 * @param access_token
	 *            access_token
	 * @param menuJson
	 *            菜单json 数据
	 *            例如{\"button\":[{\"type\":\"click\",\"name\":\"今日歌曲\",\"key\":\"V1001_TODAY_MUSIC\"},{\"type\":\"click\",\"name\":\"歌手简介\",\"key\":\"V1001_TODAY_SINGER\"},{\"name\":\"菜单\",\"sub_button\":[{\"type\":\"view\",\"name\":\"搜索\",\"url\":\"http://www.soso.com/\"},{\"type\":\"view\",\"name\":\"视频\",\"url\":\"http://v.qq.com/\"},{\"type\":\"click\",\"name\":\"赞一下我们\",\"key\":\"V1001_GOOD\"}]}]}
	 * @return BaseResult
	 */
	public static BaseResult menuCreate(String access_token, String menuJson) {
		ResponseHandler rh = null;
		String url = BASE_URI + "/cgi-bin/menu/create?" + getATPN() + "=" + access_token;

		try {
			rh = hc.post(url, menuJson, HttpClient.JsonMime);
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}

		return (rh == null ? null : rh.toJavaObject(BaseResult.class));
	}

	/**
	 * 创建菜单
	 * 
	 * @param access_token
	 *            access_token
	 * @param menuButtons
	 *            menuButtons
	 * @return BaseResult
	 */
	public static BaseResult menuCreate(String access_token, MenuButtons menuButtons) {
		String str = JSONObject.toJSONString(menuButtons);
		return menuCreate(access_token, str);
	}

	/**
	 * 获取菜单
	 * 
	 * @param access_token
	 *            access_token
	 * @return Menu
	 */
	public static Menu menuGet(String access_token) {
		ResponseHandler rh = null;
		String url = BASE_URI + "/cgi-bin/menu/get?" + getATPN() + "=" + access_token;

		try {
			rh = hc.get(url);
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}

		return (rh == null ? null : rh.toJavaObject(Menu.class));
	}

	/**
	 * 删除菜单
	 * 
	 * @param access_token
	 *            access_token
	 * @return BaseResult
	 */
	public static BaseResult menuDelete(String access_token) {
		ResponseHandler rh = null;
		String url = BASE_URI + "/cgi-bin/menu/delete?" + getATPN() + "=" + access_token;

		try {
			rh = hc.get(url);
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}

		return (rh == null ? null : rh.toJavaObject(BaseResult.class));
	}

	/**
	 * 获取自定义菜单配置 本接口将会提供公众号当前使用的自定义菜单的配置， 如果公众号是通过API调用设置的菜单，则返回菜单的开发配置，
	 * 而如果公众号是在公众平台官网通过网站功能发布菜单， 则本接口返回运营者设置的菜单配置。
	 * 
	 * @param access_token
	 *            access_token
	 * @return CurrentSelfmenuInfo
	 */
	public static CurrentSelfmenuInfo get_current_selfmenu_info(String access_token) {
		ResponseHandler rh = null;
		String url = BASE_URI + "//cgi-bin/get_current_selfmenu_info?" + getATPN() + "=" + access_token;

		try {
			rh = hc.get(url);
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}

		return (rh == null ? null : rh.toJavaObject(CurrentSelfmenuInfo.class));
	}

	/**
	 * 创建个性化菜单
	 * 
	 * @param access_token
	 *            access_token
	 * @param menuButtons
	 *            menuButtons
	 * @return BaseResult
	 */
	public static BaseResult menuAddconditional(String access_token, MenuButtons menuButtons) {
		String menuJson = JSONObject.toJSONString(menuButtons);
		return menuAddconditional(access_token, menuJson);
	}

	/**
	 * 创建个性化菜单
	 * 
	 * @since 2.7.1
	 * @param access_token
	 *            access_token
	 * @param menuJson
	 *            menuJson
	 * @return BaseResult
	 */
	public static BaseResult menuAddconditional(String access_token, String menuJson) {
		ResponseHandler rh = null;
		String url = BASE_URI + "/cgi-bin/menu/addconditional?" + getATPN() + "=" + access_token;

		try {
			rh = hc.post(url, menuJson, HttpClient.JsonMime);
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}

		return (rh == null ? null : rh.toJavaObject(BaseResult.class));
	}

	/**
	 * 删除个性化菜单
	 * 
	 * @param access_token
	 *            access_token
	 * @param menuid
	 *            menuid
	 * @return BaseResult
	 */
	public static BaseResult menuDelconditional(String access_token, String menuid) {
		ResponseHandler rh = null;
		String url = BASE_URI + "/cgi-bin/menu/delconditional?" + getATPN() + "=" + access_token;

		try {
			rh = hc.post(url, "{\"menuid\":\"" + menuid + "\"}", HttpClient.JsonMime);
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}

		return (rh == null ? null : rh.toJavaObject(BaseResult.class));
	}

	/**
	 * 测试个性化菜单匹配结果
	 * 
	 * @param access_token
	 *            access_token
	 * @param user_id
	 *            可以是粉丝的OpenID，也可以是粉丝的微信号。
	 * @return TrymatchResult
	 */
	public static TrymatchResult menuTrymatch(String access_token, String user_id) {
		ResponseHandler rh = null;
		String url = BASE_URI + "/cgi-bin/menu/trymatch?" + getATPN() + "=" + access_token;

		try {
			rh = hc.post(url, "{\"user_id\":\"" + user_id + "\"}", HttpClient.JsonMime);
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}

		return (rh == null ? null : rh.toJavaObject(TrymatchResult.class));
	}
}