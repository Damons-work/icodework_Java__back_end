//获得完整的URL路径
//page：页面地址，与当前页面可以是相对路径，也可以是绝对路径
//示例（如果当前页面为 http://console.xuejava.net/system/frame.do）：
//page: /common/upload.do  则返回：http://console.xuejava.net/common/upload.do
//page: ../common/upload.do  则返回：http://console.xuejava.net/common/upload.do
function getFullUrl(page) {
	if(page.indexOf("://") > 0) {
		// 带有协议块，本身就是完整的URL路径
		return page;
	}
	
	var pn = location.href;
	
	// 去除参数
	var nIndex = pn.indexOf("?");
	if(nIndex > 0) {
		pn = pn.substring(0, nIndex);
	}
	
	// 补全协议块
	nIndex = pn.indexOf("://");
	if(nIndex < 0) {
		// 默认http协议
		pn = "http://" + pn;
	}

	// 1. 绝对路径
	if(page.indexOf("/") == 0) {
		// 获得根地址
		nIndex = pn.indexOf("://");
		// 搜索协议块之后的第一个 /
		nIndex = pn.indexOf("/", nIndex + 3);
		
		return (nIndex < 0 ? pn + page : pn.substring(0, nIndex) + page);
	}
	
	// 相对路径	
	// 2. 以../开头
	if(page.indexOf("../") == 0) {		
		// 计算有多少个../
		// 示例：../../abc/edf.do
		var n = 0;
		while(page.indexOf("../") == 0) {
			n++;
			page = page.substring(3);
		}
		// page:   abc/edf.do
		// pn: http://console.xuejava.net/system/w111/e222.do

		// 获得根地址
		nIndex = pn.indexOf("://");
		// 定位目录位置
		while(pn.lastIndexOf("/") > nIndex + 3 && n > 0) {
			pn = pn.substring(0, pn.lastIndexOf("/"))
			n--;
		}

		console.log("pn:" + pn);

		var nEnd = pn.lastIndexOf("/");
		// https://
		return (nEnd <= nIndex + 3 ? pn + "/" + page : pn.substring(0, nEnd) + "/" + page);	
	}
	
	// 3. 非../开头
	// 获得根地址
	nIndex = pn.indexOf("://");
	
	var nEnd = pn.lastIndexOf("/");
	// https://
	return (nEnd <= nIndex + 3 ? pn + "/" + page : pn.substring(0, nEnd) + "/" + page);
}

//form表单封装成json
$.fn.serializeJSON = function() {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

// 正则获取url后的get参数
// name：参数名称
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}

// 封装layui的message、alert、confirm
// message是前灰色背景的方块提示
$.message = function(msg) {
	if(msg.length < 16) {
		// 长度比较小，使用msg
	    top.layer.msg(msg);
	    return;
	}

	// 长度比较长，使用alert，让操作者可以看清楚
	top.layer.alert(msg);
}

$.alert = function(msg, fnYes) {
    top.layer.alert(msg, function(index) {
        if(fnYes != null) {
            // 回调
            fnYes.call(window);
        }
        top.layer.close(index);
    });
}

// fnYes: 点击“确定”按钮的回调函数
// fnNo: 点击“取消”按钮的回调函数
$.confirm = function(msg, fnYes, fnNo) {
    top.layer.confirm(msg, {
            btn: ['确定','取消']
        },
        function(index) {
            if(fnYes != null) {
                // 回调
                fnYes.call(window);
            }
            top.layer.close(index);
        },
        function(index){
            if(fnNo != null) {
                // 回调
                fnNo.call(window);
            }
            top.layer.close(index);
        }
    );
}

