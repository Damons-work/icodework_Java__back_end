package cn.js.icode.api;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import team.bangbang.common.data.StatusCode;
import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import cn.js.icode.config.data.AuditTask;
import cn.js.icode.config.data.Item;
import cn.js.icode.config.data.Parameter;
import cn.js.icode.config.service.AuditService;
import cn.js.icode.config.service.AuditTaskService;
import cn.js.icode.config.service.ItemService;
import cn.js.icode.config.service.ParameterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * API服务 - 配置定义
 *
 * @author ICode Studio
 * @version 1.0 2018-10-5
 */
@RestController
@RequestMapping("/api/config")
// @CacheConfig(cacheNames = "ConfigRestful")
@Api("配置定义API")
public class ConfigRestful {
	/**
	 * 获取选项列表
	 *
	 * @param category   所属分类
	 * @param parentCode 父级选项编码
	 *
	 * @return 获取选项列表
	 */
	@PostMapping("/getItemList")
	// @Cacheable(value="Item", key="'ItemList:' + #category + ':' + #parentCode")
	@ApiOperation(value = "获取选项列表", notes = "指定分类、父选项下的选项列表", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "category", value = "所属分类", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "parentCode", value = "父级选项编码", dataType = "String") })
	public ResponseBase getItemList(String category, String parentCode) {
		if (category == null || category.trim().length() == 0) {
			return ResponseBase.REQUEST_DATA_EXPECTED;
		}

		// 父级节点编号
		Long pId = null;
		if (parentCode != null && parentCode.trim().length() > 0) {
			// 查找父节点
			// 查询条件
			Item where = new Item();
			where.setCategory(category);
			where.setItemCode(parentCode);

			Item pIt = ItemService.getObject(where, null);

			if (pIt == null) {
				// 指定的父级节点不存在
				return ResponseBase.DATA_NOT_FOUND;
			}

			pId = pIt.getId();
		}

		// 查询条件
		Item where = new Item();
		where.setCategory(category);
		where.setParentId(pId);
		// 附加限定条件
		String appendix = null;
		if (pId == null) {
			appendix = "ParentId is null";
		}

		List<Item> itemList = ItemService.list(where, appendix, null);

		if (itemList == null || itemList.size() == 0) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 返回数据
		DataResponse<List<Item>> rb = new DataResponse<List<Item>>();
		rb.setData(itemList);

		return rb;
	}

	/**
	 * 通过选项编码或者选项名称获取一个选项
	 *
	 * @param category 所属分类，必填
	 * @param itemCode 选项编码，选项编码与选项名称条件二选一必填
	 * @param itemName 选项名称，选项编码与选项名称条件二选一必填
	 *
	 * @return 一个选项
	 */
	@PostMapping("/getItem")
	// @Cacheable(key="'Item:' + #category + ':' + #itemCode" + ':' + #itemName" )
	@ApiOperation(value = "通过选项编码或者选项名称获取一个选项", notes = "指定编码或者名称对应的选项", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "category", value = "所属分类", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "itemCode", value = "选项编码，选项编码与选项名称条件二选一必填", required = false, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "itemName", value = "选项名称，选项编码与选项名称条件二选一必填", required = false, dataType = "String")})
	public ResponseBase getItem(String category, String itemCode, String itemName) {
		if (category == null || category.trim().length() == 0) {
			return ResponseBase.REQUEST_DATA_EXPECTED;
		}
		if ((itemCode == null || itemCode.trim().length() == 0)
				&& (itemName == null || itemName.trim().length() == 0)) {
			return ResponseBase.REQUEST_DATA_EXPECTED;
		}

		// 查询条件
		Item where = new Item();
		where.setCategory(category);
		where.setItemCode(itemCode);
		where.setItemName(itemName);

		Item it = ItemService.getObject(where, null);

		if (it == null) {
			// 指定的节点不存在
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 返回数据
		DataResponse<Item> rb = new DataResponse<Item>();
		rb.setData(it);

		return rb;
	}

	/**
	 * 获取一个参数配置
	 *
	 * @param module        所属模块
	 * @param parameterName 参数名称
	 *
	 * @return 一个参数配置
	 */
	@PostMapping("/getParameter")
	// @Cacheable(key="'Parameter:' + #module + ':' + #parameterName")
	@ApiOperation(value = "获取一个选项配置", notes = "指定参数名下的参数值。不同模块存在相同参数名，因此需要指定所属模块", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "module", value = "所属模块", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "parameterName", value = "参数名称", required = true, dataType = "String") })
	public ResponseBase getParameter(String module, String parameterName) {		
		if (module == null || module.trim().length() == 0 || parameterName == null
				|| parameterName.trim().length() == 0) {
			return ResponseBase.REQUEST_DATA_EXPECTED;
		}

		// 查询条件
		Parameter where = new Parameter();
		where.setModule(module);
		where.setParameterName(parameterName);

		Parameter p = ParameterService.getObject(where, null);

		if (p == null) {
			// 指定的参数配置不存在
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 返回数据
		DataResponse<Parameter> rb = new DataResponse<Parameter>();
		rb.setData(p);

		return rb;
	}
	
	/**
	 * 创建审批任务
	 * 
	 * @param userId 申请单制单人账户编号
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * 
	 * @return 创建的任务数量，其中参与并签、会签的审批会被展开计算。<br>
	 *         即如果2人参加并签、3人参加会签，则此时创建5条任务。<br>
	 *         如果返回的任务数量为0，则表示没有配置审批流程。
	 */
	@PostMapping("/createAuditTasks")
	@ApiOperation(value = "创建审批任务", notes = "为指定的申请单创建审批任务", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name = "applicantId", value = "申请单的申请人账户编号", required = true, dataType = "Long"),
		@ApiImplicitParam(paramType = "query", name = "bizCode", value = "申请单类型编码，在选项配置中配置后使用", required = true, dataType = "String"),
		@ApiImplicitParam(paramType = "query", name = "bizNo", value = "申请单编号，可以使用字符串类型编号，也可以使用长整型编号", required = true, dataType = "String")
	})
	public static ResponseBase createAuditTasks(Long applicantId, String bizCode, String bizNo) {
		int n = AuditTaskService.createTasks(applicantId, bizCode, bizNo);
		
		if (n <= 0) {
			ResponseBase rb = new ResponseBase(StatusCode.DATA_NOT_FOUND, "没有配置审批流程");
			return rb;
		}

		DataResponse<Integer> rb = new DataResponse<Integer>(StatusCode.SUCCESS, "成功创建审批任务，任务数量：" + n);
		rb.setData(n);

		return rb;
	}
	
	/**
	 * 获得指定操作账户在指定类型申请单上的审批任务列表，分为待审批、已审批2种审批任务
	 * 
	 * @param auditUserId 实际审批人的编号
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param hasAudited 是否审批，用于区分待审批、已审批2种审批任务
	 * @param pageNo 分页参数 - 页号，默认为1
	 * @param pageSize 分页参数 - 每页最大记录数量，默认为10
	 * 
	 * @return 审批任务列表
	 */
	@PostMapping("/getMyAuditTaskList")
	@ApiOperation(value = "获取指定操作账户在指定申请单的审批任务列表", notes = "分为待审批、已审批2种审批任务", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name = "auditUserId", value = "指定操作账户的编号", required = true, dataType = "Long"),
		@ApiImplicitParam(paramType = "query", name = "bizCode", value = "申请单类型编码，在选项配置中配置后使用", required = true, dataType = "String"),
		@ApiImplicitParam(paramType = "query", name = "hasAudited", value = "是否审批，用于区分待审批、已审批2种审批任务", required = true, dataType = "Boolean"),
		@ApiImplicitParam(paramType = "query", name = "pageNo", value = "分页参数 - 页号，默认为1", dataType = "Integer"),
		@ApiImplicitParam(paramType = "query", name = "pageSize", value = "分页参数 - 每页最大记录数量，默认为10", dataType = "Integer")
	})
	public static ResponseBase getMyAuditTaskList(Long auditUserId, String bizCode, boolean hasAudited, Integer pageNo, Integer pageSize) {
		List<AuditTask> atList = AuditTaskService.getMyAuditTaskList(auditUserId, bizCode, hasAudited, pageNo, pageSize);
		
		if(atList == null || atList.size() == 0) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		DataResponse<List<AuditTask>> rb = new DataResponse<List<AuditTask>>();
		rb.setData(atList);
		
		return rb;
	}
	
	/**
	 * 获得指定操作账户在指定申请单上的是否有“待审核”的审批任务
	 * 
	 * @param auditUserId 指定操作账户的编号
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编码
	 * 
	 * @return 审批任务列表
	 */
	@PostMapping("/hasPendingAuditTask")
	@ApiOperation(value = "指定操作账户在指定申请单上的是否有“待审核”的审批任务", notes = "指定操作账户在指定申请单上的是否有“待审核”的审批任务", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name = "auditUserId", value = "指定操作账户的编号", required = true, dataType = "Long"),
		@ApiImplicitParam(paramType = "query", name = "bizCode", value = "申请单类型编码，在选项配置中配置后使用", required = true, dataType = "String"),
		@ApiImplicitParam(paramType = "query", name = "bizNo", value = "申请单编码", required = true, dataType = "String")
	})
	public static ResponseBase hasPendingAuditTask(Long auditUserId, String bizCode, String bizNo) {
		DataResponse<Boolean> r = new DataResponse<Boolean>();
		
		// 查询是否有“待审核”的审批任务
		boolean bl = AuditTaskService.hasPendingAuditTask(auditUserId, bizCode, bizNo);
		
		r.setData(new Boolean(bl));
		r.setStatusCode(StatusCode.SUCCESS);
		r.setMessage("成功");
		
		return r;
	}
	
	/**
	 * 获得指定操作账户在指定类型申请单上的审批任务数量，分为待审批、已审批2种审批任务
	 * 
	 * @param auditUserId 实际审批人的编号
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param hasAudited 是否审批，用于区分待审批、已审批2种审批任务
	 * 
	 * @return 审批任务数量
	 */
	@PostMapping("/getMyAuditTaskCount")
	@ApiOperation(value = "获取指定操作账户在指定申请单的审批任务数量", notes = "分为待审批、已审批2种审批任务", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name = "auditUserId", value = "指定操作账户的编号", required = true, dataType = "Long"),
		@ApiImplicitParam(paramType = "query", name = "bizCode", value = "申请单类型编码，在选项配置中配置后使用", required = true, dataType = "String"),
		@ApiImplicitParam(paramType = "query", name = "hasAudited", value = "是否审批，用于区分待审批、已审批2种审批任务", required = true, dataType = "Boolean")
	})
	public static ResponseBase getMyAuditTaskCount(Long auditUserId, String bizCode, boolean hasAudited) {
		int n = AuditTaskService.getMyAuditTaskCount(auditUserId, bizCode, hasAudited);
		
		DataResponse<Integer> rb = new DataResponse<Integer>();
		rb.setData(n);

		return rb;
	}
	
	/**
	 * 获取指定申请单的审批任务列表，包括已经完成、正在进行、等待执行的审批任务
	 * 
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * 
	 * @return 审批任务列表
	 */
	@PostMapping("/getBillAuditTaskList")
	@ApiOperation(value = "获取指定申请单的审批任务列表", notes = "包括已经完成、正在进行、等待执行的审批任务列表，用于查看审批任务执行情况", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name = "bizCode", value = "申请单类型编码", required = true, dataType = "String"),
		@ApiImplicitParam(paramType = "query", name = "bizNo", value = "申请单编号，可以使用字符串类型编号，也可以使用长整型编号", required = true, dataType = "String")
	})
	public static ResponseBase getBillAuditTaskList(String bizCode, String bizNo) {
		// 构造查询条件
		AuditTask form = new AuditTask();
		form.setBizCode(bizCode);
		form.setBizNo(String.valueOf(bizNo).trim());

		List<AuditTask> atList = AuditTaskService.getAuditTaskList(bizCode, bizNo);
		if(atList == null || atList.size() == 0) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		DataResponse<List<AuditTask>> rb = new DataResponse<List<AuditTask>>();
		rb.setData(atList);
		
		return rb;
	}
	
	/**
	 * 删除指定业务对象的所有未执行（StatusFlag = 1,2）的审核任务，已经执行的不删除。
	 * 
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * 
	 * @return 审批任务列表
	 */
	@PostMapping("/deletePendingTasks")
	@ApiOperation(value = "删除指定申请单的未执行审批任务", notes = "删除指定业务对象的所有未执行（StatusFlag = 1,2）的审核任务，已经执行的不删除。", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name = "bizCode", value = "申请单类型编码", required = true, dataType = "String"),
		@ApiImplicitParam(paramType = "query", name = "bizNo", value = "申请单编号，可以使用字符串类型编号，也可以使用长整型编号", required = true, dataType = "String")
	})
	public static ResponseBase deletePendingTasks(String bizCode, String bizNo) {
		// 构造查询条件
		AuditTask form = new AuditTask();
		form.setBizCode(bizCode);
		form.setBizNo(String.valueOf(bizNo).trim());

		int n = AuditTaskService.deletePendingTasks(bizCode, bizNo);
		if(n <= 0) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		DataResponse<Integer> rb = new DataResponse<Integer>();
		rb.setData(n);
		
		return rb;
	}
	
	/**
	 * 获取指定申请单下的审批任务HTML字符串
	 * 
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * 
	 * @return 指定申请单下的审批任务HTML字符串
	 */
	@PostMapping("/getBillAuditTaskHtml")
	@ApiOperation(value = "获取指定申请单下的审批任务HTML字符串", notes = "以HTML表格的形式展现", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name = "bizCode", value = "申请单类型编码", required = true, dataType = "String"),
		@ApiImplicitParam(paramType = "query", name = "bizNo", value = "申请单编号，可以使用字符串类型编号，也可以使用长整型编号", required = true, dataType = "String")
	})
	public static ResponseBase getBillAuditTaskHtml(String bizCode, String bizNo) {
		String html = AuditTaskService.getBillAuditTaskHtml(bizCode, bizNo);		

		DataResponse<String> rb = new DataResponse<String>();
		rb.setData(html);
		
		return rb;
	}
	
	/**
	 * 获取指定申请单下的审批任务字符串
	 * 
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * 
	 * @return 指定申请单下的审批任务字符串
	 */
	@PostMapping("/getBillAuditTaskString")
	@ApiOperation(value = "获取指定申请单下的审批任务字符串", notes = "以字符串的形式展现", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name = "bizCode", value = "申请单类型编码", required = true, dataType = "String"),
		@ApiImplicitParam(paramType = "query", name = "bizNo", value = "申请单编号，可以使用字符串类型编号，也可以使用长整型编号", required = true, dataType = "String")
	})
	public static ResponseBase getBillAuditTaskString(String bizCode, String bizNo) {
		String str = AuditTaskService.getBillAuditTaskString(bizCode, bizNo);		

		DataResponse<String> rb = new DataResponse<String>();
		rb.setData(str);
		
		return rb;
	}

	/**
	 * 对指定的申请单进行审批
	 * 
	 * @param auditUserId 实际审批人的编号
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * @param passFlag 当前环节是否通过
	 * @param comment 审批备注
	 * 
	 * @return 审批结果{1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}
	 */
	@PostMapping("/doAudit")
	@ApiOperation(value = "对指定的申请单进行审批", notes = "包括已经完成、正在进行、等待执行的审批任务列表，用于查看审批任务执行情况。返回的审批结果含义：{1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name = "auditUserId", value = "审批人账户编号", required = true, dataType = "Long"),
		@ApiImplicitParam(paramType = "query", name = "bizCode", value = "申请单类型编码", required = true, dataType = "String"),
		@ApiImplicitParam(paramType = "query", name = "bizNo", value = "申请单编号，可以使用字符串类型编号，也可以使用长整型编号", required = true, dataType = "String"),
		@ApiImplicitParam(paramType = "query", name = "passFlag", value = "当前审批环节是否通过", required = true, dataType = "boolean"),
		@ApiImplicitParam(paramType = "query", name = "comment", value = "审批意见", required = true, dataType = "String")
	})
	public static ResponseBase doAudit(Long auditUserId, String bizCode, String bizNo, boolean passFlag, String comment) {
		// 审批结果{1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}
		int n = AuditService.doAudit(auditUserId, bizCode, bizNo, passFlag, comment);
		
		DataResponse<Integer> rb = null;
		switch(n) {
			case 1:
				rb = new DataResponse<Integer>(StatusCode.SUCCESS, "审批过程中步进");
				break;
			case 2:
				rb = new DataResponse<Integer>(StatusCode.SUCCESS, "审批通过");
				break;
			case 3:
				rb = new DataResponse<Integer>(StatusCode.SUCCESS, "审批驳回");
				break;
			case 4:
				rb = new DataResponse<Integer>(StatusCode.DATA_NOT_FOUND, "审批任务不存在");
				break;
			case 5:
				rb = new DataResponse<Integer>(StatusCode.DATA_STATUS_ERROR, "填写审批任务失败");
				break;
			default:
				rb = new DataResponse<Integer>();
		}		
		
		rb.setData(n);

		return rb;
	}
}