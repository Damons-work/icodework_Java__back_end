package cn.js.icode.system.service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.Pagination;
import cn.js.icode.system.data.WarningConfig;
import cn.js.icode.system.mapper.WarningConfigMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 告警配置 - Service
 *
 * @author ICode Studio
 * @version 1.0  2019-11-08
 */
@Service
public final class WarningConfigService {
	/* 告警配置（WarningConfig）Mapper */
	@Resource
	private WarningConfigMapper _warningConfigMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static WarningConfigMapper warningConfigMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		warningConfigMapper = _warningConfigMapper;
	}

	/**
	 * 得到指定的告警配置
	 * 
	 * @param id
	 *			指定的编号
	 * @return 告警配置
	 */
	public static WarningConfig getObject(Long id) {
		if(warningConfigMapper == null) {
			return null;
		}

		// 参数校验

		if(id == null || id == 0L) {
			return null;
		}

		// 查询条件
		WarningConfig form = new WarningConfig();
		form.setId(id);
		
		return getObject(form, null);
	}

	/**
	 * 插入一条告警配置
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(WarningConfig data) {
		if(warningConfigMapper == null) {
			return 0;
		}

		if (data.getId() == null) {

			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);

		}

		return warningConfigMapper.insert(data);
	}

	/**
	 * 删除告警配置
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(WarningConfig where, String appendix) {
		if(warningConfigMapper == null) {
			return 0;
		}

		return warningConfigMapper.delete(where, appendix);
	}

	/**
	 * 查询一条告警配置，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static WarningConfig getObject(WarningConfig where, String appendix) {
		if(warningConfigMapper == null) {
			return null;
		}

		return warningConfigMapper.getObject(where, appendix);
	}

	/**
	 * 修改告警配置
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(WarningConfig where, String appendix, WarningConfig data) {
		if(warningConfigMapper == null) {
			return 0;
		}

		return warningConfigMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条告警配置，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<WarningConfig> list(WarningConfig where, String appendix, Pagination pagination) {
		if(warningConfigMapper == null) {
			return Collections.emptyList();
		}

		return warningConfigMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的告警配置数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(WarningConfig where, String appendix) {
		if(warningConfigMapper == null) {
			return 0;
		}

		return warningConfigMapper.count(where, appendix);
	}
    
}