// 当前页面对话框url与id的映射
// 对话框ID
var dlg_dynamic = 'dlg_dynamic';
// fn：成功执行后回调函数 function(result)，成功执行，处理的结果 result.statusCode == 200
function openDialog(title, url, width, height, fn) {
    if(width == null || isNaN(width)) width = 640;
    if(height == null || isNaN(height)) height = 480;
    
    // 浏览器窗口可视区域宽度、高度
    var winWidth = $(top.window).width();
    var winHeight = $(top.window).height();
    // console.log(winWidth + ":" + winHeight);
    
    if(width > winWidth) width = winWidth;
    if(height > winHeight) height = winHeight;

    // dialog ID
    var dlgId = dlg_dynamic + "_" + (new Date()).getTime();
    // 传递dialog ID
    if(url.indexOf("?") >= 0) {
        url += "&";
    } else {
        url += "?"
    }
    url += "dialogId=" + dlgId;

    // 创建新的对话框
    var layIndex = top.layer.open({
		id : dlgId,
        type: 2,
        title: title,
        shadeClose: false,
        shade: 0.3,
        area: [width + 'px', height + 'px'],
        content: url
    });

    top.setData(dlgId + ":layIndex", layIndex);
    // 注册回调函数
    if(fn != null) {
    	top.setData(dlgId + ":callback", fn);
    }

    return layIndex;
}

// 获取对话框返回值
// dlgId：对话框的window对象ID，如果为空，则删除当前窗口下的对话框
// result: 返回值，一般为json格式
// ** 注意：此函数需要在对话框页面调用，对话框页面需要带有id为 dialogId 的控件，该控件值为当前对话框的id号 **
function returnDialog(result) {
    if(result == null) {
        $.message("获取处理结果失败！");
        return;
    }

    if(result.statusCode == null) {
        $.message("识别处理结果失败！");
        return;
    }

    // 处理失败，给出提示信息
    if(result.statusCode != 200) {
    	$.message(result.message); 
        return;
    }

	var dlgId = document.getElementById("dialogId");
    dlgId = (dlgId == null ? null : dlgId.value);

    if(dlgId == null || $.trim(dlgId) == '') {
        $.message("对话框页面未发现id为dialogId的控件值，请确定当前对话框的打开使用了openDialog()函数打开。");
        return;
    }

    // 获取对话框的回调函数
	var fn = top.getData(dlgId + ":callback");
    if(fn != null) {
        fn.call(window, result);
        top.setData(dlgId + ":callback", null);
	}

	// 关闭对话框
	// 获取对话框编号
    var layIndex = top.getData(dlgId + ":layIndex");
    top.setData(dlgId + ":layIndex", null);
    top.layer.close(layIndex);

    // 显示成功
    if(result.message != null && $.trim(result.message) != '') {
    	$.message(result.message);  
    }
}

/**************************************************************************************

								 常规菜单功能
  
**************************************************************************************/
//====================== 新增页面 ===========================
function doAdd(unitCh, unitEn, width, height) {
	var url = getFullUrl(unitEn +"Add.do");
	// 打开对话框
	openDialog(unitCh + '新增', url, width, height, function(result) {
	    // 成功执行（result.statusCode == 200）后的回调函数
        submitForm();
	});
}

function doAddAction(unitEn) {
    // 校验表单必填项
    try {
        var blResult = validate(document.getElementById('fAdd'));
        if(!blResult) return;
    } catch (e) {
        console.log(JSON.stringify(e));
    }

    $.ajax({
        url: unitEn + "Add.do"
        ,type: "post"
        ,data: $("#fAdd").serializeJSON()
        ,dataType:'json'
        ,traditional: true
        ,success:function(result) {
            returnDialog(result);
        }
    })
}

//====================== 修改页面 ===========================
function doUpdate(unitCh, unitEn, width, height, sltId) {
	var url = getFullUrl(unitEn + "Update.do?id=" + sltId);
    // 打开对话框
    openDialog(unitCh + '修改', url, width, height, function(result) {
        // 成功执行（result.statusCode == 200）后的回调函数
        submitForm();
    });
}

