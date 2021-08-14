package cn.js.icode.config.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.js.icode.basis.service.OrganizationService;
import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.utility.TreeNode;
import team.bangbang.common.utility.TreeUtil;
import cn.js.icode.config.data.Audit;
import cn.js.icode.config.data.AuditItem;
import cn.js.icode.config.data.AuditTask;
import cn.js.icode.config.mapper.AuditMapper;
import cn.js.icode.system.data.User;

/**
 * 审批流程 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-10-30
 */
@Service
public final class AuditService {
	/* 审批流程（Audit）Mapper */
	@Resource
	private AuditMapper _auditMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static AuditMapper auditMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		auditMapper = _auditMapper;
	}

	/**
	 * 得到指定的审批流程
	 * 
	 * @param auditId
	 *			指定的流程编号
	 * @return 审批流程
	 */
	public static Audit getObject(Long auditId) {
		if(auditMapper == null) {
			return null;
		}

		// 参数校验

		if(auditId == null || auditId == 0L) {
			return null;
		}

		// 查询条件
		Audit form = new Audit();
		form.setId(auditId);
		
		return auditMapper.getObject(form, null);
	}

	/**
	 * 插入一条审批流程
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Audit data) {
		if(auditMapper == null) {
			return 0;
		}

		if (data.getId() == null) {

			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);

		}

		// 设置缺省值：有效标识
		if(data.getActiveFlag() == null) {
			data.setActiveFlag(false);
		}
	
		return auditMapper.insert(data);
	}

	/**
	 * 删除审批流程
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Audit where, String appendix) {
		if(auditMapper == null) {
			return 0;
		}

		return auditMapper.delete(where, appendix);
	}

	/**
	 * 查询一条审批流程，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Audit getObject(Audit where, String appendix) {
		if(auditMapper == null) {
			return null;
		}

		return auditMapper.getObject(where, appendix);
	}

	/**
	 * 修改审批流程
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Audit where, String appendix, Audit data) {
		if(auditMapper == null) {
			return 0;
		}

		return auditMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条审批流程，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Audit> list(Audit where, String appendix, Pagination pagination) {
		if(auditMapper == null) {
			return Collections.emptyList();
		}

		return auditMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的审批流程数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Audit where, String appendix) {
		if(auditMapper == null) {
			return 0;
		}

		return auditMapper.count(where, appendix);
	}

	
	/**
	 * 对指定的申请单进行审批
	 * 
	 * @param auditUserId 实际审批人的编号
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * @param bizNo 申请单编号
	 * @param passFlag	   当前环节是否通过
	 * @param comment     审批意见
	 * 
	 * @return 审批结果{1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}
	 */
	public static int doAudit(Long auditUserId, String bizCode, Object bizNo, boolean passFlag, String comment) {
		// 获取审批任务
		AuditTask atWhere = new AuditTask();
		atWhere.setPlanUserId(auditUserId);
		atWhere.setBizCode(bizCode);
		atWhere.setBizNo(String.valueOf(bizNo));
		// 状态标识{1：对列中，2：待审核，3：已审核}
		atWhere.setStatusFlag(2);
		AuditTask task = AuditTaskService.getObject(atWhere, null);
		if (task == null) {
			// 审批结果{1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}
			return 4;
		}

		// //////////////////////// 执行审批 /////////////////////////
		// 实际审批人编号
		task.setAuditUserId(auditUserId);
		// 当前环节是否通过
		task.setPassFlag(passFlag);
		// 审批意见
		task.setAuditComment(comment);
		// 审批时间
		task.setAuditTime(new Date());
		// 状态标识{1：对列中，2：待审批，3：已审批}
		task.setStatusFlag(new Integer(3));

		int nRows = AuditTaskService.update(task, null, task);
		if (nRows <= 0) {
			// {1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}
			return 5;
		}

		// 如果当前环节审批未通过
		if (!task.getPassFlag()) {
			// 检查当前环节continueFlag
			boolean continueFlag = task.getContinueFlag().booleanValue();
			if (!continueFlag) {
				// 驳回情况下不可继续
				// 删除所有的pending任务
				AuditTaskService.deletePendingTasks(task.getBizCode(), task.getBizNo());
				
				// 审批结果{1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}
				return 3;
			}
		}

		// 审批通过、虽然驳回但ContinueFlag = true情况下：
		// 审批方式标识{1：并签2：会签}
		if (task.getAttendFlag().intValue() == 1) {
			// 如果当前是并签，则查看当前批次是否还有其它任务
			AuditTask tempTask = new AuditTask();

			tempTask.setBizCode(task.getBizCode());
			tempTask.setBizNo(task.getBizNo());

			tempTask.setTaskIndex(task.getTaskIndex());
			// 状态标识{1：对列中，2：待审批，3：已审批}
			tempTask.setStatusFlag(new Integer(2));

			tempTask = AuditTaskService.getObject(tempTask, null);
			if (tempTask != null) {
				// 当前批次还有其它任务，等待其它任务
				// 审批结果{1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}
				return 1;
			}

			// 当前批次没有其它任务，启动下一轮审批
		} else {
			// 会签 已经有一个人通过，则删除本环节其它 待审批 任务
			AuditTaskService.deleteReadyTasks(task.getBizCode(), task.getBizNo());
		}

		// 启动下一轮审批
		// 下一轮审批表格的任务序号
		int nNextTaskIndex = task.getTaskIndex().intValue() + 1;
		int nStart = AuditTaskService.startTasks(task.getBizCode(), task.getBizNo(), nNextTaskIndex);

		if (nStart <= 0) {
			// 没有下一轮审批，则当前审批已经是最后的审批
			// 根据当前审批结果修改报名单的审批状态
			// 审批结果标识{审批通过,审批驳回}
			if (passFlag) {
				// 最终结果：审批通过
				// 删除等待的审批任务
				AuditTaskService.deletePendingTasks(task.getBizCode(), task.getBizNo());
				
				// {1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}
				return 2;
			}

			// 最终结果：审批驳回
			// 审批结果{1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}
			return 3;
		} // end if

		// 审批结果{1：审批过程中步进2：审批通过3：审批驳回4：审批任务不存在5：填写审批任务失败}
		return 1;
	}

	/**
	 * 得到适合指定单据的审批流程列表，用于提交单据时，自动选择审批流程。
	 * 
	 * @param user 业务制单人，必须包含用户编号和所在组织编号
	 * @param bizCode 申请单类型编码，关联config_item_base.ItemCode[申请单类型]
	 * 
	 * @return 匹配到的审批流程
	 */
	public static Audit getActiveAudit(User user, String bizCode) {
		// 首先按照用户编号去匹配审批流程
		Audit form = new Audit();
		form.setUserId(user.getId());
		form.setBizCode(bizCode);
		form.setActiveFlag(new Boolean(true));
		
		Audit temp = getObject(form, null);

		if (temp != null) {
			// 加载审批环节信息
			AuditItem aiWhere = new AuditItem();
			aiWhere.setAuditId(temp.getId());
			List<AuditItem> aiList = AuditItemService.list(aiWhere, null, null);
			if(aiList != null && aiList.size() > 0) {
				temp.getAuditItemList().addAll(aiList);
			}

			return temp;
		}

		// 有效的组织机构树
		TreeNode tree = OrganizationService.getRoot(false);

		String appendix = "UserId is null";

		// 根据所在组织机构编号去匹配审批流程
		Long orgId = user.getOrganizationId();
		
		form = new Audit();
		form.setBizCode(bizCode);
		form.setActiveFlag(new Boolean(true));
		
		// 如果所在组织为空，则选择申请单下任意一个审批流程
		if(orgId == null || orgId <= 0) {			
			Audit adTemp = getObject(form, null);
			if (adTemp != null) {
				// 加载审批环节信息
				AuditItem aiWhere = new AuditItem();
				aiWhere.setAuditId(adTemp.getId());
				List<AuditItem> aiList = AuditItemService.list(aiWhere, null, null);
				if(aiList != null && aiList.size() > 0) {
					adTemp.getAuditItemList().addAll(aiList);
				}

				// 找到了
				return adTemp;
			}
		}
		
		while (true) {
			// 限定条件
			String strCondition = null;
			if (orgId == null || orgId == 0L) {
				strCondition = appendix + " and OrganizationId is null";
			} else {
				strCondition = appendix + " and OrganizationId = " + orgId;
			}

			Audit adTemp = getObject(form, strCondition);

			if (adTemp != null) {
				// 加载审批环节信息
				AuditItem aiWhere = new AuditItem();
				aiWhere.setAuditId(adTemp.getId());
				List<AuditItem> aiList = AuditItemService.list(aiWhere, null, null);
				if(aiList != null && aiList.size() > 0) {
					adTemp.getAuditItemList().addAll(aiList);
				}

				// 找到了
				return adTemp;
			}

			// 上溯到父节点
			TreeNode node = TreeUtil.findNode(tree, orgId);

			if (node == null) {
				break;
			}

			orgId = (Long)node.getParentId();
		}

		return null;
	}
}
