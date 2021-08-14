package cn.js.icode.config.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import cn.js.icode.config.data.AuditItem;
import cn.js.icode.config.mapper.AuditItemMapper;

/**
 * 审批环节 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-10-30
 */
@Service
public final class AuditItemService {
	/* 审批环节（AuditItem）Mapper */
	@Resource
	private AuditItemMapper _auditItemMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static AuditItemMapper auditItemMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		auditItemMapper = _auditItemMapper;
	}

	/**
	 * 得到指定的审批环节
	 * 
	 * @param itemId
	 *			指定的环节编号
	 * @return 审批环节
	 */
	public static AuditItem getObject(Long itemId) {
		if(auditItemMapper == null) {
			return null;
		}

		// 参数校验

		if(itemId == null || itemId == 0L) {
			return null;
		}

		// 查询条件
		AuditItem form = new AuditItem();
		form.setId(itemId);
		
		return auditItemMapper.getObject(form, null);
	}

	/**
	 * 插入一条审批环节
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(AuditItem data) {
		if(auditItemMapper == null) {
			return 0;
		}

		if (data.getId() == null) {

			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);

		}

		// 设置缺省值：驳回是否可以继续审批
		if(data.getContinueFlag() == null) {
			data.setContinueFlag(false);
		}
	
		return auditItemMapper.insert(data);
	}

	/**
	 * 删除审批环节
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(AuditItem where, String appendix) {
		if(auditItemMapper == null) {
			return 0;
		}

		return auditItemMapper.delete(where, appendix);
	}

	/**
	 * 查询一条审批环节，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static AuditItem getObject(AuditItem where, String appendix) {
		if(auditItemMapper == null) {
			return null;
		}

		return auditItemMapper.getObject(where, appendix);
	}

	/**
	 * 修改审批环节
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(AuditItem where, String appendix, AuditItem data) {
		if(auditItemMapper == null) {
			return 0;
		}

		return auditItemMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条审批环节，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<AuditItem> list(AuditItem where, String appendix, Pagination pagination) {
		if(auditItemMapper == null) {
			return Collections.emptyList();
		}

		return auditItemMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的审批环节数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(AuditItem where, String appendix) {
		if(auditItemMapper == null) {
			return 0;
		}

		return auditItemMapper.count(where, appendix);
	}
	
	/**
	 * 获得审批{1：并签2：会签}列表
	 * 
	 * @return 审批{1：并签2：会签}列表
	 */
	public static List<KeyValue> getAttendList() {
		return CommonMPI.getDictionaryList(AuditItem.attendFlags);
	}
}