function doUpdateAction(unitEn) {
    // 校验表单必填项
    try {
        var blResult = validate(document.getElementById('fUpdate'));
        if(!blResult) return;
    } catch (e) {
        console.log(JSON.stringify(e));
    }

    $.ajax({
        url: unitEn + "Update.do"
        ,type: "post"
        ,data: $("#fUpdate").serializeJSON()
        ,dataType:'json'
        ,traditional: true
        ,success:function(result) {
            returnDialog(result);
        }
    })
}

//========================== 删除 ===============================
function doDelete(unitCh, unitEn, sltId) {
    $.confirm("您确认要删除所选择的" + unitCh + "记录？", function(){
        $.ajax({
            type: "POST",
            dataType: "json",
            url: unitEn + "Delete.do?id="+sltId,
            success: function(result) {
                if(result == null) {
                    $.message("获取处理结果失败！");
                    return;
                }

                if(result.statusCode == null) {
                    $.message("识别处理结果失败！");
                    return;
                }

                // 处理失败，给出提示信息
                if(result.statusCode != 200) {
                    $.message(result.message);
                    return;
                }

                // 成功执行（result.statusCode == 200）后的回调函数
                submitForm();

                // 显示成功
                if(result.message != null && $.trim(result.message) != '') {
                    $.message(result.message);
                }
            }
        });
    });
}

//====================== 详情页面 ===========================
function doView(unitCh, unitEn, width, height, sltId) {
	var url = getFullUrl(unitEn +"View.do?id=" + sltId);
    // 打开对话框
    openDialog(unitCh + '详情', url, width, height);
}

//====================== 业务单据审批页面 ===========================
function doAudit(unitCh, unitEn, width, height, bizNo) {
	var url = getFullUrl(unitEn + "Audit.do?bizNo=" + bizNo);
	openDialog(unitCh + '审批', url, width, height, function(result) {
		// 审核成功后，刷新页面
		if(result != null && result.statusCode == 200) {
			submitForm();
		}
	});
}

//====================== 业务单据审批查看页面 ===========================
function doAuditView(unitCh, unitEn, width, height, bizNo) {
	var url = getFullUrl(unitEn + "AuditView.do?bizNo=" + bizNo);
    // 打开对话框
    openDialog(unitCh + '审批查看', url, width, height);
}

//====================== 取反（禁用改为启用，启用改为禁用） ===========================
// flag：false表示禁用，true表示启用
function doReverse(unitCh, unitEn, sltId, flag) {
	var op = (flag == null ? "取反" : (flag ? "启用" : "启用"));
    top.layer.confirm("您确认要" + op + "所选择的" + unitCh + "记录？", {
        btn: ['是的','取消'] //按钮
    },function(){
        $.ajax({
            type: "POST",
            url: unitEn + "Reverse.do?id="+sltId,
            success: function (result) {
                if(result == null) {
                    $.message("获取处理结果失败！");
                    return;
                }

                if(result.statusCode == null) {
                    $.message("识别处理结果失败！");
                    return;
                }

                // 处理失败，给出提示信息
                if(result.statusCode != 200) {
                    $.message(result.message);
                    return;
                }

                // 成功执行（result.statusCode == 200）后的回调函数
                submitForm();

                // 显示成功
                if(result.message != null && $.trim(result.message) != '') {
                    $.message(result.message);
                }
            },
        });
    },function(){
        return;
    });
}

function submitForm() {
	var theForm = document.forms[0];

	try {
        if (changedFlag) theForm["pagination.pageNo"].value = 1;
        var pageNo = $.trim(theForm["pagination.pageNo"].value);
        if (isNaN(pageNo) || pageNo * 1 <= 0 || pageNo.indexOf(".") >= 0) {
            $.alert("请输入正确的页数！");
            return;
        }
    } catch (e1) {
	    // 有的页面没有分业条
    }

    // 检查表单的接口
    // 如果存在表但接口validate(theForm)，则调用该接口
    // 该接口返回布尔值，true通过，false未通过
    try {
        var blResult = validate(theForm);
        if(!blResult) return;
    } catch (e) {}

    theForm.submit();
}

