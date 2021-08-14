package cn.js.icode.project.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.js.icode.project.entity.ProjectX;
import cn.js.icode.project.mapper.ProjectXMapper;
import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.exception.BizException;

/**
 * 项目信息 - Service
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
@Service
public class ProjectXService {
	/* 项目信息（ProjectX）Mapper */
	@Resource
	private ProjectXMapper _projectXMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static ProjectXMapper projectXMapper = null;

	/**
	 * IOC初始化之后执行的方法
	 */
	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		projectXMapper = _projectXMapper;
	}

	/**
	 * 得到指定的项目信息
	 *
	 * @param project_id
	 *			指定的项目编号
	 * @return 项目信息
	 */
	public static ProjectX getObject(Long project_id) {
		if(projectXMapper == null) {
			return null;
		}

		// 参数校验

		if(project_id == null || project_id == 0L) {
			return null;
		}

		// 查询条件
		ProjectX form = new ProjectX();
		form.setId(project_id);

		ProjectX unt = getObject(form, null);
		if (unt == null) {
			return unt;
		}
		// 加载动态数据字典

		return unt;
	}

	/**
	 * 查询一条项目信息，并转化为相应的POJO对象
	 *
	 * @param projectX
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static ProjectX getObject(ProjectX projectX, String appendix) {
		if(projectXMapper == null) {
			return null;
		}

		// 参数校验
		if(projectX == null && (appendix == null || appendix.trim().length() == 0)) {
			return null;
		}

		return projectXMapper.getObject(projectX, appendix);
	}

	/**
	 * 插入一条项目信息
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(ProjectX data) throws BizException {
		if(projectXMapper == null) {
			return 0;
		}

		// 关键字段是int类型，默认为自增长，直接用Mybatis下面的配置获得新增后的关键字段值

		// 非空判断
		StringBuffer sb = new StringBuffer();

		// 检查非空值：项目名称
		if(data.getProjectName() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("projectName");
		}
	
		// 检查非空值：税后金额（元）
		if(data.getAfterTax() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("afterTax");
		}
	
		// 检查非空值：状态标识{1:进行中，2:已完成，3：已删除}
		if(data.getStatusFlag() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("statusFlag");
		}
	
		// 检查非空值：钉子编号，关联project_person_base.person_id
		if(data.getLeaderId() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("leaderId");
		}
	
		if (sb.length() > 0) {
			throw new BizException("以下字段未传入值：" + sb);
		}

		return projectXMapper.insert(data);
	}

	/**
	 * 删除项目信息
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(ProjectX where, String appendix) {
		if(projectXMapper == null) {
			return 0;
		}

		return projectXMapper.delete(where, appendix);
	}

	/**
	 * 修改项目信息
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(ProjectX where, String appendix, ProjectX data) {
		if(projectXMapper == null) {
			return 0;
		}

		return projectXMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条项目信息，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<ProjectX> list(ProjectX where, String appendix, Pagination pagination) {
		if(projectXMapper == null) {
			return Collections.emptyList();
		}

		List<ProjectX> lst = projectXMapper.list(where, appendix, pagination);
		if (lst == null || lst.isEmpty()) {
			return lst;
		}
		// 加载动态数据字典

		return lst;
	}

	/**
	 * 获得符合条件的项目信息数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(ProjectX where, String appendix) {
		if(projectXMapper == null) {
			return 0;
		}

		return projectXMapper.count(where, appendix);
	}

	/**
	 * 获得状态{1:进行中，2:已完成，3：已删除}列表
	 *
	 * @return 状态{1:进行中，2:已完成，3：已删除}列表
	 */
	public static List<KeyValue> getStatusList() {
		return CommonMPI.getDictionaryList(ProjectX.statusFlags);
	}

}
