package com.sample.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.js.icode.config.data.AuditTask;
import cn.js.icode.config.service.AuditService;
import cn.js.icode.config.service.AuditTaskService;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.StatusCode;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.spring.parameter.EntityParam;
import team.bangbang.spring.parameter.SessionUser;
import team.bangbang.sso.data.Account;

/**
 * 审批示例 - Controller
 *
 * @author ICode Studio
 * @version 1.0 2018-11-01
 */
@Controller
@RequestMapping("/sample")
public class AuditSampleController {
	/* 日志对象 */
	private Logger log = LoggerFactory.getLogger(AuditSampleController.class);

	/**
	 * 处理审批通过的逻辑
	 * @param bizCode	申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo		申请单编号
	 */
	private void doPass(String bizCode, Long bizNo) {
		log.info(bizCode + ":" + bizNo + "审批通过，处理审批通过的逻辑（开始）");
		// TODO: 在这里添加审批通过的逻辑

		log.info(bizCode + ":" + bizNo + "审批通过，处理审批通过的逻辑（结束）");
	}
	
	/**
	 * 处理审批驳回的逻辑
	 * @param bizCode	申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo		申请单编号
	 */
	private void doReject(String bizCode, Long bizNo) {
		log.info(bizCode + ":" + bizNo + "审批驳回，处理审批驳回的逻辑（开始）");
		// TODO: 在这里添加审批驳回的逻辑
		

		log.info(bizCode + ":" + bizNo + "审批驳回，处理审批驳回的逻辑（结束）");
	}
	
	/**
	 * 创建审批任务
	 * 
	 * 此方法完全为了测试需要，用于手动调用，创建审批任务，以便测试后续的审批功能
	 * 
	 * @param applicantId 申请单制单人账户编号
	 * @param bizCode     申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo       申请单编号
	 *
	 * @return 创建的任务数量，其中参与并签、会签的审批会被展开计算。<br>
	 *         即如果2人参加并签、3人参加会签，则此时创建5条任务。<br>
	 *         如果返回的任务数量为0，则表示没有配置审批流程。
	 */
	@RequestMapping("/createAuditTasks.do")
	@ResponseBody
	public ResponseBase createTasks(Long applicantId, String bizCode, Long bizNo) {
		int n = AuditTaskService.createTasks(applicantId, bizCode, bizNo);

		if (n <= 0) {
			ResponseBase rb = new ResponseBase(StatusCode.EXCEPTION_OCCURED, "没有配置审批流程");
			return rb;
		}

		ResponseBase rb = new ResponseBase(StatusCode.SUCCESS, "成功创建审批任务，任务数量：" + n);

		return rb;
	}

	/**
	 * 审批任务
	 * 
	 * @param user 当前操作账户
	 * @param bizCode     审批人账户编号
	 * @param hasAudited  是否审批（该参数决定选择“待审批”还是“已审批”状态的记录），默认为false，查询待审批
	 * @param pagination  分页数据
	 *
	 * @return 审批任务列表页面
	 */
	@RequestMapping("/xxxxAuditTaskList.do")
	public ModelAndView list(@SessionUser Account user, String bizCode, boolean hasAudited, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("sample/xxxxAuditTaskList");
		
		// 审批人的编号
		Long auditUserId = LogicUtility.parseLong(user.getId(), 0L);
		
		// 状态标识{1：对列中，2：待审核，3：已审核}
		// 查询“我”的审批任务列表
		List<AuditTask> auditTaskList = AuditTaskService.getMyAuditTaskList(auditUserId, bizCode, hasAudited,
				pagination.getPageNo(), pagination.getMaxResults());

		// 符合条件的记录
		int count = AuditTaskService.getMyAuditTaskCount(auditUserId, bizCode, hasAudited);

		pagination.setRecordCount(count);

		if (auditTaskList != null) {
			// 填充业务数据，带到页面上展示
			for(AuditTask at : auditTaskList) {
				// 申请单编号
				String bizNo = at.getBizCode();
				
				// TODO: 调用业务Service.getObject()获得业务对象
				Object bizData = null; 
				// TODO: 将业务对象放入AuditTask.bizData中
				at.setBizData(bizData);
			}
			
			view.addObject("auditTaskList", auditTaskList);
		}
		
		view.addObject("bizCode", bizCode);
		view.addObject("hasAudited", hasAudited);		
		view.addObject("pagination", pagination);

		return view;
	}