function lowerCaseFirst(str) {
    if(str == null || str.length == 0) {
        return str;
    }

    return str.substr(0, 1).toLowerCase() + str.substr(1);
}

/**************************************************************************************

								 多条记录翻页

**************************************************************************************/
var changedFlag = false;

function toPage(nPageNo) {
    var theForm = document.forms[0];
    theForm["pagination.pageNo"].value = nPageNo;
    submitForm();
}

function changeQuery() {
    changedFlag = true;
}

function enterIn(evt) {
	var evt = evt ? evt : window.event;
	if (evt.keyCode == 13) {
		// 提交表单
		submitForm();
		return;
	}
}

function getTimestampUrl(url) {
    if(url == null || $.trim(url) == "") {
        return "";
    }

    if(url.indexOf("?") > 0) {
        url += "&";
    } else {
        url += "?";
    }
    url += "t=" + (new Date()).getTime();

    return url;
}

// (SELECT element) select a option with value: sltValue
function selectOption(slt, sltValue) {
	var opts = slt.options;
	var found = false;
	for(var i = 0; opts != null && i < opts.length; i++) {
		if(opts[i].value == sltValue) {
			slt.selectedIndex = i;
			found = true;
			break;
		}
	}
	
	if(!found && opts.length > 0) slt.selectedIndex = 0;
}

// (SELECT element) clear all options
function clearOption(sltTarget) {
	while(sltTarget.childNodes.length > 0){
		sltTarget.removeChild(sltTarget.childNodes[0]);
	}
}
	
function getOptionAttribute(slt, attributeName) {
	var index = slt.selectedIndex;
	if(index < 0) return null;
	var opt = slt.options[index];
	return $(opt).attr(attributeName);
}

// 获得单选框、复选框的值，如果是复选框多个值，则以半角都好间隔
// 若单选框、复选框都没有选择，返回null
function getChoiceValue(eleName) {
	var arrEle = getElement(eleName);
	if(arrEle == null) { // no item list
		return null;
	}
	
	if(arrEle.length == null || arrEle.length == 1) {
		if(arrEle.checked) {
			return $.trim(arrEle.value);
		} else {
			return null;
		}
	} else {
		var ids = "";
		for(var nIndex = 0; nIndex < arrEle.length; nIndex++) {
			if(arrEle[nIndex].checked) {
				if(ids != "") ids += ",";				
				ids += $.trim(arrEle[nIndex].value);
			}
		}
		if(ids == "") ids = null;
		
		return ids;
	}
}

// 得到父对象parentEle下面名称为eleName的控件，代替IE下面的document.all("...")操作
// 如果parentEle省略，默认使用document作为父对象。
function getElement(eleName, parentEle) {
    if (parentEle == null) {
        parentEle = document;
    }

    // 得到所有子对象
    var chld = parentEle.getElementsByTagName("input");
    var eles = new Array();
    for (var i = 0; chld != null && i < chld.length; i++) {
        if (chld[i].getAttribute("name") == eleName) {
            eles[eles.length] = chld[i];
        }
    }

    chld = parentEle.getElementsByTagName("textarea");
    for (var i = 0; chld != null && i < chld.length; i++) {
        if (chld[i].getAttribute("name") == eleName) {
            eles[eles.length] = chld[i];
        }
    }

    chld = parentEle.getElementsByTagName("select");
    for (var i = 0; chld != null && i < chld.length; i++) {
        if (chld[i].getAttribute("name") == eleName) {
            eles[eles.length] = chld[i];
        }
    }

    switch (eles.length) {
        case 0: return null;
        case 1: return eles[0];
        default: return eles;
    }
}

/**************************************************************************************

								 为Script对象添加函数
  
**************************************************************************************/
// 为JScript的String添加trim(), trimLeft(), trimRight()函数
String.prototype.trim = function() {
    // 用正则表达式将前后空格
    // 用空字符串替代。
    return this.trimLeft().trimRight();
}

