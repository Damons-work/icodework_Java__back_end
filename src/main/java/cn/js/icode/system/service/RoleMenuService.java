package cn.js.icode.system.service;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.config.Constants;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.common.utility.TreeNode;
import cn.js.icode.system.data.Menu;
import cn.js.icode.system.data.RoleMenu;
import cn.js.icode.system.mapper.RoleMenuMapper;
/**
 * 角色管理的菜单项 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-09-17
 */
@Service
public final class RoleMenuService {
	/* 角色管理的菜单项（RoleMenu）Mapper */
	@Resource
	private RoleMenuMapper _roleMenuMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static RoleMenuMapper roleMenuMapper = null;
	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		roleMenuMapper = _roleMenuMapper;
	}

	/**
	 * 得到指定的角色管理的菜单项
	 *
	 * @param id
	 *			指定的编号
	 * @return 角色管理的菜单项
	 */
	public static RoleMenu getObject(Long id) {
		if(roleMenuMapper == null) {
			return null;
		}
		// 参数校验
		if(id == null || id == 0L) {
			return null;
		}
		// 查询条件
		RoleMenu form = new RoleMenu();
		form.setId(id);
		return roleMenuMapper.getObject(form, null);
	}
	/**
	 * 插入一条角色管理的菜单项
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(RoleMenu data) {
		if(roleMenuMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		return roleMenuMapper.insert(data);
	}
	/**
	 * 删除角色管理的菜单项
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(RoleMenu where, String appendix) {
		if(roleMenuMapper == null) {
			return 0;
		}
		return roleMenuMapper.delete(where, appendix);
	}

	/**
	 * 查询一条角色管理的菜单项，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static RoleMenu getObject(RoleMenu where, String appendix) {
		if(roleMenuMapper == null) {
			return null;
		}
		return roleMenuMapper.getObject(where, appendix);
	}

	/**
	 * 修改角色管理的菜单项
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(RoleMenu where, String appendix, RoleMenu data) {
		if(roleMenuMapper == null) {
			return 0;
		}
		return roleMenuMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条角色管理的菜单项，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<RoleMenu> list(RoleMenu where, String appendix, Pagination pagination) {
		if(roleMenuMapper == null) {
			return Collections.emptyList();
		}
		return roleMenuMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的角色管理的菜单项数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(RoleMenu where, String appendix) {
		if(roleMenuMapper == null) {
			return 0;
		}

		return roleMenuMapper.count(where, appendix);
	}

	/**
	 * RoleAdd的权限节点选择checkbox群组
	 *
	 * @param module
	 *            一个模块下的菜单项树，包含菜单项本身数据
	 * @return HTML字符串，该字符串表现了在角色新增时的功能权限节点checkbox选择群组
	 */
	public static String getMenuStringForRoleAdd(TreeNode module) {
		return getMenuStringForRoleUpdate(module, null);
	}

	/**
	 * RoleUpdate的权限节点选择checkbox群组
	 *
	 * @param module
	 *            一个模块下的菜单项树，包含菜单项本身数据
	 * @param lstOwn
	 *            里面的元素类型为RoleMenu
	 * @return HTML字符串，该字符串表现了在角色更新时的功能权限节点checkbox选择群组
	 */
	public static String getMenuStringForRoleUpdate(TreeNode module,
													List<RoleMenu> lstOwn) {
		StringBuffer buff = new StringBuffer();

		// 层级
		int nLevel = 0;
		buff.append("<table width=\"100%\" style=\"margin-top:10px;");
		buff.append("margin-bottom:10px;\" border=\"0\">");
		buff.append(Constants.LINE_SEPARATOR);

		// 设置节点层级
		module.setAttribute("level_flag", new Integer(nLevel));
		// 每个节点组成表格Row块
		// 第 4 个参数为null，表示没有父节点权限
		writeModule(buff, module, lstOwn, null);

		buff.append("</table>").append(Constants.LINE_SEPARATOR);

		return buff.toString();
	}

	/**
	 * 一个节点形成一个权限选择的表格行
	 *
	 * @param buff
	 *            HTML字符串容器
	 * @param treeNode
	 *            一个节点，包含菜单项本身数据
	 * @param lstOwn
	 *            所有节点的权限集合，可以根据节点到本集合中获取相应的权限
	 * @param rpParent
	 *            父节点的权限
	 */
	private static void writeModule(StringBuffer buff, TreeNode treeNode,
									List<RoleMenu> lstOwn, RoleMenu rpParent) {
		if (treeNode == null)
			return;
		// 模块名称
		String moduleName = treeNode.getName();
		// 图标文件
		String icon = (String) treeNode.getAttribute(MenuService.ICON_IMAGE);

		// 所有父节点+当前节点的Id串：ParentId1_ParentId2_ParentId3..._Id
		String id_chain = (String) treeNode.getAttribute("id_chain");
		if (id_chain == null) {
			// 无父节点
			id_chain = String.valueOf(treeNode.getId());
			treeNode.setAttribute("id_chain", id_chain);
		}

		// 第一列写节点名称
		buff.append("<tr><td style=\"vertical-align: top;\">").append(
				Constants.LINE_SEPARATOR);
		// 层级
		Integer nLevel = (Integer) treeNode.getAttribute("level_flag");
		int n = (nLevel == null) ? 0 : nLevel.intValue();
		String space = LogicUtility.duplicateString("　　", n);
		buff.append(space);

		// 判断是否选中
		// 获得当前节点的权限
		RoleMenu rpCurrent = getRoleMenu(treeNode, lstOwn);
		// 叠加父节点的权限
		inherit(rpParent, rpCurrent);
		buff.append("<input type=\"checkbox\" name=\"checkboxmenu\"");
		if (rpCurrent.getCan(0)) {
			buff.append(" checked ");
		}

		buff.append(" onclick=\"doCheck(this);\" value=\"");
		buff.append(id_chain).append("\" lay-ignore/>");

		buff.append("<i class=\"layui-icon ").append(icon).append(
				"\"></i>");
		if (n == 0) {
			// 模块
			buff.append("【").append(moduleName).append("】").append(
					Constants.LINE_SEPARATOR);
		} else {
			// 菜单
			buff.append(moduleName).append(Constants.LINE_SEPARATOR);
		}

		buff.append("</td>").append(Constants.LINE_SEPARATOR);

		// 第二列写该模块的权限
		buff.append("<td width=\"320\" style=\"vertical-align: top;\">")
				.append(Constants.LINE_SEPARATOR);

		buff.append(getNodePermissionHTML(treeNode, rpParent, rpCurrent));

		buff.append("</td></tr>").append(Constants.LINE_SEPARATOR);
		buff.append("<tr><td colspan=\"2\" height=\"9\"></td></tr>").append(
				Constants.LINE_SEPARATOR);

		// 写子节点
		if (treeNode.hasSon()) {
			TreeNode[] sons = treeNode.getSons();
			for (int i = 0; i < sons.length; i++) {
				sons[i].setAttribute("level_flag", new Integer(n + 1));
				writeModule(buff, sons[i], lstOwn, rpCurrent);
			}
		}
	}

	/**
	 * 获得菜单项节点上的权限设置checkbox组，由父节点的权限与当前节点的权限叠加得到
	 *
	 * @param treeNode
	 *            菜单项节点，包含菜单项本身数据
	 * @param rpParent
	 *            父节点的权限
	 * @param rpCurrent
	 *            当前节点的权限
	 * @return 权限设置checkbox组
	 */
	private static String getNodePermissionHTML(TreeNode treeNode,
												RoleMenu rpParent, RoleMenu rpCurrent) {
		// 所有父节点+当前节点的Id串：ParentId1_ParentId2_ParentId3..._Id
		String id_chain = (String) treeNode.getAttribute("id_chain");
		if (id_chain == null) {
			// 无父节点
			id_chain = (String) treeNode.getId();
			treeNode.setAttribute("id_chain", id_chain);
		}

		// 如果不是叶子节点，则不需要权限设置checkbox组
		if (treeNode.hasSon()) {
			// 设置子节点的id_chain
			for (TreeNode tn : treeNode.getSons()) {
				tn.setAttribute("id_chain", id_chain + "_" + tn.getId());
			}
			return "&nbsp;";
		}

		// 将父节点的权限叠加到当前节点上
		inherit(rpParent, rpCurrent);

		// 所有备选的权限类型
		List<KeyValue> kvList = MenuService.getTypeList();
		// 当前菜单节点包含的权限类型
		Menu m = (Menu)treeNode.getAttribute(MenuService.MENU_DATA);
		Collection<Integer> flags = (m == null ? null : m.getTypeFlags());

		// 是否选了全部权限类型
		boolean blAll = rpCurrent.getCan(0);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; flags != null && kvList != null && i < kvList.size(); i++) {
			KeyValue kv = kvList.get(i);

			Integer n = (Integer) kv.getKey();

			// 当前菜单节点未包含这样的权限类型
			if (!flags.contains(n))
				continue;

			String name = (String) kv.getValue();

			String s = "<input type=\"checkbox\" name=\"checkboxmenu\" value=\""
					+ id_chain
					+ "_" + n + "\" onClick=\"doCheck(this);\""
					+ (blAll || rpCurrent.getCan(n.intValue()) ? " checked" : "")
					+ " lay-ignore>\r\n" + "      " + name;
			sb.append(s).append("\r\n");
		}

		if (sb.length() > 0) {
			String s = "<input type=\"checkbox\" name=\"checkboxmenu\" value=\""
					+ id_chain
					+ "_\" onClick=\"doCheck(this);\""
					+ (rpCurrent.getCan(0) ? " checked" : "")
					+ " lay-ignore>\r\n"
					+ "      全部\r\n";
			sb.append(s).append("\r\n");
		}

		return sb.toString();
	}

	/**
	 * 将父节点的权限叠加到当前节点上
	 *
	 * @param rpParent
	 *            父节点权限
	 * @param rpCurrent
	 *            当前节点权限
	 */
	private static void inherit(RoleMenu rpParent, RoleMenu rpCurrent) {
		if (rpParent == null || rpParent.getPermissionFlag() == null || rpCurrent == null) {
			return;
		}

		// 将叠加后的权限存储到当前节点的权限中
		int pFlag = rpParent.getPermissionFlag().intValue();
		Integer sFlag = rpCurrent.getPermissionFlag();
		int n2 = (sFlag == null ? 0 : sFlag.intValue());

		n2 |= pFlag;
		rpCurrent.setPermissionFlag(new Integer(n2));
	}

	/**
	 * @param node
	 *            指定功能节点
	 * @param lstPrivilege
	 *            元素为RoleMenu类型
	 * @return 当前的角色在指定功能节点上的角色路径设置
	 */
	private static RoleMenu getRoleMenu(TreeNode node,
										List<RoleMenu> lstPrivilege) {
		if (node == null || lstPrivilege == null)
			return new RoleMenu();
		Object obj = node.getId();
		for (int i = 0; i < lstPrivilege.size(); i++) {
			if (lstPrivilege.get(i).getMenuId().equals(obj))
				return lstPrivilege.get(i);
		}

		return new RoleMenu();
	}
}
