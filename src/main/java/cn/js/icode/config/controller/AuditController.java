package cn.js.icode.config.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.js.icode.basis.service.OrganizationService;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import team.bangbang.common.utility.LogicUtility;
import cn.js.icode.config.data.Audit;
import cn.js.icode.config.data.AuditItem;
import cn.js.icode.config.data.Item;
import cn.js.icode.config.service.AuditItemService;
import cn.js.icode.config.service.AuditService;
import cn.js.icode.config.service.ItemService;
import team.bangbang.spring.parameter.EntityParam;
import cn.js.icode.system.data.Role;
import cn.js.icode.system.service.RoleService;
import cn.js.icode.system.service.UserService;

/**
 * 审批流程 - Controller
 * 
 * @author ICode Studio
 * @version 1.0  2018-10-30
 */
@Controller
@RequestMapping("/config")
public class AuditController {
	/**
	 * 审批流程列表
	 * 
	 * @param audit 查询条件
	 * @param pagination 分页数据
	 * 
	 * @return 列表页面
	 */
	@RequestMapping("/auditList.do")
	public ModelAndView doList(@EntityParam Audit audit, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("config/auditList");

		// 设置缺省值：有效标识
		// 默认查询有效记录
		if(audit.getActiveFlag() == null) {
			audit.setActiveFlag(true);
		}
	
		// 构造下拉框数据
		configDropdownList(audit, view);  
		
		List<Audit> auditList = AuditService.list(audit, null, pagination);	
		
		// 统计符合条件的结果记录数量
		int recordCount = AuditService.count(audit, null);
		pagination.setRecordCount(recordCount);
		
		// 数据填充
		for(int i = 0; auditList != null && i < auditList.size(); i++) {
			Audit a = auditList.get(i);
			
			// 申请单类型编码
			String bizCode = a.getBizCode();
			a.setBiz(ItemService.getItem("申请单类型", bizCode));
			
			// 适用组织机构的编号
			Long orgId = a.getOrganizationId();
			a.setOrganization(OrganizationService.getObject(orgId));
			
			// 申请人编号
			Long userId = a.getUserId();
			a.setUser(UserService.getObject(userId));
		}
		
		view.addObject("audit", audit);
		view.addObject("auditList", auditList);
		view.addObject("pagination", pagination);
				
		return view;
	}

	/**
	 * 审批流程选择
	 * 
	 * @param audit 查询条件
	 * @param pagination 分页数据
	 * 
	 * @return 选择页面
	 */
	@RequestMapping("/auditSelect.do")
	public ModelAndView doSelect(@EntityParam Audit audit, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("config/auditSelect");

		// 设置缺省值：有效标识
		// 默认查询有效记录
		if(audit.getActiveFlag() == null) {
			audit.setActiveFlag(true);
		}
	
		// 构造下拉框数据
		configDropdownList(audit, view);
		
		List<Audit> auditList = AuditService.list(audit, null, pagination);
		
		// 统计符合条件的结果记录数量
		int recordCount = AuditService.count(audit, null);
		pagination.setRecordCount(recordCount);
		
		// 数据填充
		for(int i = 0; auditList != null && i < auditList.size(); i++) {
			Audit a = auditList.get(i);
			
			// 申请单类型编码
			String bizCode = a.getBizCode();
			a.setBiz(ItemService.getItem("申请单类型", bizCode));
			
			// 适用组织机构的编号
			Long orgId = a.getOrganizationId();
			a.setOrganization(OrganizationService.getObject(orgId));
			
			// 申请人编号
			Long userId = a.getUserId();
			a.setUser(UserService.getObject(userId));
		}
		
		view.addObject("audit", audit);
		view.addObject("auditList", auditList);
		view.addObject("pagination", pagination);

		return view;
	}
	
	/**
	 * 新增页面显示
	 * 
	 * @param audit 预设定的数据，比如在指定的分类下新增记录
	 * 
	 * @return 新增页面
	 */
	@GetMapping("/auditAdd.do")
	public ModelAndView doAdd(@EntityParam Audit audit) {
		ModelAndView view = new ModelAndView("config/auditAdd");

		// 构造下拉框数据
		configDropdownList(audit, view);
		
		// 默认新增有效数据
		audit.setActiveFlag(true);
		
		// 保存预设定的数据
		view.addObject("audit", audit);

		return view;  
	}
	