String.prototype.trimLeft = function() {
	return this.replace(/^\s+/, "");
}

String.prototype.trimRight = function() {
	return this.replace(/\s+$/, "");
}

// 从数组中删除指定下标的元素
Array.prototype.remove = function(index) {
	if(index < 0 || index > this.length) return false;
	
	for(var i = index; i < this.length - 1; i++) {
		this[i] = this[i+1];
	}
	
	this.length -= 1;
}

/**************************************************************************************

								  chekc element value
  
**************************************************************************************/
// test whether the element's value is positive integer. if not, give a message and focus the element
function checkPositiveInteger(ele) {
	var blResult = isPositiveInteger(ele.value);
	if(!blResult) {
        $.alert('\u8bf7\u8f93\u5165\u6b63\u6574\u6570\uff01');
		ele.focus();
	}
	return blResult;
}

// whether the value(in) is position interger
function isPositiveInteger(eleValue) {
	return !(eleValue == null || isNaN(eleValue) || eleValue.indexOf('.') >= 0 || eleValue <= 0);
}

// test whether the element's value is positive. if not, give a message and focus the element
function checkPositive(ele) {
	var blResult = isPositive(ele.value);
	if(!blResult) {
		$.alert('\u8bf7\u8f93\u5165\u6b63\u6570\uff01');
		ele.focus();
	}
	return blResult;
}

// whether the value(in) is position
function isPositive(eleValue) {
	return !(eleValue == null || isNaN(eleValue) || eleValue <= 0);
}

function isEmail(vl){
	var reg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
	return reg.test(vl);
}
   
// format a float in terms of "#.##"
function formatMoney(fValue) {
	var vl = Math.round(fValue * 100);
	var str = (vl / 100) + "";
	if(str.indexOf(".") < 0) {
		str += ".00";
	} else if(str.indexOf(".") == str.length - 2) {
		str += "0";
	}
	
	return str;
}

// get file extension name containing "."
function getFileExt(filePath) {
    var nIndex = filePath.lastIndexOf(".");
   return (nIndex >= 0)?filePath.substr(nIndex):"";
}

// 计算字符串的字节长度，一个汉字按2个字节计算
function getByteLength(str) {
	if(str == null) return -1;
	
	var byteLen=0, len = str.length;

	for(var i=0; i<len; i++){ 
		if(str.charCodeAt(i) > 255){ 
			byteLen += 2; 
		} else { 
			byteLen++; 
		} 
	}
	
	return byteLen;
}

/**************************************************************************************

							ICode Table Style and Class
  
**************************************************************************************/
// 为控件ele追加一个class属性，不影响原有的class属性
function addClass(ele, cls) {
	if(ele == null) return;
	if(ele.className == null || $.trim(ele.className) == '') {
		ele.className = cls;
		return;
	}
	// 获取原有的class
	var att = ele.className.split(/\s+/);
	var blExist = false;
	for(var i = 0; i < att.length; i++) {
		if(att[i] == cls) {
			blExist = true;
			break;
		}
	}
	// 本来就含有指定的class属性
	if(blExist) {
		return;
	}
	
	att.push(cls);
	
	var s = '';
	for(var i = 0; i < att.length; i++) {
		if(s.length > 0) s += ' ';
		s += att[i];
	}
	
	ele.className = s;
}

// 控件ele删除一个class属性，不影响其它的class属性
function removeClass(ele, cls) {
	if(ele == null || ele.className == null || $.trim(ele.className) == '') {
		return;
	}
	// 获取原有的class
	var att = ele.className.split(/\s+/);
	var s = '';
	for(var i = 0; i < att.length; i++) {
		if(att[i] == cls) {
			continue;
		}
		if(s.length > 0) s += ' ';
		s += att[i];
	}
	
	ele.className = s;
}

