package cn.js.icode.tool.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.config.Config;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import cn.js.icode.tool.data.Dbtable;
import cn.js.icode.tool.data.DbtableField;
import cn.js.icode.tool.mapper.DbtableMapper;

/**
 * 数据表 - Service
 *
 * @author ICode Studio
 * @version 1.0 2018-10-21
 */
@Service
public final class DbtableService {
	/* 数据表（Dbtable）Mapper */
	@Resource
	private DbtableMapper _dbtableMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static DbtableMapper dbtableMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		dbtableMapper = _dbtableMapper;
	}

	/**
	 * 得到指定的数据表
	 *
	 * @param dbtableId 指定的表编号
	 * @return 数据表
	 */
	public static Dbtable getObject(Long dbtableId) {
		if (dbtableMapper == null) {
			return null;
		}
		// 参数校验
		if (dbtableId == null || dbtableId == 0L) {
			return null;
		}
		// 查询条件
		Dbtable form = new Dbtable();
		form.setId(dbtableId);
		return dbtableMapper.getObject(form, null);
	}

	/**
	 * 插入一条数据表
	 *
	 * @param data 插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Dbtable data) {
		if (dbtableMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		return dbtableMapper.insert(data);
	}

	/**
	 * 删除数据表
	 *
	 * @param where    删除条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Dbtable where, String appendix) {
		if (dbtableMapper == null) {
			return 0;
		}
		return dbtableMapper.delete(where, appendix);
	}

	/**
	 * 查询一条数据表，并转化为相应的POJO对象
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Dbtable getObject(Dbtable where, String appendix) {
		if (dbtableMapper == null) {
			return null;
		}
		return dbtableMapper.getObject(where, appendix);
	}

	/**
	 * 修改数据表
	 *
	 * @param where    更新条件，不能为null
	 * @param appendix 附加限定条件
	 * @param data     更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Dbtable where, String appendix, Dbtable data) {
		if (dbtableMapper == null) {
			return 0;
		}
		return dbtableMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条数据表，并转化为相应的POJO对象列表
	 *
	 * @param where      更新条件，不能为null
	 * @param appendix   附加限定条件
	 * @param pagination 分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Dbtable> list(Dbtable where, String appendix, Pagination pagination) {
		if (dbtableMapper == null) {
			return Collections.emptyList();
		}
		return dbtableMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的数据表数量
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Dbtable where, String appendix) {
		if (dbtableMapper == null) {
			return 0;
		}
		return dbtableMapper.count(where, appendix);
	}

	/* 字段类型列表 */
	private static String[] types = { "string", "integer", "double", "date", "text", "boolean" };

	/**
	 * 获得字段类型列表("string", "integer", "double", "date", "text", "boolean")
	 * 
	 * @return 字段类型列表("string", "integer", "double", "date", "text", "boolean")
	 */
	public static List<KeyValue> getTypeList() {
		// 字段类型列表
		List<KeyValue> typeList = new ArrayList<KeyValue>();

		for (String str : types) {
			KeyValue kv = new KeyValue(str, str);
			typeList.add(kv);
		}

		return typeList;
	}

	/**
	 * 获得模板列表，即 {code.generator.template.directory}配置目录下子目录列表
	 * 
	 * @return 模板列表，即 {code.generator.template.directory}配置目录下子目录列表
	 */
	public static String[] getTemplates() {
		// {code.generator.template.directory}配置目录下子目录
		String dir = Config.getProperty("code.generator.template.directory");
		if (dir == null || dir.trim().length() == 0) {
			return null;
		}

		File f = new File(dir);
		if (f.exists() && f.isDirectory()) {
			// 获得该目录下子目录
			String[] subDirs = f.list();

			// 排序
			Arrays.sort(subDirs);

			return subDirs;
		}

		return null;
	}

	/**
	 * 获得数据库表的评估基本工作量(人.天)，仅仅限于CRUD操作，其它操作可以类比此工作量，乘以相应的系数。
	 * 
	 * @param table 数据库表，包括数据库字段列表
	 * 
	 * @return 自动评估的基本工作量(人.天)
	 */
	public static double getWorkload(Dbtable table) {
		if (table == null || table.getFieldList() == null || table.getFieldList().size() == 0) {
			return 0.0;
		}
		
		int count = 0;
		List<DbtableField> dfList = table.getFieldList();		
		for(int i = 0; dfList != null && i < dfList.size(); i++) {
			DbtableField df = dfList.get(i);
			
			if(df.getFieldType().equals("date")) {
				// 日期字段，1个字段当做2个
				count += 3;
			} else if(df.isFixDict()) {
				// 固定数据字典，1个字段当做3个
				count += 5;
			} else if(df.isDynamicDict()){
				// 动态数据字典，1个字段当做5个
				count += 8;				
			} else if(df.getFieldType().equals("text")) {
				// 此类字段一般需要富文本编辑器	
				// text字段，1个字段当做10个
				count += 15;
			} else {
				count++;
			}
		}

		double dbl = Math.round(Math.sqrt(1.2 * count) - 2.5) * 0.5;
		
		return dbl < 0.5 ? 0.25 : dbl;
	}
}
