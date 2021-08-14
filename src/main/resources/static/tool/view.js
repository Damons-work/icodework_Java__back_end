// 字段类型下拉框的option
var typeString = "";
    
// attach an event function with an object
function addEventHandler(oTarget, sEventType, fnHandler) {
	if(oTarget.addEventListener) {
		oTarget.addEventListener(sEventType, fnHandler, false);
	} else if(oTarget.attachEvent) {
		oTarget.attachEvent("on" + sEventType, fnHandler);
	} else {
		oTarget["on" + sEventType] = fnHandler;
	}
}

// 行增长的序号
var nLineIndex = 0;
function addField(tableID, lineId) {
	var newRow = null;
	if(lineId != null && lineId != "") {
		var lineSelected = document.getElementById(lineId);
		newRow = tableID.insertRow(lineSelected.rowIndex+1);
	} else {
		newRow = tableID.insertRow(-1);
	}
	
	newRow.id = 'line' + nLineIndex;
	newRow.align = 'center';
	newRow.style.cursor = 'pointer';
	// addEventHandler(newRow, "click", function(event) {setSelectedId(newRow.id);});

	nLineIndex++;
	var td1 = document.createElement("td");
	var td2 = document.createElement("td");
	var td3 = document.createElement("td");
	var td4 = document.createElement("td");
	var td5 = document.createElement("td");
	var td6 = document.createElement("td");
	var td7 = document.createElement("td");
	var td8 = document.createElement("td");
	
	// 表格行数
	var lineCount = tableID.rows.length;
	// 编号
	// td1.innerHTML = (lineCount - 1) + "";
	// 字段名称
	td2.innerHTML = '<input type="text" name="fieldName" maxlength="50" class="layui-input" style="width:100%;">';
	// 字段类型
	td3.innerHTML = typeString;
	// 字段长度
	td4.innerHTML = '<input type="text" name="fieldLength" maxlength="10" class="layui-input" style="width:100%;">';
	// 是否PK
	td5.innerHTML = '<input type="checkbox" name="pK" value="true" lay-skin="primary"><input type="hidden" name="pKValue"/>';
	// nullable
	td6.innerHTML = '<input type="checkbox" name="notNull" value="true" lay-skin="primary"><input type="hidden" name="notNullValue"/>';
	// 字段说明
	td7.innerHTML = '<input type="text" name="fieldDescription" maxlength="100" class="layui-input" style="width:100%;">';
	// 操作
	td8.innerHTML = '<i class="layui-icon layui-icon-add-1" title="在下面添加一行" style=\'cursor:pointer;font-size:20px;color:#FFB800;margin:5px;\' onclick=\'addField(document.getElementById(\"dynamicTable\"), \"' + newRow.id + '\");\'></i>' +
		'<i class=\'layui-icon layui-icon-delete\' title="删除" onclick=\'deleteField(document.getElementById(\"dynamicTable\"), \"' + newRow.id + '\");\' style=\'cursor:pointer;font-size:20px;color:#01AAED;margin:5px;\'></i>';

	newRow.appendChild(td1);
	newRow.appendChild(td2);
	newRow.appendChild(td3);
	newRow.appendChild(td4);
	newRow.appendChild(td5);
	newRow.appendChild(td6);
	newRow.appendChild(td7);
	newRow.appendChild(td8);

	// 梳理表信息字段序号
	fillNo(tableID);
	
    if(layui.form != null) {
        layui.form.render();
    }
}

// 梳理表信息字段序号
function fillNo(tableID) {
	var lineCount = tableID.rows.length;
	for(var i = 1; i < lineCount; i++) {
		var tempCell = tableID.rows[i].cells[0];
		tempCell.innerHTML = i + "";
	}
}

function deleteField(tableID, lineId) {
	if(tableID.rows.length == 2) {
		$.message('一定要有一条字段，最后一个字段行不能删除！');
		return;
	}
	
	if(lineId == null || lineId == "") {
		$.message("请选择一条字段然后执行本操作！");
		return;
	}
	
	var lineField = document.getElementById(lineId);
	
	if(lineField == null) {
		$.message("请选择一条字段然后执行本操作！");
		return;
	}
	
	$.confirm('你确定删除该行？', function(){
		tableID.deleteRow(lineField.rowIndex);
		internalSelectedId = null;
		// 梳理表信息字段序号
		fillNo(tableID);
	});
}

/**************************************************************************************

								FIELDS DATA

**************************************************************************************/
function getFieldCount() {
	return document.getElementById("dynamicTable").rows.length - 1;
}

function getFieldName(nIndex) {
	if(getFieldCount() == 1) {
		document.forms[0]["fieldName"].value = $.trim(document.forms[0]["fieldName"].value);
		return document.forms[0]["fieldName"].value;
	} else {
		document.forms[0]["fieldName"][nIndex].value = $.trim(document.forms[0]["fieldName"][nIndex].value);
		return document.forms[0]["fieldName"][nIndex].value.trim();
	}
}

function getFieldType(nIndex) {
	if(getFieldCount() == 1) {
		return document.forms[0]["fieldType"].value;
	} else {
		return document.forms[0]["fieldType"][nIndex].value;
	}
}

function getFieldLength(nIndex) {
	if(getFieldCount() == 1) {
		return document.forms[0]["fieldLength"].value;
	} else {
		return document.forms[0]["fieldLength"][nIndex].value;
	}
}

function isPK(nIndex) {
	if(getFieldCount() == 1) {
		return document.forms[0]["pK"].checked;
	} else {
		return document.forms[0]["pK"][nIndex].checked;
	}
}

function isNotNull(nIndex) {
	if(getFieldCount() == 1) {
		return document.forms[0]["notNull"].checked;
	} else {
		return document.forms[0]["notNull"][nIndex].checked;
	}
}

function getFieldDescription(nIndex) {
	if(getFieldCount() == 1) {
		document.forms[0]["fieldDescription"].value = $.trim(document.forms[0]["fieldDescription"].value);
		return $.trim(document.forms[0]["fieldDescription"].value);
	} else {
		document.forms[0]["fieldDescription"][nIndex].value = $.trim(document.forms[0]["fieldDescription"][nIndex].value);
		return $.trim(document.forms[0]["fieldDescription"][nIndex].value);
	}
}
