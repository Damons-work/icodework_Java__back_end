package cn.js.icode.system.service;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import cn.js.icode.system.data.Role;
import cn.js.icode.system.mapper.RoleMapper;
/**
 * 角色 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-09-17
 */
@Service
public final class RoleService {
	/* 角色（Role）Mapper */
	@Resource
	private RoleMapper _roleMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static RoleMapper roleMapper = null;

    @PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		roleMapper = _roleMapper;
	}
	/*
	 * 固定数据字典 类别{1：权限角色2：审核角色}
	 */
	private static final String[] typeFlags = { "权限角色", "审核角色" };
	/**
	 * 得到指定的角色
	 *
	 * @param roleId
	 *			指定的角色编号
	 * @return 角色
	 */
	public static Role getObject(Long roleId) {
		if(roleMapper == null) {
			return null;
		}
		// 参数校验
		if(roleId == null || roleId == 0L) {
			return null;
		}
		// 查询条件
		Role form = new Role();
		form.setId(roleId);
		return roleMapper.getObject(form, null);
	}
	/**
	 * 获得类别{1：权限角色2：审核角色}列表
	 *
	 * @return 类别{1：权限角色2：审核角色}列表
	 */
	public static List<KeyValue> getTypeList() {
		return CommonMPI.getDictionaryList(typeFlags);
	}
	/**
	 * 获得类别{1：权限角色2：审核角色}名称
	 *
	 * @return 类别{1：权限角色2：审核角色}名称
	 */
	public static String getTypeName(Integer typeFlag) {
		return CommonMPI.getDictionaryName(typeFlags, typeFlag);
	}
	/**
	 * 插入一条角色
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Role data) {
		if(roleMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		return roleMapper.insert(data);
	}
	/**
	 * 删除角色
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Role where, String appendix) {
		if(roleMapper == null) {
			return 0;
		}
		return roleMapper.delete(where, appendix);
	}
	/**
	 * 查询一条角色，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Role getObject(Role where, String appendix) {
		if(roleMapper == null) {
			return null;
		}
		return roleMapper.getObject(where, appendix);
	}
	/**
	 * 修改角色
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Role where, String appendix, Role data) {
		if(roleMapper == null) {
			return 0;
		}
		return roleMapper.update(where, appendix, data);
	}
	/**
	 * 查询多条角色，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Role> list(Role where, String appendix, Pagination pagination) {
		if(roleMapper == null) {
			return Collections.emptyList();
		}
		return roleMapper.list(where, appendix, pagination);
	}
	/**
	 * 获得符合条件的角色数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Role where, String appendix) {
		if(roleMapper == null) {
			return 0;
		}
		return roleMapper.count(where, appendix);
	}
}