	/**
	 * 审批任务
	 * 
	 * @param user 当前操作账户
	 * @param bizCode     申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo       申请单编号
	 *
	 * @return 审批任务列表页面
	 */
	@GetMapping("/xxxxAudit.do")
	public ModelAndView doAudit(@SessionUser Account user, String bizCode, String bizNo) {
		ModelAndView view = new ModelAndView("sample/xxxxAudit");
		
		// 1. TODO: 根据申请单编号，调用业务Service.getObject()获得申请单信息
		Object bizData = null; 
		if(bizData != null) {
			// 将申请单信息带到页面上
			view.addObject("bizData", bizData);
		}
		
		// 2. 将当前申请单的审批任务信息形成页面便于显示的HTML字符
		String auditTaskHtml = AuditTaskService.getBillAuditTaskHtml(bizCode, bizNo);
		// 带到页面上
		view.addObject("bizCode", bizCode);
		// 带到页面上
		view.addObject("bizNo", bizNo);
		// 带到页面上
		view.addObject("auditTaskHtml", auditTaskHtml);
		
		return view;
	}

	/**
	 * 审批任务
	 * 
	 * @param user 当前操作账户
	 * @param bizCode     申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo       申请单编号
	 * @param passFlag	   当前环节是否通过
	 * @param comment     审批意见
	 *
	 * @return 审批任务列表页面
	 */
	@PostMapping("/xxxxAuditAction.do")
	@ResponseBody
	public ResponseBase doAuditAction(@SessionUser Account user, String bizCode, String bizNo, boolean passFlag, String comment) {
		ResponseBase rb = null;
		
		try {
			Long auditUserId = LogicUtility.parseLong(user.getId(), 0L);
			// 审批结果{1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}
			int n = AuditService.doAudit(auditUserId, bizCode, bizNo, passFlag, comment);
			
			switch(n) {
				case 1:
					rb = new ResponseBase(StatusCode.SUCCESS, "审批过程中步进");
					break;
				case 2:
					// TODO: 处理审批通过的逻辑
					
					System.out.println("处理审批通过的逻辑：" + bizCode + "：" + bizNo);
					rb = new ResponseBase(StatusCode.SUCCESS, "审批通过");
					break;
				case 3:
					// TODO: 处理审批驳回的逻辑
					
					System.out.println("处理审批驳回的逻辑：" + bizCode + "：" + bizNo);
					rb = new ResponseBase(StatusCode.SUCCESS, "审批驳回");
					break;
				case 4:
					rb = new ResponseBase(StatusCode.DATA_NOT_FOUND, "审批任务不存在");
					break;
				case 5:
					rb = new ResponseBase(StatusCode.EXCEPTION_OCCURED, "填写审批任务失败");
					break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
		
		return rb;
	}

	/**
	 * 审批任务查看
	 * 
	 * @param bizCode     申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo       申请单编号
	 *
	 * @return 审批任务列表页面
	 */
	@GetMapping("/xxxxAuditView.do")
	public ModelAndView doAudit(String bizCode, String bizNo) {
		ModelAndView view = new ModelAndView("sample/xxxxAuditView");
		
		// 1. TODO: 根据申请单编号，调用业务Service.getObject()获得申请单信息
		Object bizData = null; 
		if(bizData != null) {
			// 将申请单信息带到页面上
			view.addObject("bizData", bizData);
		}
		
		// 2. 将当前申请单的审批任务信息形成页面便于显示的HTML字符
		String auditTaskHtml = AuditTaskService.getBillAuditTaskHtml(bizCode, bizNo);
		// 带到页面上
		view.addObject("bizCode", bizCode);
		// 带到页面上
		view.addObject("bizNo", bizNo);
		// 带到页面上
		view.addObject("auditTaskHtml", auditTaskHtml);
		
		return view;
	}
}