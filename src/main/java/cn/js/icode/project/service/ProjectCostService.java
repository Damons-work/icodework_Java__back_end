package cn.js.icode.project.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.js.icode.project.entity.ProjectCost;
import cn.js.icode.project.mapper.ProjectCostMapper;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.exception.BizException;

/**
 * 项目成本 - Service
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
@Service
public class ProjectCostService {
	/* 项目成本（ProjectCost）Mapper */
	@Resource
	private ProjectCostMapper _projectCostMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static ProjectCostMapper projectCostMapper = null;

	/**
	 * IOC初始化之后执行的方法
	 */
	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		projectCostMapper = _projectCostMapper;
	}

	/**
	 * 得到指定的项目成本
	 *
	 * @param cost_id
	 *			指定的成本编号
	 * @return 项目成本
	 */
	public static ProjectCost getObject(Long cost_id) {
		if(projectCostMapper == null) {
			return null;
		}

		// 参数校验

		if(cost_id == null || cost_id == 0L) {
			return null;
		}

		// 查询条件
		ProjectCost form = new ProjectCost();
		form.setId(cost_id);

		ProjectCost unt = getObject(form, null);
		if (unt == null) {
			return unt;
		}
		// 加载动态数据字典

		return unt;
	}

	/**
	 * 查询一条项目成本，并转化为相应的POJO对象
	 *
	 * @param projectCost
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static ProjectCost getObject(ProjectCost projectCost, String appendix) {
		if(projectCostMapper == null) {
			return null;
		}

		// 参数校验
		if(projectCost == null && (appendix == null || appendix.trim().length() == 0)) {
			return null;
		}

		return projectCostMapper.getObject(projectCost, appendix);
	}

	/**
	 * 插入一条项目成本
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(ProjectCost data) throws BizException {
		if(projectCostMapper == null) {
			return 0;
		}

		// 关键字段是int类型，默认为自增长，直接用Mybatis下面的配置获得新增后的关键字段值

		// 非空判断
		StringBuffer sb = new StringBuffer();

		// 检查非空值：项目编号，关联project_project_base.project_id
		if(data.getProjectId() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("projectId");
		}
	
		// 检查非空值：所属年月，yyyyMM格式，如：202104
		if(data.getYearMonth() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("yearMonth");
		}
	
		// 检查非空值：人员编号，关联project_person_base.person_id
		if(data.getPersonId() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("personId");
		}
	
		// 检查非空值：月底或者当前日期（是几号？）
		if(data.getToDay() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("toDay");
		}
	
		// 检查非空值：产生的成本（元）
		if(data.getPersonCost() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("personCost");
		}
	
		if (sb.length() > 0) {
			throw new BizException("以下字段未传入值：" + sb);
		}

		return projectCostMapper.insert(data);
	}

	/**
	 * 删除项目成本
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(ProjectCost where, String appendix) {
		if(projectCostMapper == null) {
			return 0;
		}

		return projectCostMapper.delete(where, appendix);
	}

	/**
	 * 修改项目成本
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(ProjectCost where, String appendix, ProjectCost data) {
		if(projectCostMapper == null) {
			return 0;
		}

		return projectCostMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条项目成本，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<ProjectCost> list(ProjectCost where, String appendix, Pagination pagination) {
		if(projectCostMapper == null) {
			return Collections.emptyList();
		}

		List<ProjectCost> lst = projectCostMapper.list(where, appendix, pagination);
		if (lst == null || lst.isEmpty()) {
			return lst;
		}
		// 加载动态数据字典

		return lst;
	}

	/**
	 * 获得符合条件的项目成本数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(ProjectCost where, String appendix) {
		if(projectCostMapper == null) {
			return 0;
		}

		return projectCostMapper.count(where, appendix);
	}

}
