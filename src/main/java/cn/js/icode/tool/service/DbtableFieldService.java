package cn.js.icode.tool.service;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.Pagination;
import cn.js.icode.tool.data.DbtableField;
import cn.js.icode.tool.mapper.DbtableFieldMapper;
/**
 * 数据表字段 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-10-21
 */
@Service
public final class DbtableFieldService {
	/* 数据表字段（DbtableField）Mapper */
	@Resource
	private DbtableFieldMapper _dbtableFieldMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static DbtableFieldMapper dbtableFieldMapper = null;
	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		dbtableFieldMapper = _dbtableFieldMapper;
	}
	/**
	 * 得到指定的数据表字段
	 *
	 * @param fieldId
	 *			指定的字段编号
	 * @return 数据表字段
	 */
	public static DbtableField getObject(Long fieldId) {
		if(dbtableFieldMapper == null) {
			return null;
		}
		// 参数校验
		if(fieldId == null || fieldId == 0L) {
			return null;
		}
		// 查询条件
		DbtableField form = new DbtableField();
		form.setId(fieldId);
		return dbtableFieldMapper.getObject(form, null);
	}
	/**
	 * 插入一条数据表字段
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(DbtableField data) {
		if(dbtableFieldMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		// 设置缺省值：是否主键
		if(data.getIsPk() == null) {
			data.setIsPk(false);
		}
		// 设置缺省值：是否非空
		if(data.getIsNotNull() == null) {
			data.setIsNotNull(false);
		}
		return dbtableFieldMapper.insert(data);
	}
	/**
	 * 删除数据表字段
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(DbtableField where, String appendix) {
		if(dbtableFieldMapper == null) {
			return 0;
		}
		return dbtableFieldMapper.delete(where, appendix);
	}
	/**
	 * 查询一条数据表字段，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static DbtableField getObject(DbtableField where, String appendix) {
		if(dbtableFieldMapper == null) {
			return null;
		}
		return dbtableFieldMapper.getObject(where, appendix);
	}
	/**
	 * 修改数据表字段
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(DbtableField where, String appendix, DbtableField data) {
		if(dbtableFieldMapper == null) {
			return 0;
		}
		return dbtableFieldMapper.update(where, appendix, data);
	}
	/**
	 * 查询多条数据表字段，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<DbtableField> list(DbtableField where, String appendix, Pagination pagination) {
		if(dbtableFieldMapper == null) {
			return Collections.emptyList();
		}
		return dbtableFieldMapper.list(where, appendix, pagination);
	}
	/**
	 * 获得符合条件的数据表字段数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(DbtableField where, String appendix) {
		if(dbtableFieldMapper == null) {
			return 0;
		}
		return dbtableFieldMapper.count(where, appendix);
	}
}
