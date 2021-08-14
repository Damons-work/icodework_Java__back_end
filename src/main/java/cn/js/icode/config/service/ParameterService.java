package cn.js.icode.config.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.Pagination;
import cn.js.icode.config.data.Parameter;
import cn.js.icode.config.mapper.ParameterMapper;

/**
 * 系统参数 - Service
 *
 * @author ICode Studio
 * @version 1.0 2018-10-13
 */
@Service
public final class ParameterService {
	/* 系统参数（Parameter）Mapper */
	@Resource
	private ParameterMapper _parameterMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static ParameterMapper parameterMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		parameterMapper = _parameterMapper;
	}

	/**
	 * 得到指定的系统参数
	 *
	 * @param parameterId 指定的参数编号
	 * @return 系统参数
	 */
	public static Parameter getObject(Long parameterId) {
		if (parameterMapper == null) {
			return null;
		}
		// 参数校验
		if (parameterId == null || parameterId == 0L) {
			return null;
		}
		// 查询条件
		Parameter form = new Parameter();
		form.setId(parameterId);
		return parameterMapper.getObject(form, null);
	}

	/**
	 * 插入一条系统参数
	 *
	 * @param data 插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Parameter data) {
		if (parameterMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		return parameterMapper.insert(data);
	}

	/**
	 * 删除系统参数
	 *
	 * @param where    删除条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Parameter where, String appendix) {
		if (parameterMapper == null) {
			return 0;
		}
		return parameterMapper.delete(where, appendix);
	}

	/**
	 * 查询一条系统参数，并转化为相应的POJO对象
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Parameter getObject(Parameter where, String appendix) {
		if (parameterMapper == null) {
			return null;
		}
		return parameterMapper.getObject(where, appendix);
	}

	/**
	 * 修改系统参数
	 *
	 * @param where    更新条件，不能为null
	 * @param appendix 附加限定条件
	 * @param data     更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Parameter where, String appendix, Parameter data) {
		if (parameterMapper == null) {
			return 0;
		}
		return parameterMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条系统参数，并转化为相应的POJO对象列表
	 *
	 * @param where      更新条件，不能为null
	 * @param appendix   附加限定条件
	 * @param pagination 分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Parameter> list(Parameter where, String appendix, Pagination pagination) {
		if (parameterMapper == null) {
			return Collections.emptyList();
		}
		return parameterMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的系统参数数量
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Parameter where, String appendix) {
		if (parameterMapper == null) {
			return 0;
		}
		return parameterMapper.count(where, appendix);
	}
	
	/**
	 * 得到参数值
	 * 
	 * @param module 所属模块
	 * @param parameterName 参数名称
	 * 
	 * @return 参数值
	 */
	public static String getParameter(String module, String parameterName) {
		// 查询条件
		Parameter where = new Parameter();
		where.setModule(module);
		where.setParameterName(parameterName);
		
		// 查询数据库
		Parameter p = getObject(where, null);
		
		return (p != null ? p.getParameterValue() : null);
	}
}
