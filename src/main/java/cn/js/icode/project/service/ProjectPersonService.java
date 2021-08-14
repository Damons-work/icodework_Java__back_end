package cn.js.icode.project.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.js.icode.project.entity.ProjectPerson;
import cn.js.icode.project.mapper.ProjectPersonMapper;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.exception.BizException;

/**
 * 项目内参与人员 - Service
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
@Service
public class ProjectPersonService {
	/* 项目内参与人员（ProjectPerson）Mapper */
	@Resource
	private ProjectPersonMapper _projectPersonMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static ProjectPersonMapper projectPersonMapper = null;

	/**
	 * IOC初始化之后执行的方法
	 */
	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		projectPersonMapper = _projectPersonMapper;
	}

	/**
	 * 得到指定的项目内参与人员
	 *
	 * @param id
	 *			指定的编号
	 * @return 项目内参与人员
	 */
	public static ProjectPerson getObject(Long id) {
		if(projectPersonMapper == null) {
			return null;
		}

		// 参数校验

		if(id == null || id == 0L) {
			return null;
		}

		// 查询条件
		ProjectPerson form = new ProjectPerson();
		form.setId(id);

		ProjectPerson unt = getObject(form, null);
		if (unt == null) {
			return unt;
		}
		// 加载动态数据字典

		return unt;
	}

	/**
	 * 查询一条项目内参与人员，并转化为相应的POJO对象
	 *
	 * @param projectPerson
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static ProjectPerson getObject(ProjectPerson projectPerson, String appendix) {
		if(projectPersonMapper == null) {
			return null;
		}

		// 参数校验
		if(projectPerson == null && (appendix == null || appendix.trim().length() == 0)) {
			return null;
		}

		return projectPersonMapper.getObject(projectPerson, appendix);
	}

	/**
	 * 插入一条项目内参与人员
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(ProjectPerson data) throws BizException {
		if(projectPersonMapper == null) {
			return 0;
		}

		// 关键字段是int类型，默认为自增长，直接用Mybatis下面的配置获得新增后的关键字段值

		// 非空判断
		StringBuffer sb = new StringBuffer();

		// 检查非空值：项目编号
		if(data.getProjectId() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("projectId");
		}
	
		// 检查非空值：参与人员编号，关联project_person_base.person_id
		if(data.getPersonId() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("personId");
		}
	
		// 设置缺省值：是否是钉子标识
		if(data.getIsLeaderFlag() == null) {
			data.setIsLeaderFlag(false);
		}
	
		// 检查非空值：投入时间百分比
		if(data.getTimePercent() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("timePercent");
		}
	
		if (sb.length() > 0) {
			throw new BizException("以下字段未传入值：" + sb);
		}

		return projectPersonMapper.insert(data);
	}

	/**
	 * 删除项目内参与人员
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(ProjectPerson where, String appendix) {
		if(projectPersonMapper == null) {
			return 0;
		}

		return projectPersonMapper.delete(where, appendix);
	}

	/**
	 * 修改项目内参与人员
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(ProjectPerson where, String appendix, ProjectPerson data) {
		if(projectPersonMapper == null) {
			return 0;
		}

		return projectPersonMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条项目内参与人员，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<ProjectPerson> list(ProjectPerson where, String appendix, Pagination pagination) {
		if(projectPersonMapper == null) {
			return Collections.emptyList();
		}

		List<ProjectPerson> lst = projectPersonMapper.list(where, appendix, pagination);
		if (lst == null || lst.isEmpty()) {
			return lst;
		}
		// 加载动态数据字典

		return lst;
	}

	/**
	 * 获得符合条件的项目内参与人员数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(ProjectPerson where, String appendix) {
		if(projectPersonMapper == null) {
			return 0;
		}

		return projectPersonMapper.count(where, appendix);
	}

}
