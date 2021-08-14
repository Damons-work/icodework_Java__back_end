package cn.js.icode.tool.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.Pagination;
import cn.js.icode.tool.data.Project;
import cn.js.icode.tool.mapper.ProjectMapper;

/**
 * 工程 - Service
 *
 * @author ICode Studio
 * @version 1.0 2018-10-08
 */
@Service
public final class ProjectService {
	/* 工程（Project）Mapper */
	@Resource
	private ProjectMapper _projectMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static ProjectMapper projectMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		projectMapper = _projectMapper;
	}

	/**
	 * 得到指定的工程
	 *
	 * @param projectId 指定的工程编号
	 * @return 工程
	 */
	public static Project getObject(Long projectId) {
		if (projectMapper == null) {
			return null;
		}
		// 参数校验
		if (projectId == null || projectId == 0L) {
			return null;
		}
		// 查询条件
		Project form = new Project();
		form.setId(projectId);
		return projectMapper.getObject(form, null);
	}

	/**
	 * 插入一条工程
	 *
	 * @param data 插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Project data) {
		if (projectMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		return projectMapper.insert(data);
	}

	/**
	 * 删除工程
	 *
	 * @param where    删除条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Project where, String appendix) {
		if (projectMapper == null) {
			return 0;
		}
		return projectMapper.delete(where, appendix);
	}

	/**
	 * 查询一条工程，并转化为相应的POJO对象
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Project getObject(Project where, String appendix) {
		if (projectMapper == null) {
			return null;
		}
		return projectMapper.getObject(where, appendix);
	}

	/**
	 * 修改工程
	 *
	 * @param where    更新条件，不能为null
	 * @param appendix 附加限定条件
	 * @param data     更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Project where, String appendix, Project data) {
		if (projectMapper == null) {
			return 0;
		}
		return projectMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条工程，并转化为相应的POJO对象列表
	 *
	 * @param where      更新条件，不能为null
	 * @param appendix   附加限定条件
	 * @param pagination 分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Project> list(Project where, String appendix, Pagination pagination) {
		if (projectMapper == null) {
			return Collections.emptyList();
		}
		return projectMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的工程数量
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Project where, String appendix) {
		if (projectMapper == null) {
			return 0;
		}
		return projectMapper.count(where, appendix);
	}
}