	/**
	 * 新增页面数据提交
	 * 
	 * @param audit 审批流程
     * @param request HTTP请求
	 * 
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/auditAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam Audit audit, HttpServletRequest request) {
		// 是否存在重复记录？
		if(exist(audit)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		int result = AuditService.insert(audit);
		
		if(result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}

		// 保存环节信息
		saveItems(audit, request);

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("新增审批流程");
        log.setBizData(audit);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 修改页面显示
	 * 
	 * @param auditId 流程编号（关键字）
	 * 
	 * @return 修改页面
	 */
	@GetMapping("/auditUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value="id") Long auditId) {
		ModelAndView view = new ModelAndView("config/auditUpdate");

		if(auditId == null || auditId == 0L) {
			return view;
		}

		// 查询条件
		Audit where = new Audit();
		where.setId(auditId);
		
		Audit audit = AuditService.getObject(where, null);
		
		if (audit == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + auditId + "的审批流程");
			return common;
		}
		
		// 适用组织机构的编号
		Long orgId = audit.getOrganizationId();
		audit.setOrganization(OrganizationService.getObject(orgId));
		
		// 申请人编号
		Long userId = audit.getUserId();
		audit.setUser(UserService.getObject(userId));

		AuditItem aiWhere = new AuditItem();
		aiWhere.setAuditId(audit.getId());
		// 审批环节列表
		List<AuditItem> aiList = AuditItemService.list(aiWhere, null, null);
		// 填充数据
		for(int i = 0; aiList != null && i < aiList.size(); i++) {
			AuditItem ai = aiList.get(i);
			
			// 审批角色编号，关联system_role_base.RoleId
			Long roleId = ai.getChoiceRoleId();
			ai.setChoiceRole(RoleService.getObject(roleId));
			
			// 审批人员编号串，以半角逗号间隔
			String ids = ai.getChoiceIds();
			ai.setChoiceNames(UserService.getUserNamesByUserIds(ids));
		}
		if(aiList != null) {
			view.addObject("auditItemList", aiList);
		}
		
		view.addObject("audit", audit);
		
		// 构造下拉框数据
		configDropdownList(audit, view);
		
