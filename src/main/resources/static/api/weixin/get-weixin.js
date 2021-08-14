// 获取页面的GET参数值
function getParameter(key) {
	var pairs = location.search.substring(1).split('&');
	for (var i = 0; i < pairs.length; i++) {
		var pos = pairs[i].indexOf('=');
		if (pos === -1) {
			continue;
		}
		var sk = pairs[i].substring(0, pos);
		if(sk == key) {
			return decodeURIComponent(pairs[i].substring(pos + 1));
		}
	}

	return null;
}

// 获得grantId
function getGrantId() {
	//获所有script标签
	var scripts = document.getElementsByTagName('script')
	// 获得当前脚本
	var script = scripts[scripts.length - 1];
	var scriptSrc = script.src;
	var index = scriptSrc.indexOf("?");
	if(index < 0) {
		return null;
	}

	var qs = scriptSrc.substring(index + 1);
	
	var pairs = qs.split('&');
	for (var i = 0; i < pairs.length; i++) {
		var pos = pairs[i].indexOf('=');
		if (pos === -1) {
			continue;
		}
		var sk = pairs[i].substring(0, pos);
		if(sk == "grantId") {
			return decodeURIComponent(pairs[i].substring(pos + 1));
		}
	}

	return null;
}

var grantId = getGrantId();
var sUser = getParameter("OAuth_user");

window.onload = function() {
	try {
		if(onOpenIdGet != null) {
			// 获取code
			if(sUser == null) {
				var currentUrl = location.href;
				var url = 'http://console.icode.js.cn/api/weixin/get-weixin-openId.html?grantId=' + grantId + '&returnUrl=' + currentUrl;
				location.href= url;
			} else {
				onOpenIdGet(JSON.parse(sUser).openId);
			}

			return;
		}
	} catch (e1) {
		// alert("0x01. " + e1.message);
	}

	try {
		if(onUserInfoGet != null) {
			// 获取code
			if(sUser == null) {
				var currentUrl = location.href;
				var url = 'http://console.icode.js.cn/api/weixin/get-weixin-userInfo.html?grantId=' + grantId + '&returnUrl=' + currentUrl;
				location.href= url;
			} else {
				onUserInfoGet(JSON.parse(sUser));
			}

			return;
		}
	} catch (e1) {
		// alert("0x02. " + e1.message);
	}
}