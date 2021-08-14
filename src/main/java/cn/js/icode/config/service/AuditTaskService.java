package cn.js.icode.config.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.js.icode.basis.service.OrganizationService;
import team.bangbang.common.CommonMPI;
import team.bangbang.common.config.Constants;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.common.utility.TreeNode;
import team.bangbang.common.utility.TreeUtil;
import cn.js.icode.config.data.Audit;
import cn.js.icode.config.data.AuditItem;
import cn.js.icode.config.data.AuditTask;
import cn.js.icode.config.mapper.AuditTaskMapper;
import cn.js.icode.system.data.User;
import cn.js.icode.system.service.UserRoleService;
import cn.js.icode.system.service.UserService;

/**
 * 审批任务 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-10-31
 */
@Service
public final class AuditTaskService {
	/* 审批任务（AuditTask）Mapper */
	@Resource
	private AuditTaskMapper _auditTaskMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static AuditTaskMapper auditTaskMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		auditTaskMapper = _auditTaskMapper;
	}

	/**
	 * 得到指定的审批任务
	 * 
	 * @param taskId
	 *			指定的任务编号
	 * @return 审批任务
	 */
	public static AuditTask getObject(Long taskId) {
		if(auditTaskMapper == null) {
			return null;
		}

		// 参数校验

		if(taskId == null || taskId == 0L) {
			return null;
		}

		// 查询条件
		AuditTask form = new AuditTask();
		form.setId(taskId);
		
		return auditTaskMapper.getObject(form, null);
	}

	/**
	 * 插入一条审批任务
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(AuditTask data) {
		if(auditTaskMapper == null) {
			return 0;
		}

		if (data.getId() == null) {

			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);

		}

		// 设置缺省值：驳回时是否继续审批
		if(data.getContinueFlag() == null) {
			data.setContinueFlag(false);
		}
	
		return auditTaskMapper.insert(data);
	}

	/**
	 * 删除审批任务
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(AuditTask where, String appendix) {
		if(auditTaskMapper == null) {
			return 0;
		}

		return auditTaskMapper.delete(where, appendix);
	}

	/**
	 * 查询一条审批任务，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static AuditTask getObject(AuditTask where, String appendix) {
		if(auditTaskMapper == null) {
			return null;
		}

		return auditTaskMapper.getObject(where, appendix);
	}

	/**
	 * 修改审批任务
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(AuditTask where, String appendix, AuditTask data) {
		if(auditTaskMapper == null) {
			return 0;
		}

		return auditTaskMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条审批任务，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<AuditTask> list(AuditTask where, String appendix, Pagination pagination) {
		if(auditTaskMapper == null) {
			return Collections.emptyList();
		}

		return auditTaskMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的审批任务数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(AuditTask where, String appendix) {
		if(auditTaskMapper == null) {
			return 0;
		}

		return auditTaskMapper.count(where, appendix);
	}
	
	/**
	 * 获得审批方式{1：并签2：会签}列表
	 * 
	 * @return 审批方式{1：并签2：会签}列表
	 */
	public static List<KeyValue> getAttendList() {
		return CommonMPI.getDictionaryList(AuditTask.attendFlags);
	}

	/**
	 * 获得状态{1：对列中，2：待审核，3：已审核}列表
	 * 
	 * @return 状态{1：对列中，2：待审核，3：已审核}列表
	 */
	public static List<KeyValue> getStatusList() {
		return CommonMPI.getDictionaryList(AuditTask.statusFlags);
	}

	/**
	 * 创建审批任务
	 * 
	 * @param applicantId 申请单制单人账户编号
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * 
	 * @return 创建的任务数量，其中参与并签、会签的审批会被展开计算。<br>
	 *         即如果2人参加并签、3人参加会签，则此时创建5条任务。<br>
	 *         如果返回的任务数量为0，则表示没有配置审批流程。
	 */
	public static int createTasks(Long applicantId, String bizCode, Object bizNo) {
		// 参数不完整
		if (applicantId == null || bizCode == null || bizCode.trim().length() == 0
				|| bizNo == null) {
			return 0;
		}
		
		// 获得申请单制单人系统账户资料
		User applicant = UserService.getObject(applicantId);
		if (applicant == null) {
			return 0;
		}

		// getActiveAudit() 已经加载了加载审批环节信息
		Audit audit = AuditService.getActiveAudit(applicant, bizCode);

		// 没有匹配的审批模板
		if (audit == null) {
			return 0;
		}
		
		// 删除之前的审批环节
		AuditTask atWhere = new AuditTask();
		atWhere.setBizCode(bizCode);
		atWhere.setBizNo(String.valueOf(bizNo));
		AuditTaskService.delete(atWhere, null);

		// 任务数量
		int nCount = 0;
		// 从模板中获取审批环节的配置列表
		List<AuditItem> aiList = audit.getAuditItemList();

		// 当前时间
		Date now = new Date();
		// 任务序号，从1开始
		int taskIndex = 0;
		for (int i = 0; aiList != null && i < aiList.size(); i++) {
			AuditItem ai = aiList.get(i);
			// 构造一个审批任务
			AuditTask at = new AuditTask();
			// 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
			at.setBizCode(bizCode);
			// 业务对象的编号
			at.setBizNo(String.valueOf(bizNo));
			// 任务序号，从1开始
			at.setTaskIndex(taskIndex + 1);
			// 审批方式标识{1：并签2：会签}
			at.setAttendFlag(ai.getAttendFlag());
			// 驳回时是否继续审批的标识
			at.setContinueFlag(ai.getContinueFlag());
			// 状态标识{1：对列中，2：待审批，3：已审批}
			if (i == 0) {
				// 设置第一个环节的任务为 2：待审批
				at.setStatusFlag(new Integer(2));
			} else {
				at.setStatusFlag(new Integer(1));
			}
			// 实际审批人编号
			// 审批结果标识{审批通过,审批驳回}
			// 审批意见
			// 系统根据审批自动填写的备注信息
			// 审批时间

			// 任务登记时间
			at.setCreateTime(now);

			// 根据配置的原定审批人编号，展开设置任务
			// 如果设置的是角色，通过角色抽取相应的人员编号
			Long roleId = ai.getChoiceRoleId();
			// 原定审批人编号
			String choiceIds = null;
			if (roleId == null || roleId == 0L) {
				// 直接使用指定的审批人
				choiceIds = ai.getChoiceIds();
			} else {
				// 根据指定的角色查找审批人
				choiceIds = getUserIdsByRoleId(applicant, roleId);
				// 此种情况下审批方式一律置为会签
				// 审批方式标识{1：并签2：会签}
				at.setAttendFlag(new Integer(2));
			}

			// 根据指定的角色没有找到审批人，则此环节作废
			if (choiceIds == null || choiceIds.trim().length() == 0) {
				continue;
			}

			// 当前环节是否实际生成了任务
			boolean blgen = false;
			String[] ids = LogicUtility.splitString(choiceIds, ",");
			for (int j = 0; ids != null && j < ids.length; j++) {
				if (ids[j] == null) {
					continue;
				}
				// 清空关键字的值，让系统自动创建
				at.setId(null);
				// 设置计划审批人
				long uId = LogicUtility.parseLong(ids[j], 0L);
				if(uId == 0L) {
					continue;
				}
				
				at.setPlanUserId(uId);

				if (AuditTaskService.insert(at) > 0) {
					nCount++;
					blgen = true;
				}
			} // end for
			
			if(blgen) taskIndex = at.getTaskIndex();
		} // end for

		return nCount;
	}
	
	/**
	 * 获得指定操作账户在指定类型申请单上的审批任务列表，分为待审批、已审批2种审批任务
	 * 
	 * @param auditUserId 指定操作账户的编号
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param hasAudited 是否审批，用于区分待审批、已审批2种审批任务
	 * @param pageNo 分页参数 - 页号，默认为1
	 * @param pageSize 分页参数 - 每页最大记录数量，默认为10
	 * 
	 * @return 审批任务列表
	 */
	public static List<AuditTask> getMyAuditTaskList(Long auditUserId, String bizCode, boolean hasAudited, Integer pageNo, Integer pageSize) {
		if(pageNo == null) pageNo = 1;
		if(pageSize == null) pageSize = 10;
		
		// 构造查询条件
		AuditTask form = new AuditTask();
		form.setPlanUserId(auditUserId);
		form.setBizCode(bizCode);
		if(hasAudited) {
			// 状态标识{1：对列中，2：待审核，3：已审核}
			form.setStatusFlag(3);
		} else {
			// 状态标识{1：对列中，2：待审核，3：已审核}
			form.setStatusFlag(2);
		}
		
		// 分页数据
		Pagination p = new Pagination();
		p.setRecordCount(Integer.MAX_VALUE);
		p.setMaxResults(pageSize);
		p.setPageNo(pageNo);
		
		return AuditTaskService.list(form, null, p);
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
	public static boolean hasPendingAuditTask(Long auditUserId, String bizCode, String bizNo) {
		if(auditUserId == null || bizCode == null || bizCode.trim().length() == 0
				|| bizNo == null || bizNo.trim().length() == 0) {
			return false;
		}
		
		// 构造查询条件
		AuditTask form = new AuditTask();
		form.setPlanUserId(auditUserId);
		form.setBizCode(bizCode);
		// 状态标识{1：对列中，2：待审核，3：已审核}
		form.setStatusFlag(2);
		form.setBizNo(bizNo);
		
		AuditTask at = AuditTaskService.getObject(form, null);
		
		return (at != null);
	}
	
	/**
	 * 获得当前操作账户在指定类型申请单上的审批任务数量，分为已审批、未审批2种审批任务
	 * 
	 * @param auditUserId 实际审批人的编号
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param hasAudited 是否审批，用于区分待审批、已审批2种审批任务
	 * 
	 * @return 审批任务数量
	 */
	public static int getMyAuditTaskCount(Long auditUserId, String bizCode, boolean hasAudited) {		
		// 构造查询条件
		AuditTask form = new AuditTask();
		form.setPlanUserId(auditUserId);
		form.setBizCode(bizCode);
		if(hasAudited) {
			// 状态标识{1：对列中，2：待审核，3：已审核}
			form.setStatusFlag(3);
		} else {
			// 状态标识{1：对列中，2：待审核，3：已审核}
			form.setStatusFlag(2);
		}
		
		return AuditTaskService.count(form, null);
	}
	
	/**
	 * 获得指定申请单的审批任务列表，包括已经完成、正在进行、等待执行的审批任务
	 * 
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * 
	 * @return 审批任务列表
	 */
	public static List<AuditTask> getAuditTaskList(String bizCode, Object bizNo) {
		// 构造查询条件
		AuditTask form = new AuditTask();
		form.setBizCode(bizCode);
		form.setBizNo(String.valueOf(bizNo).trim());

		return AuditTaskService.list(form, null, null);
	}

	/**
	 * 启动的指定的批次审核任务
	 * 
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * @param taskIndex 任务序号
	 *            
	 * @return 本次动作启动的审核任务数量
	 */
	public static int startTasks(String bizCode, Object bizNo, int taskIndex) {
		// 更新条件
		AuditTask atWhere = new AuditTask();
		atWhere.setBizCode(bizCode);
		atWhere.setBizNo(String.valueOf(bizNo));
		atWhere.setTaskIndex(taskIndex);
		// 状态标识{1：对列中，2：待审核，3：已审核}
		atWhere.setStatusFlag(1);
		
		// 更新数据
		AuditTask atData = new AuditTask();
		// 状态标识{1：对列中，2：待审核，3：已审核}
		atData.setStatusFlag(2);
		atData.setCreateTime(new Date());
		
		int nCount = update(atWhere, null, atData);

		return nCount;
	}

	/**
	 * 删除指定业务对象的所有未执行（StatusFlag = 1,2）的审核任务，已经执行的不删除。
	 * 
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * 
	 * @return 删除的记录数量
	 */
	public static int deletePendingTasks(String bizCode, Object bizNo) {
		if(bizCode == null || bizNo == null) {
			return -1;
		}

		AuditTask at = new AuditTask();
		at.setBizNo(String.valueOf(bizNo));
		at.setBizCode(bizCode);

		// 状态{1：对列中，2：待审核，3：已审核}
		return delete(at, "(StatusFlag = 1 or StatusFlag = 2)");
	}

	/**
	 * 删除指定业务对象的所有待执行（StatusFlag = 2）的审核任务，已经执行的和队列中的不删除。
	 * 
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * 
	 * @return 删除的记录数量
	 */
	public static int deleteReadyTasks(String bizCode, Object bizNo) {
		if(bizCode == null || bizNo == null) {
			return -1;
		}

		AuditTask at = new AuditTask();
		at.setBizNo(String.valueOf(bizNo));
		at.setBizCode(bizCode);
		// 状态{1：对列中，2：待审核，3：已审核}
		at.setStatusFlag(new Integer(2));

		return delete(at, null);
	}
	

	/* 日期格式化对象 */
	private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * 
	 * @return 指定申请单下的审批任务HTML字符串
	 */
	public static String getBillAuditTaskHtml(String bizCode, Object bizNo) {
		// 获取指定申请单的审批任务
		List<AuditTask> atList = AuditTaskService.getAuditTaskList(bizCode, bizNo);

		// 填充数据
		for(int i = 0; atList != null && i < atList.size(); i++) {
			AuditTask at = atList.get(i);
			
			// 审批人编号，关联system_user_base.UserId
			Long planUserId = at.getPlanUserId();
			at.setPlanUser(UserService.getObject(planUserId));
		}
		
		StringBuffer buff = new StringBuffer();
		// 一个layui-card块
		buff.append("<div class=\"layui-card\">").append(Constants.LINE_SEPARATOR);
		buff.append("	<div class=\"layui-card-header\">审批信息</div>").append(Constants.LINE_SEPARATOR);
		buff.append("	<div class=\"layui-card-body\" style=\"padding: 15px;\">").append(Constants.LINE_SEPARATOR).append(Constants.LINE_SEPARATOR);
		
		// 写入任务的table表格
		buff.append(fillTaskTable(atList)).append(Constants.LINE_SEPARATOR);

		buff.append("	</div>");
		buff.append("</div>");
		
		return buff.toString();
	}

	/**
	 * 将任务的table表格写入字符串容器中
	 * 
	 * @param atList 审批任务列表
	 * 
	 * @return HTML表格
	 */
	private static String fillTaskTable(List<AuditTask> atList) {
		if(atList == null || atList.isEmpty()) {
			return "<span style=\"color:#FF5722;\">（当前无审批任务）</span>";
		}
		
		StringBuffer buff = new StringBuffer();
		
		buff.append("<table class=\"layui-table\" id=\"auditTaskTable\">")
				.append(Constants.LINE_SEPARATOR);
		buff.append(" <thead>").append(Constants.LINE_SEPARATOR);
		buff.append(" <tr>").append(Constants.LINE_SEPARATOR);
		buff.append("   <th width=\"30\">序号</th>").append(Constants.LINE_SEPARATOR);
		buff.append("   <th colspan=\"2\">审批人</th>").append(
				Constants.LINE_SEPARATOR);
		buff.append("   <th>任务状态</th>").append(Constants.LINE_SEPARATOR);
		buff.append("   <th>审批结果</th>").append(Constants.LINE_SEPARATOR);
		buff.append("   <th>审批意见</th>").append(Constants.LINE_SEPARATOR);
		buff.append("   <th>审批时间</th>").append(
				Constants.LINE_SEPARATOR);
		buff.append(" </tr>").append(Constants.LINE_SEPARATOR);
		buff.append(" </thead>").append(Constants.LINE_SEPARATOR);

		buff.append(" <tbody>").append(Constants.LINE_SEPARATOR);
		// 写入任务的数据行
		buff.append(fillTaskRows(atList)).append(Constants.LINE_SEPARATOR);
		buff.append(" </tbody>").append(Constants.LINE_SEPARATOR);

		buff.append("</table>").append(Constants.LINE_SEPARATOR);
		
		return buff.toString();
	}

	/**
	 * 将任务的数据行写入字符串容器中
	 * 
	 * @param atList 审批任务列表
	 * 
	 * @return HTML表格行
	 */
	private static String fillTaskRows(List<AuditTask> atList) {
		// 上一轮审核方式标识
		int lastTaskIndex = 0;
		// 审核批次
		int nBatch = 0;

		StringBuffer buff = new StringBuffer();
		
		for (int i = 0; atList != null && i < atList.size(); i++) {
			AuditTask at = atList.get(i);
			buff.append(" <tr>").append(Constants.LINE_SEPARATOR);
			// 当前审核方式标识
			int currTaskIndex = at.getTaskIndex().intValue();
			if (currTaskIndex != lastTaskIndex) {
				// 新增一行
				++nBatch;

				String rowSpan = getRowSpan(atList, i);
				String attendName = ("".equals(rowSpan)) ? "&nbsp;" : at
						.getAttendName();
				// 替换之前的<icode:batch/>标记
				buff.append("   <td" + rowSpan + ">" + nBatch
						+ "</td>" + Constants.LINE_SEPARATOR);
				buff.append("   <td " + rowSpan
						+ ">"
						+ attendName + "</td>" + Constants.LINE_SEPARATOR);
				lastTaskIndex = currTaskIndex;
			}

			buff.append("<td>");
			if(at.getPlanUser() != null) {
				buff.append(at.getPlanUser().getUserName());
			} else {
				buff.append(at.getPlanUserId());
			}
			buff.append("</td>").append(Constants.LINE_SEPARATOR);

			buff.append("<td>");
			// 
			if (at.getStatusFlag() != null
					&& at.getStatusFlag().intValue() != 1) {
				buff.append(at.getStatusName());
			}
			buff.append("</td>").append(Constants.LINE_SEPARATOR);

			buff.append("<td>");
			if (at.getPassFlag() != null) {
				buff.append(at.getPassFlag() ? "通过" : "驳回");
			}
			buff.append("</td>").append(Constants.LINE_SEPARATOR);

			if (at.getAuditComment() != null) {
				buff.append("<td title=\"");
				buff.append(LogicUtility.getQuotedString(at.getAuditComment()));
				buff.append("\">");
				buff.append(LogicUtility.getSegmentString(at.getAuditComment(),
						25));
			} else {
				buff.append("<td>");
			}

			buff.append("</td>").append(Constants.LINE_SEPARATOR);

			buff.append("<td>");
			if (at.getAuditTime() != null) {
				buff.append(formatter.format(at.getAuditTime()));
			}

			buff.append("</td>").append(Constants.LINE_SEPARATOR);
			
			buff.append(" </tr>");
		}
		
		return buff.toString();
	}

	private static String getRowSpan(List<AuditTask> atList, int n) {
		AuditTask at1 = atList.get(n);

		int rowSpan = 1;
		for (int i = n + 1; i < atList.size(); i++) {
			AuditTask at2 = atList.get(i);
			if (at2.getTaskIndex().equals(at1.getTaskIndex())) {
				rowSpan++;
			}
		}

		return (rowSpan == 1) ? "" : " rowspan=\"" + rowSpan + "\"";
	}

	/**
	 * 获得匹配指定审批角色的操作用户编号串
	 * 
	 * @param user 业务制单人，必须包含用户编号和所在组织编号
	 * @param roleId 指定的审批角色编号
	 * 
	 * @return 匹配指定审批角色的操作账户编号串
	 */
	private static String getUserIdsByRoleId(User user, Long roleId) {
		// 业务制单人信息中必须包含所在组织编号
		if (user == null || roleId == null || roleId == 0L) {
			return null;
		}

		// 业务制单人所在组织编号
		Long orgId = user.getOrganizationId();
		Long[] roleIds = { roleId };

		// 整个组织机构树，不包含被删除的
		TreeNode root = OrganizationService.getRoot(false);

		// 从业务制单人所在组织开始，上溯查找匹配的角色人员，直到找到为止
		while (true) {
			if (orgId == null || orgId == 0L) {
				// 已经追溯到最高组织节点了
				break;
			}

			// 直接管辖（尤其注意是管辖，不是所在组织）组织、
			// 且角色在指定范围内的操作用户列表
			List<User> lst = UserRoleService.getLeaderList(orgId, roleIds);
			if (lst != null && lst.size() > 0) {
				// 找到了
				StringBuffer sb = new StringBuffer();
				for (User temp : lst) {
					if (sb.length() > 0)
						sb.append(",");
					sb.append(temp.getId());
				}

				return sb.toString();
			}

			// 没有找到
			// 上溯到上一级组织
			TreeNode node = TreeUtil.findNode(root, orgId);
			orgId = ((node == null) ? null : (Long) node.getParentId());
		}

		return null;
	}

	/**
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * 
	 * @return 展现审批任务列表的字符串
	 */
	public static String getBillAuditTaskString(String bizCode, Object bizNo) {
		// 获取指定申请单的审批任务
		List<AuditTask> atList = AuditTaskService.getAuditTaskList(bizCode, bizNo);
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0; atList != null && i < atList.size(); i++) {
			AuditTask at = atList.get(i);
			// 1	 	测试账号	已审核	通过	通过意见是好的	2019-09-29 22:39
			sb.append(at.getTaskIndex()).append(". ");
			// 审批结果
			Boolean bf = at.getPassFlag();
			if(bf == null) {
				// 审批人
				Long userId = at.getPlanUserId();
				User u = UserService.getObject(userId);
				sb.append(u != null ? u.getUserName() : "");					
				sb.append(" [").append(at.getStatusName()).append("]").append("\r\n");
				continue;
			}
			
			// 审批人
			Long userId = at.getAuditUserId();
			User u = UserService.getObject(userId);
			sb.append(u != null ? u.getUserName() : "");
	
			sb.append(bf ? " 审批通过" : " 审批驳回");
			if(at.getAuditComment() != null) {
				sb.append("[").append(at.getAuditComment()).append("]");
			}
			sb.append("[").append(LogicUtility.getTimeAsString(at.getAuditTime())).append("]");
			sb.append("\r\n");
		}
		
		return sb.toString();
	}
}
