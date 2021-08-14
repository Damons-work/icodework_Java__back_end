package cn.js.icode.system.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.js.icode.basis.service.OrganizationService;
import cn.js.icode.system.data.RoleMenu;
import cn.js.icode.system.data.User;
import cn.js.icode.system.data.UserOrganization;
import cn.js.icode.system.mapper.UserMapper;
import team.bangbang.common.CommonMPI;
import team.bangbang.common.config.Config;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.exception.BizException;
import team.bangbang.common.sql.DbUtil;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.common.utility.SetUtility;
import team.bangbang.common.utility.TreeNode;
import team.bangbang.common.utility.TreeUtil;
import team.bangbang.sso.data.Account;
import team.bangbang.sso.data.DataLimit;

/**
 * 系统账户 - Service
 *
 * @author ICode Studio
 * @version 1.0 2018-09-18
 */
@Service
public final class UserService {
	/* 系统账户（User）Mapper */
	@Resource
	private UserMapper _userMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static UserMapper userMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		userMapper = _userMapper;
	}

	/**
	 * 得到指定的系统账户
	 *
	 * @param userId 指定的账户编号
	 * @return 系统账户
	 */
	public static User getObject(Long userId) {
		if (userMapper == null) {
			return null;
		}
		// 参数校验
		if (userId == null || userId == 0L) {
			return null;
		}
		// 查询条件
		User form = new User();
		form.setId(userId);
		return userMapper.getObject(form, null);
	}

	/**
	 * 插入一条系统账户
	 *
	 * @param data 插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(User data) {
		if (userMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		// 设置缺省值：有效标识
		if (data.getActiveFlag() == null) {
			data.setActiveFlag(false);
		}
		return userMapper.insert(data);
	}

	/**
	 * 删除系统账户
	 *
	 * @param where    删除条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(User where, String appendix) {
		if (userMapper == null) {
			return 0;
		}
		return userMapper.delete(where, appendix);
	}

	/**
	 * 查询一条系统账户，并转化为相应的POJO对象
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static User getObject(User where, String appendix) {
		if (userMapper == null) {
			return null;
		}
		return userMapper.getObject(where, appendix);
	}

	/**
	 * 修改系统账户
	 *
	 * @param where    更新条件，不能为null
	 * @param appendix 附加限定条件
	 * @param data     更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(User where, String appendix, User data) {
		if (userMapper == null) {
			return 0;
		}
		return userMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条系统账户，并转化为相应的POJO对象列表
	 *
	 * @param where      更新条件，不能为null
	 * @param appendix   附加限定条件
	 * @param pagination 分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<User> list(User where, String appendix, Pagination pagination) {
		if (userMapper == null) {
			return Collections.emptyList();
		}
		return userMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的系统账户数量
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(User where, String appendix) {
		if (userMapper == null) {
			return 0;
		}
		return userMapper.count(where, appendix);
	}

	/**
	 * 根据传入的用户编号串（以半角逗号间隔），查询获得对应的用户名称串（以半角逗号间隔）。
	 * 
	 * @param sqlHelper 数据库操作对象
	 * @param userIds   用户编号串（以半角逗号间隔）
	 * @return 对应的用户名称串（以半角逗号间隔）
	 */
	public static String getUserNamesByUserIds(String userIds) {
		if (userIds == null || userIds.trim().length() == 0) {
			return null;
		}

		String[] arr = userIds.split(",");
		StringBuffer sb = new StringBuffer();
		for (String id : arr) {
			Long lId = LogicUtility.parseLong(id, 0L);
			if (lId == 0L)
				continue;

			if (sb.length() > 0)
				sb.append(",");
			sb.append(lId);
		}
		if (sb.length() == 0) {
			return null;
		}

		List<User> uList = list(new User(), "UserId in (" + sb + ")", null);
		sb = new StringBuffer();
		for (int i = 0; uList != null && i < uList.size(); i++) {
			if (i > 0)
				sb.append("，");
			sb.append(uList.get(i).getUserName());
		}

		return sb.toString();
	}

	/**
	 * 获得指定用户的临时文件。
	 * 
	 * 文件路径组成：[file.attachment.directory]\Temp\[UserId].dat
	 * 
	 * @param user 指定用户，必须包含所在机构的编号和用户编号
	 * @return 临时文件，文件路径组成：[file.attachment.directory]\Temp\[UserId].dat
	 * @throws IOException IO异常
	 */
	public static String getTemporaryFile(Account user) throws IOException {
		// 检查参数
		if (user == null) {
			throw new IOException("指定的用户信息为空！");
		}

		String userId = user.getId();
		if (userId == null) {
			throw new IOException("指定的用户信息必须包含用户编号！");
		}
		// 附件文件根目录
		String root = Config.getProperty("file.attachment.directory");
		if (root == null || root.trim().length() == 0) {
			throw new IOException("在 application.properties 文件中没有配置 " + "附件文件根目录（KEY: file.attachment.directory）！");
		}

		if (!root.endsWith("/") && !root.endsWith("\\")) {
			root += File.separator;
		}

		// 临时文件目录
		String tempFolder = root + "Temp" + File.separator;
		File folder = new File(tempFolder);

		if (!folder.exists()) {
			folder.mkdirs();
		}

		return tempFolder + userId + ".dat";
	}

	/**
	 * /** 获取指定角色集合下的菜单节点编号，包括所有子节点
	 *
	 * @param roleIds 指定的角色集合
	 * @return 指定账户管辖的菜单节点编号集合，包括所有子节点
	 */
	public static Set<Long> getPermissionMenuIds(Collection<?> roleIds) {
		Set<Long> ps = new HashSet<Long>();
		if (roleIds == null || roleIds.size() == 0) {
			return ps;
		}

		// 得到这些组织可以访问的路径列表
		StringBuffer sb = new StringBuffer();
		for (Object rId : roleIds) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(rId);
		}

		// 取得拥有的这些角色可以访问的所有路径编号
		if (sb.length() > 0) {
			List<RoleMenu> rmList = RoleMenuService.list(new RoleMenu(), "RoleId in (" + sb + ")", null);

			for (int i = 0; rmList != null && i < rmList.size(); i++) {
				// 角色下无菜单项
				ps.add(rmList.get(i).getMenuId());
			}
		}

		return ps;
	}

	/**
	 * 获取指定账户管辖的组织机构节点编号，包括所有子节点
	 *
	 * @param userId 指定的账户编号
	 * @return 指定账户管辖的组织机构节点编号集合，包括所有子节点
	 */
	public static Set<Long> getPermissionOrganizationIds(Long userId) {
		Set<Long> ps = new HashSet<Long>();
		if (userId == null || userId == 0L) {
			return ps;
		}

		// 整个组织机构树
		TreeNode root = OrganizationService.getRoot(false);
		if (root == null) {
			return ps;
		}

		// 得到当前账户的所有可维护的组织机构列表
		UserOrganization uo = new UserOrganization();
		uo.setUserId(userId);

		List<UserOrganization> lstUo = UserOrganizationService.list(uo, null, null);
		if (lstUo == null || lstUo.isEmpty()) {
			return ps;
		}

		// 得到组织机构编号列表
		List<Long> lstOrgIds = new ArrayList<Long>();
		for (int i = 0; i < lstUo.size(); i++) {
			uo = lstUo.get(i);
			lstOrgIds.add(uo.getOrganizationId());
		}

		// 得到组织机构编号列表
		for (int i = 0; lstOrgIds != null && i < lstOrgIds.size(); i++) {
			// 找出当前组织的编号
			Long orgId = lstOrgIds.get(i);
			TreeNode cNode = TreeUtil.findNode(root, orgId);

			// 取所有涵盖的子组织编号
			Collection<Object> lstSonIds = TreeUtil.getSelfAndSonIds(cNode);

			if (lstSonIds != null) {
				for (Object ob : lstSonIds) {
					ps.add((Long) ob);
				}
			}
		}

		return ps;
	}

	/**
	 * 根据权限管理范围内的组织机构节点情况，构造数据查询的范围或者反范围
	 * 
	 * 该方法不使用缓存，业务系统应实现缓存机制、刷新机制
	 * 
	 * @param userId 指定的账户编号
	 * @return 数据查询的范围或者反范围
	 */
	public static DataLimit getDataLimit(Long userId) {
		// 当前所有的组织机构节点
		Set<Long> allIds = SetUtility.toType(OrganizationService.getAllIds(true), Long.class);
		// 指定账户可以操作的集合范围
		Set<Long> inIds = getPermissionOrganizationIds(userId);
		// 指定账户不可以操作的集合范围
		Set<Long> notInIds = SetUtility.remove(allIds, inIds);

		DataLimit os = new DataLimit();
		// 可以操作的范围
		os.setInScope(inIds);

		// 可以操作的范围
		os.setOutScope(notInIds);
		
		return os;
	}

	/**
	 * 取得指定组织下（含下级组织）指定角色的用户列表
	 * 
	 * @param orgId  可选，组织机构编号，如果为null，则不进行组织机构的限定
	 * @param roleId 可选，角色编号，如果为null，则不进行角色的限定
	 * @return 指定组织下（含下级组织）指定角色的用户列表
	 * @throws BizException 异常
	 */
	public static List<User> getUserList(Long orgId, Long roleId) throws BizException {
		if (userMapper == null) {
			return null;
		}

		String appendix = "ActiveFlag = 1";
		if (orgId == null) {
			// 为null，则不进行组织机构的限定
		} else {
			// 获取组织机构下面的所有子组织编号
			TreeNode root = OrganizationService.getRoot(false);
			TreeNode tn = TreeUtil.findNode(root, orgId);
			Collection<Object> sonIdList = TreeUtil.getSelfAndSonIds(tn);
			if(sonIdList == null || sonIdList.isEmpty()) {
				return Collections.emptyList();
			}
			
			appendix += " and (" + DbUtil.getOrSQL("OrganizationId", sonIdList.toArray()) + ")";
		}

		if (roleId == null) {
			// 为null，则不进行角色的限定
		} else {
			// 进行角色限定
			appendix += " and UserId in (select UserId from system_user_role where RoleId = " + roleId + ")";
		}

		return userMapper.list(new User(), appendix, null);
	}
}