// 控件ele是否有指定的class属性
function hasClass(ele, cls) {
	if(ele == null || ele.className == null || $.trim(ele.className) == '') {
		return false;
	}
	// 获取原有的class
	var att = ele.className.split(/\s+/);
	for(var i = 0; i < att.length; i++) {
		if(att[i] == cls) {
			return true;
		}
	}
	
	return false;
}

function btnMsEvent(evt){
	evt = (evt) ? evt : (window.event ? window.event : "");

	if (evt) {
		var elem = getEventTarget(evt);
		if(evt.type == "mouseover" || evt.type == "mouseup") {
			addClass(elem, "msover");
		}
		if(evt.type == "mousedown"){
			addClass(elem, "msdown");
		}
		if(evt.type == "mouseout"){
			addClass(elem, "menuitem");
		}
	}
}

//attach an event function with an object
function addEventHandler(oTarget, sEventType, fnHandler) {
	if(oTarget.addEventListener) {
		oTarget.addEventListener(sEventType, fnHandler, false);
	} else if(oTarget.attachEvent) {
		oTarget.attachEvent("on" + sEventType, fnHandler);
	} else {
		oTarget["on" + sEventType] = fnHandler;
	}
}



/*************************************************************************************
 权限检查
 **************************************************************************************/
//访问检查
//return true: 可以访问   false：不能访问
function visitCheck(url) {
    var nFlag = getPermissionFlag(url) * 1;

    switch(nFlag) {
        case 1:
            $.alert("您没有登录或者长时间未操作导致登录信息丢失！");
            return false;
        case 2:
            // 有权限
            return true;
        case 3:
            $.alert("您没有权限执行本操作！");
            return false;
        case 4:
            // 异常，留待程序执行时再作权限检查
            return true;
        default:
            // 留待程序执行时再作权限检查
            return true;
    }
}

// 测试当前用户是否有访问某页面的权限
// return 1:操作超时 2:有权限操作 3:无权限操作 4:异常
function getPermissionFlag(url) {
    var sUrl = "../ajax/ajaxPermission.jsp?url=" + url + "&t=" + (new Date()).getTime();

    // 同步调用
    var res = $.ajax({
        url: "../ajax/ajaxPermission.jsp?url=" + url + "&t=" + (new Date()).getTime(),
        async: false
    });

    // $.alert(JSON.stringify(res));

    if(res.status == 200) {
        return $.trim(res.responseText);
    } else {
        return 4;
    }
}


/*************************************************************************************
 2017年7月10日
 統管部分js方法
 **************************************************************************************/


// 得到当前页面所在目录，目录值以"/"开头，以"/"结尾，[ContextPath]之后的二级目录
// if location is http://jtest.sandload.cn/orderA/orderB/orderList.jsp
// getPageDirectory() will be /orderB/


var paginationId = "pagination";
$(document).ready(function() {
	if(document.getElementById(paginationId) != null) {
		// 渲染分页条
		initPagination($('#' + paginationId));
	}
});

function initPagination(ele) {
    var recordCount = ele.attr("recordCount");
    if(recordCount == null || recordCount < 0) recordCount = 0;

    var pageNo = ele.attr("pageNo");
    if(pageNo == null || pageNo < 0) pageNo = 1;

    var pageSize = ele.attr("pageSize");
    if(pageSize == null || pageSize < 0) pageSize = 10;

    layui.use(['laypage'], function() {
        var laypage = layui.laypage;

        //执行一个laypage实例
        laypage.render({
            elem: paginationId // 注意，这里的 pagination 是 ID，不用加 # 号
            ,count:  recordCount // 数据总数，从服务端得到
            ,curr:   pageNo     // 当前页
            ,limit:  pageSize
            ,layout: ['prev', 'page', 'next', 'skip','count','limit'] //样板
            ,jump:function (obj, first) {
                // 不包括第一次
                if(!first){
                	toPage(obj.curr);
                }
            }
        });
    });
}