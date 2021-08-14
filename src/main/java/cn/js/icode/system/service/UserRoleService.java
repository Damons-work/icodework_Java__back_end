package cn.js.icode.system.service;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.js.icode.system.data.Role;
import cn.js.icode.system.data.User;
import cn.js.icode.system.data.UserRole;
import cn.js.icode.system.mapper.UserRoleMapper;
import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.exception.BizException;
import team.bangbang.common.sql.DbUtil;
/**
 * 账户具有的角色 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-09-18
 */
@Service
public final class UserRoleService {
	/* 账户具有的角色（UserRole）Mapper */
	@Resource
	private UserRoleMapper _userRoleMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static UserRoleMapper userRoleMapper = null;

    @PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		userRoleMapper = _userRoleMapper;
	}
	/**
	 * 得到指定的账户具有的角色
	 *
	 * @param id
	 *			指定的编号
	 * @return 账户具有的角色
	 */
	public static UserRole getObject(Long id) {
		if(userRoleMapper == null) {
			return null;
		}
		// 参数校验
		if(id == null || id == 0L) {
			return null;
		}
		// 查询条件
		UserRole form = new UserRole();
		form.setId(id);
		return userRoleMapper.getObject(form, null);
	}
	/**
	 * 插入一条账户具有的角色
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(UserRole data) {
		if(userRoleMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		int n = userRoleMapper.insert(data);
		
		// 刷新一个角色下的操作账户数量
		refreshUserCountInRole(data.getRoleId());
		
		return n;
	}
	/**
	 * 删除账户具有的角色
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(UserRole where, String appendix) {
		if(userRoleMapper == null) {
			return 0;
		}
		
		// 获得需要删除的关系列表
		List<UserRole> urList = list(where, appendix, null);
		if(urList == null || urList.isEmpty()) {
			return 0;
		}
		
		int n = userRoleMapper.delete(where, appendix);
		
		// 刷新一个角色下的操作账户数量
		for(int i = 0; urList != null && i < urList.size(); i++) {
			UserRole ur = urList.get(i);
			refreshUserCountInRole(ur.getRoleId());
		}
		
		return n;
	}
	/**
	 * 查询一条账户具有的角色，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static UserRole getObject(UserRole where, String appendix) {
		if(userRoleMapper == null) {
			return null;
		}
		return userRoleMapper.getObject(where, appendix);
	}
	/**
	 * 修改账户具有的角色
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(UserRole where, String appendix, UserRole data) {
		if(userRoleMapper == null) {
			return 0;
		}
		return userRoleMapper.update(where, appendix, data);
	}
	/**
	 * 查询多条账户具有的角色，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<UserRole> list(UserRole where, String appendix, Pagination pagination) {
		if(userRoleMapper == null) {
			return Collections.emptyList();
		}
		return userRoleMapper.list(where, appendix, pagination);
	}
	/**
	 * 获得符合条件的账户具有的角色数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(UserRole where, String appendix) {
		if(userRoleMapper == null) {
			return 0;
		}
		return userRoleMapper.count(where, appendix);
	}
	
	/**
	 * 刷新一个角色下的操作账户数量
	 * 
	 * @param roleId 角色编号
	 * @return 操作账户数量
	 */
	private static int refreshUserCountInRole(Long roleId) {
		UserRole ur = new UserRole();
		ur.setRoleId(roleId);
		
		// 获得该角色的数量
		int count = count(ur, null);
		
		// 修改该角色的数量
		Role r = new Role();
		r.setId(roleId);
		r.setUserCount(count);
		
		RoleService.update(r, null, r);
		
		return count;
	}

	/**
	 * UserUpdate的角色选择checkbox群组
	 *
	 * @param lstAllRole
	 *            所有角色的列表，元素为Role
	 * @param lstOwn
	 *            当前拥有的角色，类型为Role
	 * @return HTML字符串，该字符串表现了在用户更新时的角色checkbox选择群组
	 */
	public static String getRoleStringForUserUpdate(List<Role> lstAllRole,
													List<Role> lstOwn) {
		StringBuffer buff = new StringBuffer();
		buff.append("\t\t\t  <table width=\"100%\" border=\"0\">\r\n");
		int i = 0;
		for (; i < lstAllRole.size(); i++) {
			Role role = lstAllRole.get(i);
			boolean blExist = existRole(role, lstOwn);
			if (i % 4 == 0)
				buff.append("\t\t\t\t<tr>\r\n");
			buff.append("\t\t\t\t  <td width=\"25%\">"
					//  title="写作" lay-skin="primary"
					+ "<input type=\"checkbox\" name=\"checkboxrole\" value=\""
					+ role.getId() + "\"" + (blExist ? " checked" : "") + " title=\""
					+ role.getRoleName() + "\" lay-skin=\"primary\"></td>\r\n");
			if (i % 4 == 3)
				buff.append("\t\t\t\t</tr>\r\n");
		}

		if (i % 4 != 0) {
			for (; i % 4 != 0; i++) {
				buff.append("\t\t\t\t  <td>&nbsp;</td>\r\n");
			}
			buff.append("\t\t\t\t</tr>\r\n");
		}

		buff.append("\t\t\t  </table>\r\n");
		return buff.toString();
	}

	/**
	 * UserAdd的角色选择checkbox群组
	 *
	 * @param lstAllRole
	 *            所有角色的列表，元素为Role
	 * @return HTML字符串，该字符串表现了在用户新增时的角色checkbox选择群组
	 */
	public static String getRoleStringForUserAdd(List<Role> lstAllRole) {
		return getRoleStringForUserUpdate(lstAllRole, null);
	}

	/**
	 * 得到指定账户具有的角色列表
	 *
	 * @param userId 指定用户的编号
	 * @return 指定账户具有的角色列表
	 */
	public static List<Role> getRoleList(Long userId) {
		if(userId == null || userId == 0L) {
			return Collections.emptyList();
		}

		// 查询账户角色关系表
		UserRole urWhere = new UserRole();
		urWhere.setUserId(userId);

		List<UserRole> urList = UserRoleService.list(urWhere, null, null);
		if(urList == null || urList.size() == 0) {
			return Collections.emptyList();
		}

		// 角色编号
		StringBuffer sb = new StringBuffer();
		for(UserRole ur : urList) {
			if(sb.length() > 0) sb.append(" or ");
			sb.append("RoleId = " + ur.getRoleId());
		}
		List<Role> rList = RoleService.list(new Role(), "(" + sb + ")", null);
		return rList;
	}

	/**
	 * 检查传入的角色是否存在于一个集合列表范围内
	 *
	 * @param role
	 *            传入的角色
	 * @param lstAll
	 *            元素为Role类型
	 * @return
	 */
	private static boolean existRole(Role role, List<Role> lstAll) {
		if (role == null || lstAll == null)
			return false;

		for (int i = 0; i < lstAll.size(); i++) {
			if (role.getId().equals(lstAll.get(i).getId()))
				return true;
		}

		return false;
	}

	/**
	 * 获得指定账户的私有角色名称
	 *
	 * @param userId 指定账户的编号
	 * @return 指定账户的私有角色名称
	 */
	private static String getPrivateRoleName(Long userId) {
		return "_temp_" + userId;
	}

	/**
	 * 获得指定账户的私有角色
	 *
	 * @param userId 指定账户的编号
	 * @return 指定账户的私有角色
	 */
	public static Role getPrivateRole(Long userId) {
		// 尝试查询
		Role form = new Role();
		form.setRoleName(getPrivateRoleName(userId));
		// typeFlag 类别标识{1：权限角色2：审核角色}
		form.setTypeFlag(1);

		Role role = RoleService.getObject(form, null);

		// 之前没有设置过临时角色
		if(role != null) {
			// 检查是否有用户角色关联关系
			UserRole urWhere = new UserRole();
			urWhere.setRoleId(role.getId());
			urWhere.setUserId(userId);

			UserRole ur2 = UserRoleService.getObject(urWhere, null);
			if (ur2 == null) {
				// 不存在，重新建立关联关系
				UserRoleService.insert(urWhere);
			}

			return role;
		}

		// 不存在，新增一个私有角色
		int nRows = RoleService.insert(form);
		// 新增是否成功
		if (nRows <= 0) {
			// 设定用户直接权限的准备操作失败！
			return null;
		}

		// 建立该私有角色与用户的关联关系
		UserRole ur = new UserRole();
		ur.setUserId(userId);
		ur.setRoleId(form.getId());

		UserRoleService.insert(ur);

		return form;
	}

	/**
	 * 取得一定角色范围内指定组织直接负责人(直接管辖)列表
	 * 
	 * @param orgId 指定的组织机构编号，如果为null，则不进行组织机构的限定
	 * @param roleIds 角色范围列表
	 * 
	 * @return 一定角色范围内指定组织负责人列表
	 * @throws BizException
	 */
	public static List<User> getLeaderList(Long orgId, Object[] roleIds) {
		String appendix = null;

		// 获取组织机构下面的所有子组织编号
		if (orgId != null && orgId > 0) {
			// 负责该组织的所有人员编号列表
			String sql = "select UserId from system_user_organization "
					+ "where OrganizationId = " + orgId;

			appendix = "UserId in (" + sql + ")";
		}

		// 角色限制条件
		String appendix2 = DbUtil.getOrSQL("RoleId", roleIds);
		if (appendix2 == null || appendix2.length() == 0) {
			// 无角色匹配
			return Collections.emptyList();
		}
		
		UserRole urWhere = new UserRole();
		if(appendix != null) {
			appendix2 = "(" + appendix2 + ") and " + appendix;
		} else {
			appendix2 = "(" + appendix2 + ")";
		}
		
		List<UserRole> urList = UserRoleService.list(urWhere, appendix2, null);
		// 获得UserId
		if(urList == null || urList.size() == 0) {
			// 无操作账号匹配
			return Collections.emptyList();
		}
		
		StringBuffer sb = new StringBuffer();
		for(UserRole ur : urList) {
			Long uId = ur.getUserId();
			if(sb.length() > 0) sb.append(",");
			sb.append(uId);
		}
		
		return UserService.list(new User(), "UserId in (" + sb+ ")", null);
	}
}