		return view;
	}
	
	/**
	 * 修改页面数据提交
	 * 
	 * @param audit 审批流程
     * @param request HTTP请求
	 * 
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/auditUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam Audit audit, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long auditId = audit.getId();

		if(auditId == null || auditId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 是否存在重复记录？
		if(exist(audit)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		// 第1个参数audit，取关键字段audit.auditId为条件
		// 第3个参数audit，取audit内关键字段以外其它属性数据
		int result = AuditService.update(audit, null, audit);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 保存环节信息
		saveItems(audit, request);

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("修改审批流程");
        log.setBizData(audit);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 * 
	 * @param auditId 流程编号（关键字）
     * @param request HTTP请求
	 * 
	 * @return 删除结果
	 */
	@PostMapping(value = "/auditDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value="id") Long auditId, HttpServletRequest request) {

		if(auditId == null || auditId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 获取待删除的对象，用于日志记录
        Audit audit = AuditService.getObject(auditId);
        if(audit == null) {
            return ResponseBase.DATA_NOT_FOUND;
        }

		// 限定条件
		Audit where = new Audit();
		where.setId(auditId);
		
		int result = AuditService.delete(where, null);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		
		// 删除之前的环节信息
		AuditItem aiWhere = new AuditItem();
		aiWhere.setAuditId(auditId);
		AuditItemService.delete(aiWhere, null);

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("删除审批流程");
        log.setBizData(audit);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 展示页面
	 * 
	 * @param auditId 流程编号（关键字）
	 * 
	 * @return 展示页面
	 */
	@RequestMapping("/auditView.do")
	public ModelAndView doView(@RequestParam(value="id") Long auditId) {
		ModelAndView view = new ModelAndView("config/auditView");

		if(auditId == null || auditId == 0L) {
			return view;
		}

		// 查询条件
		Audit where = new Audit();
		where.setId(auditId);
		
		Audit audit = AuditService.getObject(where, null);
		
		if(audit == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + auditId + "的审批流程");
			return common;
		}
	
		// 申请单类型编码
		String bizCode = audit.getBizCode();
		audit.setBiz(ItemService.getItem("申请单类型", bizCode));
		
		// 适用组织机构的编号
		Long orgId = audit.getOrganizationId();
		audit.setOrganization(OrganizationService.getObject(orgId));
		
		// 申请人编号
		Long userId = audit.getUserId();
		audit.setUser(UserService.getObject(userId));

		AuditItem aiWhere = new AuditItem();
		aiWhere.setAuditId(audit.getId());
		
		// 审批环节列表
		List<AuditItem> aiList = AuditItemService.list(aiWhere, null, null);
		// 填充数据
		for(int i = 0; aiList != null && i < aiList.size(); i++) {
			AuditItem ai = aiList.get(i);
			
			// 审批角色编号，关联system_role_base.RoleId
			Long roleId = ai.getChoiceRoleId();
			ai.setChoiceRole(RoleService.getObject(roleId));
			
			// 审批人员编号串，以半角逗号间隔
			String ids = ai.getChoiceIds();
			ai.setChoiceNames(UserService.getUserNamesByUserIds(ids));
		}
		
		if(aiList != null) {
			view.addObject("auditItemList", aiList);
		}

		view.addObject("audit", audit);
		
		return view;
	}

	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 * 
	 * @param audit 查询条件
	 * @param view 视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(Audit audit, ModelAndView view) {

		// 申请单类型编码，关联config_item_base.ItemCode[申请单类型]列表
		List<Item> bizList = ItemService.getItemList("申请单类型", null);
		view.addObject("bizList", bizList);		
		
		// 审批标识{1：并签2：会签}列表
		List<KeyValue> attendList = AuditItemService.getAttendList();
		view.addObject("attendList", attendList);

		// 所有角色列表
		List<Role> roleList = RoleService.list(new Role(), "RoleName not like '_temp_%'", null);
		if(roleList != null) {
			view.addObject("roleList", roleList);
		}
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 * 
	 * @param temp
	 *            将要修改的审批流程POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(Audit temp) {
		// 检查修改的审批流程是否有重复记录
		Audit form = new Audit();
		
		// 关键字限定条件
		String str = (temp.getId() == null ? null : "AuditId != " + temp.getId());

		// 其它信息限定条件
		// 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
		form.setBizCode(temp.getBizCode());
		// 适用组织机构的编号，关联basis_organization_base.OrganizationId
		form.setOrganizationId(temp.getOrganizationId());
		// 申请人编号，关联system_user_base.UserId
		form.setUserId(temp.getUserId());

		return (AuditService.getObject(form, str) != null);
	}

	/**
	 * 保存环节信息
	 * 
	 * @param audit 审批流程主信息
     * @param request HTTP请求
	 */
	private void saveItems(Audit audit, HttpServletRequest request) {
		// 审批流程主信息编号
		Long auditId = audit.getId();
		if(auditId == null) {
			return;
		}
		
		// 删除之前的环节信息
		AuditItem aiWhere = new AuditItem();
		aiWhere.setAuditId(auditId);
		AuditItemService.delete(aiWhere, null);
		
		// 审批角色编号
		String[] choiceRoleId = request.getParameterValues("choiceRoleId");
		// 审批人员编号串
		String[] choiceIds = request.getParameterValues("choiceIds");
		// 审批标识{1：并签2：会签}
		String[] attendFlag = request.getParameterValues("attendFlag");
		// 驳回是否可继续审批的标识
		String[] continueFlag = request.getParameterValues("continueFlag");

		for (int i = 0; choiceIds != null && i < choiceIds.length; i++) {
			AuditItem ai = new AuditItem();
			ai.setAuditId(auditId);
			// 审批角色编号
			ai.setChoiceRoleId(LogicUtility.parseLong(choiceRoleId[i], 0L));
			// 审批人员编号串
			ai.setChoiceIds(choiceIds[i]);
			// 审批标识{1：并签2：会签}
			int flag = LogicUtility.parseInt(attendFlag[i], 0);
			ai.setAttendFlag(new Integer(flag));
			// 驳回是否可继续审批的标识
			boolean bl = (continueFlag != null && "true"
					.equals(continueFlag[i]));
			ai.setContinueFlag(new Boolean(bl));
			// 任务序号（从1开始）
			ai.setTaskIndex(new Integer(i + 1));

			AuditItemService.insert(ai);
		}
	}
}
