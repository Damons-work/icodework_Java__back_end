package cn;

import com.alibaba.fastjson.JSONObject;
import cn.weixin.popular.api.MenuAPI;
import cn.weixin.popular.bean.menu.Menu;

/**
 * 微信菜单操作
 * 
 * @author Wang Xianjun
 */
public class MenuTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String token = "11_Gezj1bqg7I2v920Ex3Jq6fZESYpWjORcswftntUCVifH1BfWj5zvW9WIoBv_wTUxojmX7BBFtDP9TDQyWpFnRBs7VqWPbmRjf9driPm1EDh_hEfiBJMtsw0ald1Wm0VbzFSXg2Aq5FLz88EvSTBfADDLMU";

		Menu m = MenuAPI.menuGet(token);

		String j = JSONObject.toJSONString(m);
		System.out.println(j);
	}
}
