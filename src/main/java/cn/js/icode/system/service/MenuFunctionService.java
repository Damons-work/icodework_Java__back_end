package cn.js.icode.system.service;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import cn.js.icode.system.data.MenuFunction;
import cn.js.icode.system.mapper.MenuFunctionMapper;
/**
 * 菜单功能 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-09-17
 */
@Service
public final class MenuFunctionService {
	/* 菜单功能（MenuFunction）Mapper */
	@Resource
	private MenuFunctionMapper _menuFunctionMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static MenuFunctionMapper menuFunctionMapper = null;
	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		menuFunctionMapper = _menuFunctionMapper;
	}
	/*
	 * 固定数据字典 类型{1:查看2:新增3:修改4:删除5:导入6:导出7:审核8:扩展A9:扩展B}
	 */
	private static final String[] typeFlags = { "查看", "新增", "修改", "删除", "导入", "导出", "审核", "扩展A", "扩展B" };
	/**
	 * 得到指定的菜单功能
	 *
	 * @param id
	 *			指定的编号
	 * @return 菜单功能
	 */
	public static MenuFunction getObject(Long id) {
		if(menuFunctionMapper == null) {
			return null;
		}
		// 参数校验
		if(id == null || id == 0L) {
			return null;
		}
		// 查询条件
		MenuFunction form = new MenuFunction();
		form.setId(id);
		return menuFunctionMapper.getObject(form, null);
	}
	/**
	 * 获得类型{1:查看2:新增3:修改4:删除5:导入6:导出7:审核8:扩展A9:扩展B}列表
	 *
	 * @return 类型{1:查看2:新增3:修改4:删除5:导入6:导出7:审核8:扩展A9:扩展B}列表
	 */
	public static List<KeyValue> getTypeList() {
		return CommonMPI.getDictionaryList(typeFlags);
	}
	/**
	 * 获得类型{1:查看2:新增3:修改4:删除5:导入6:导出7:审核8:扩展A9:扩展B}名称
	 *
	 * @return 类型{1:查看2:新增3:修改4:删除5:导入6:导出7:审核8:扩展A9:扩展B}名称
	 */
	public static String getTypeName(Integer typeFlag) {
		return CommonMPI.getDictionaryName(typeFlags, typeFlag);
	}
	/**
	 * 插入一条菜单功能
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(MenuFunction data) {
		if(menuFunctionMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		return menuFunctionMapper.insert(data);
	}
	/**
	 * 删除菜单功能
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(MenuFunction where, String appendix) {
		if(menuFunctionMapper == null) {
			return 0;
		}
		return menuFunctionMapper.delete(where, appendix);
	}
	/**
	 * 查询一条菜单功能，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static MenuFunction getObject(MenuFunction where, String appendix) {
		if(menuFunctionMapper == null) {
			return null;
		}
		return menuFunctionMapper.getObject(where, appendix);
	}
	/**
	 * 修改菜单功能
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(MenuFunction where, String appendix, MenuFunction data) {
		if(menuFunctionMapper == null) {
			return 0;
		}
		return menuFunctionMapper.update(where, appendix, data);
	}
	/**
	 * 查询多条菜单功能，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<MenuFunction> list(MenuFunction where, String appendix, Pagination pagination) {
		if(menuFunctionMapper == null) {
			return Collections.emptyList();
		}
		return menuFunctionMapper.list(where, appendix, pagination);
	}
	/**
	 * 获得符合条件的菜单功能数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(MenuFunction where, String appendix) {
		if(menuFunctionMapper == null) {
			return 0;
		}
		return menuFunctionMapper.count(where, appendix);
	}
}
