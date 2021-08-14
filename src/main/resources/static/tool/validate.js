/**************************************************************************************

									VALIDATION DATA

**************************************************************************************/
function checkInput(theForm) {
	// Code文件包名
	theForm["dbtable.packagePrefix"].value = $.trim(theForm["dbtable.packagePrefix"].value);
	if(!checkPackageName(theForm["dbtable.packagePrefix"].value)) {
		$.message("Code包前缀不正确！");
		return false;
	}
	
	// 表名
	if(theForm["dbtable.tableName"].value.trim() == "") {
		$.message("缺少数据表名称！");
		return false;
	}
	theForm["dbtable.tableName"].value = $.trim(theForm["dbtable.tableName"].value);
	
	// 表说明
	// 数据库表名称，使用正则表达式，去掉最后的“表”字
	var tn = $.trim(theForm["dbtable.tableDescription"].value);
	tn = $.trim(tn.replace(/表+$/g, ""));
	if(tn == "") {
		$.message("缺少数据表说明！");
		return false;
	}
	theForm["dbtable.tableDescription"].value = tn;

	// 字段定义验证
	// 字段个数
	var fieldcount = getFieldCount();
	if(fieldcount == 0) {
		$.message("缺少数据库表的字段定义！");
		return false;
	}
	
	var pk = 0;
	// 疑似数据字典（可以是静态数据字典，也可以是动态数据字典）
	var fixWarning = "";
	for(var i = 0; i < fieldcount; i++) {
		// 字段名称
		/*
		if(!checkFieldName(getFieldName(i))) {
			$.message("请使用大写字母开头的字段名！");
			return false;
		}
		*/
		if(getFieldName(i).indexOf(' ') >= 0) {
			$.message("第 " + (i + 1) + " 字段含有空格，请清除空格！");
			return false;
		}
		
		if(getFieldName(i).toLowerCase() == "flag" || getFieldName(i).toLowerCase() == "code") {
			$.message("第 " + (i + 1) + " 字段使用了保留字，不可以直接使用“Flag”、“Code”作为字段名！");
			return false;
		}
		
		// 固定数据字典，长度检查
		if((getFieldName(i).endsWith("Flag") || getFieldName(i).toLowerCase().endsWith("_flag")) && getFieldType(i) == 'integer') {
			if (getFieldLength(i) > 4) {
				$.message("第 " + (i + 1) + " 字段为固定数据字典字段，长度不能大于4！");
				return false;
			}
			
			// 注释是否包含{取值范围}
			if (getFieldDescription(i).search(/标识\{.+\}$/g) <= 0) {
				$.message("第 " + (i + 1) + " 字段为固定数据字典字段，字段说明的格式应为：XXX标识{取值范围}！<br/><br/>示例：订单状态标识{1：待付款2：待发货3：待收货4：已结单}");
				return false;
			}
		}
		
		// 动态数据字典，长度检查
		if((getFieldName(i).endsWith("Code") || getFieldName(i).toLowerCase().endsWith("_code")) && getFieldType(i) == 'string') {
			if (getFieldLength(i) > 20) {
				$.message("第 " + (i + 1) + " 字段为动态数据字典字段，长度不能大于20！");
				return false;
			}
			
			// 注释是否包含{动态字典分类}
			if (getFieldDescription(i).search(/编码\{.+\}$/g) <= 0) {
				$.message("第 " + (i + 1) + " 字段为动态数据字典字段，字段说明的格式应为：XXX编码{字典分类名称}<br/><br/>示例：学历编码{学历}");
				return false;
			}
		}
		
		if(getFieldType(i) == 'date' || getFieldType(i) == 'text' || getFieldType(i) == 'boolean') {
			if(getFieldLength(i).trim() != '') {
				$.message("第 " + (i + 1) + " 字段的长度输入不正确，日期类型、长文本类型和布尔类型的字段不需要长度！");
				return false;
			}
		} else if(getFieldType(i) == 'double') {
			if(!checkDecimal(getFieldLength(i))) {
				$.message("第 " + (i + 1) + " 字段的长度输入不正确，双精度类型的字段长度输入格式应为：正整数1, 正整数2！");
				return false;
			}
		} else {
			// 字段长度
			if(isNaN(getFieldLength(i)) || getFieldLength(i) <= 0) {
				$.message("第 " + (i + 1) + " 字段的长度输入值不正确！");
				return false;
			}
		}
		
		// PK
		if (isPK(i)) {
			// 关键字段只能为string或者integer类型
			if(getFieldType(i) != 'string' && getFieldType(i) != 'integer') {
				$.message("关键字段只能是string或者integer类型！");
				return false;
			}
			
			// 关键字段个数
			pk++;
		}
		
		// field desc
		if(getFieldDescription(i).trim() == '') {
			$.message("第 " + (i + 1) + " 字段缺少字段说明！");
			return false;
		}

		// set pKValue and notNullValue
		if(fieldcount == 1) {
			theForm["pKValue"].value = theForm["pK"].checked + "";
			theForm["notNullValue"].value = theForm["notNull"].checked + "";
		} else {
			theForm["pKValue"][i].value = theForm["pK"][i].checked + "";
			theForm["notNullValue"][i].value = theForm["notNull"][i].checked + "";
		}
		
		// 根据字段说明，判断疑似数据字典（可以是静态数据字典，也可以是动态数据字典）
		if (likeDict(i)) {
			if (fixWarning.length > 0) {
				fixWarning += "\n";
			}
			fixWarning += "第 " + (i + 1) + " 字段 " + getFieldName(i) + " 好像是字典字段，如确为字典，请改为动态数据字典或静态数据字典！";
		}
	} // end for
	
	if(pk != 1) {
		$.message("该表应该有且只有一个关键字！");
		return false;
	}

	if (fixWarning.length > 0) {
		fixWarning += "\n\n确认按上述设计保存？";
		if (!confirm(fixWarning)) {
			return false;
		}
	}
	
	return true;
}

// 可能是数据字典的字段说明关键字
var dictKeys = ["状态", "分类", "类型", "类别", "种类", "来源", "标识", "层次", "级别", "等级", "区间", "范围", "性质"];
// 检查指定行号的字段是否是疑似字典字段
function likeDict(i) {
	if((getFieldName(i).endsWith("Flag") || getFieldName(i).toLowerCase().endsWith("_flag")) && getFieldType(i) == 'integer') {
		// 已经是固定数据字典
		return false;
	}

	if((getFieldName(i).endsWith("Code") || getFieldName(i).toLowerCase().endsWith("_code")) && getFieldType(i) == 'string') {
		// 已经是动态数据字典
		return false;
	}
	
	for(var j = 0; j < dictKeys.length; j++) {
		if (getFieldDescription(i).indexOf(dictKeys[j]) >= 0) {
			return true;
		}
	}
	return false;
}

// 格式：整数1, 整数2
function checkDecimal(strDecimal) {
	return (strDecimal.search(/^\s*\d+\s*,\s*\d\s*$/) == 0);
}

function checkPackageName(strPackage) {
	// start with lower alpha
	// must use alpha, _ or number
	// not end with .
	if(strPackage.trim() == "") {
		return false;
	}
	
	// ^[a-z][\w]+(\.[a-z]+[\w]*)*$
	return strPackage.search(/^[a-z][\w]+(\.[a-z]+[\w]*)*$/) == 0;
}

function checkFieldName(strFieldName) {
	if(strFieldName.trim() == "") {
		return false;
	}
	
	// must start with A-Z(upper case)
	return (strFieldName.search(/^[A-Z]+/) == 0);
}