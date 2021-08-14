package cn.js.icode.system.controller;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.common.utility.TreeNode;
import team.bangbang.spring.parameter.EntityParam;
import cn.js.icode.system.data.Role;
import cn.js.icode.system.data.RoleMenu;
import cn.js.icode.system.service.MenuService;
import cn.js.icode.system.service.RoleMenuService;
import cn.js.icode.system.service.RoleService;

/**
 * 角色 - Controller
 *
 * @author ICode Studio
 * @version 1.0  2018-09-17
 */
@Controller
@RequestMapping("/system")
public class RoleController {
	/**
	 * 角色列表
	 *
	 * @param role 查询条件
	 * @param pagination 分页数据
	 *
	 * @return 列表页面
	 */
	@RequestMapping("/roleList.do")
	public ModelAndView doList(@EntityParam Role role, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("system/roleList");
		// 构造下拉框数据
		configDropdownList(role, view);
		List<Role> roleList = RoleService.list(role, "RoleName not like '_temp_%'", pagination);
		
		// 统计符合条件的结果记录数量
		int recordCount = RoleService.count(role, "RoleName not like '_temp_%'");		
		pagination.setRecordCount(recordCount);
		
		view.addObject("role", role);
		view.addObject("roleList", roleList);
		view.addObject("pagination", pagination);
		return view;
	}
	/**
	 * 新增页面显示
	 *
	 * @param role 预设定的数据，比如在指定的分类下新增记录
	 *
	 * @return 新增页面
	 */
	@GetMapping("/roleAdd.do")
	public ModelAndView doAdd(@EntityParam Role role) {
		ModelAndView view = new ModelAndView("system/roleAdd");
		// 构造下拉框数据
		configDropdownList(role, view);

		// 默认新增：权限角色
		// 类别{1：权限角色2：审核角色}
		role.setTypeFlag(1);

		// 保存预设定的数据
		view.addObject("role", role);
		return view;
	}
	/**
	 * 新增页面数据提交
	 *
	 * @param role 角色
     * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/roleAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam Role role, HttpServletRequest request) {
		// 是否存在重复记录？
		if(exist(role)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		int result = RoleService.insert(role);
		if(result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}

        // 保存角色路径
        saveRoleMenus(role, request);

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("新增角色");
		log.setBizData(role);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}

    /**
     * 保存角色可以操作的菜单功能
     *
     * @param role 角色
     * @param request HTTP请求
     */
    public static void saveRoleMenus(Role role, HttpServletRequest request) {
        // 删除原有Role和Menu的关联
        RoleMenu where = new RoleMenu();
        where.setRoleId(role.getId());

        RoleMenuService.delete(where, null);

        String arr[] = request.getParameterValues("checkboxmenu");

        // 把页面勾选转化为RoleMenu对象
        List<RoleMenu> lst = toRoleMenu(arr);
        for (int i = 0; i < lst.size(); i++) {
            RoleMenu rp = lst.get(i);
            rp.setRoleId(role.getId());

            RoleMenuService.insert(rp);
        }
    }

    private static List<RoleMenu> toRoleMenu(String[] arr) {
        List<RoleMenu> lstRp = new ArrayList<RoleMenu>();
        List<String> lst = new ArrayList<String>();
        // 当父节点已经选择时，忽略子节点的选取
        for (int i = 0; arr != null && i < arr.length; i++) {
            lst.add(arr[i]);
        }
        for (int i = 0; arr != null && i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                if (i == j)
                    continue;

                // str2为str1的子项
                // 删除子项
                if (arr[j].indexOf(arr[i]) == 0)
                    lst.remove(arr[j]);
            }
        }

        // 具有全部权限的节点先构造为RoleMenu并存入lstRp，余下的保存在lst中
        for (int i = 0; i < lst.size(); i++) {
            String menucheck = lst.get(i);
            // 倒数第2位字符如果不是_，则为具有全部权限的节点
            char c = menucheck.charAt(menucheck.length() - 2);
            // 以下一种情况都表示某个权限
            // M00002_X
            // M00002_M00007_X
            // M00002_M00007_X
            // 全部权限
            if (c != '_') {
                // 把传入的Menu中父项标记去除
                String menuId = menucheck;
                int nIndex = menuId.lastIndexOf("_");
                if (nIndex > 0)
                    menuId = menuId.substring(nIndex + 1);

                RoleMenu rp = new RoleMenu();
                // 菜单项的节点编号
                rp.setMenuId(LogicUtility.parseLong(menuId, 0L));

                // 设置全部权限
                rp.setPermissionFlag(new Integer(1 << MenuService.PERMISSION_ALL));

                lstRp.add(rp);

                // 从lst中去除
                lst.remove(i--);
            }
        }

        // 处理没有赋予全部功能权限的节点
        for (int i = 0; i < lst.size(); i++) {
            String menucheck = lst.get(i);
            // 得到节点编号
            int nIndex = menucheck.lastIndexOf("_");
            String menuId = menucheck.substring(0, nIndex);
            nIndex = menuId.lastIndexOf("_");
            menuId = menuId.substring(nIndex + 1);

            // 得到该功能节点的RoleMenu
            RoleMenu rp = getRoleMenu(lstRp, LogicUtility.parseLong(menuId, 0L));
            // 得到功能权限
            menucheck = menucheck.toLowerCase();
            // 叠加权限
            Integer nTemp = rp.getPermissionFlag();
            int nPermissionFlag = (nTemp == null) ? 0 : nTemp.intValue();
            if (menucheck.endsWith("_1")) {
                nPermissionFlag += (1 << MenuService.PERMISSION_VIEW);
            } else if (menucheck.endsWith("_2")) {
                nPermissionFlag += (1 << MenuService.PERMISSION_ADD);
            } else if (menucheck.endsWith("_3")) {
                nPermissionFlag += (1 << MenuService.PERMISSION_UPDATE);
            } else if (menucheck.endsWith("_4")) {
                nPermissionFlag += (1 << MenuService.PERMISSION_DELETE);
            } else if (menucheck.endsWith("_5")) {
                nPermissionFlag += (1 << MenuService.PERMISSION_IMPORT);
            } else if (menucheck.endsWith("_6")) {
                nPermissionFlag += (1 << MenuService.PERMISSION_EXPORT);
            } else if (menucheck.endsWith("_7")) {
                nPermissionFlag += (1 << MenuService.PERMISSION_AUDIT);
            } else if (menucheck.endsWith("_8")) {
                nPermissionFlag += (1 << MenuService.PERMISSION_OTHER);
            }

            rp.setPermissionFlag(new Integer(nPermissionFlag));
        }

        return lstRp;
    }

    /**
     * @param lstRp
     *            RoleMenu列表
     * @param menuId
     *            路径编号
     * @return 从RoleMenu列表中找到路径编号为指定值的RoleMenu，如果找不到，
     *         则在RoleMenu列表中新建该路径编号的RoleMenu
     */
    private static RoleMenu getRoleMenu(List<RoleMenu> lstRp, long menuId) {
        for (int i = 0; i < lstRp.size(); i++) {
            RoleMenu rp = lstRp.get(i);
            if (rp.getMenuId().equals(menuId)) {
                return rp;
            }
        }

        // 没有找到
        RoleMenu rp = new RoleMenu();
        rp.setMenuId(menuId);
        lstRp.add(rp);
        return rp;
    }

    /**
	 * 修改页面显示
	 *
	 * @param roleId 等级编号
	 *
	 * @return 修改页面
	 */
	@GetMapping("/roleUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value="id") Long roleId) {
		ModelAndView view = new ModelAndView("system/roleUpdate");
		if(roleId == null || roleId == 0L) {
			return view;
		}
		
		// 查询条件
		Role where = new Role();
		where.setId(roleId);
		Role role = RoleService.getObject(where, null);
		if(role == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + roleId + "的角色信息");
			return common;
		}
		
		view.addObject("role", role);
		// 构造下拉框数据
		configDropdownList(role, view);

		return view;
	}
	/**
	 * 修改页面数据提交
	 *
	 * @param role 角色
     * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/roleUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam Role role, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long roleId = role.getId();
		if(roleId == null || roleId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
        
		// 是否存在重复记录？
		if(exist(role)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		// 第1个参数role，取关键字段role.roleId为条件
		// 第3个参数role，取role内关键字段以外其它属性数据
		int result = RoleService.update(role, null, role);
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 保存角色路径
        saveRoleMenus(role, request);

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("修改角色");
		log.setBizData(role);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}
	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 *
	 * @param roleId 等级编号
	 * @param request HTTP请求
	 *
	 * @return 删除结果
	 */
	@PostMapping(value = "/roleDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value="id") Long roleId, HttpServletRequest request) {
		if(roleId == null || roleId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
        if(roleId == 1001L || roleId == 1002L) {
        	return ResponseBase.DATA_PROTECTED;
        }

		// 获取待删除的对象，用于日志记录
		Role role = RoleService.getObject(roleId);
		if(role == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 限定条件
		Role where = new Role();
		where.setId(roleId);
		int result = RoleService.delete(where, null);
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 删除菜单关联
        RoleMenuService.delete(new RoleMenu(), "RoleId = " + roleId);

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("删除角色");
		log.setBizData(role);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}
	/**
	 * 展示页面
	 *
	 * @param roleId 关键字段的值
	 *
	 * @return 展示页面
	 */
	@RequestMapping("/roleView.do")
	public ModelAndView doView(@RequestParam(value="id") Long roleId) {
		ModelAndView view = new ModelAndView("system/roleView");
		if(roleId == null || roleId == 0L) {
			return view;
		}
		// 查询条件
		Role where = new Role();
		where.setId(roleId);
		Role role = RoleService.getObject(where, null);
		
		if(role == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + roleId + "的角色信息");
			return common;
		}

		// 获得菜单模块信息
		configDropdownList(role, view);

		view.addObject("role", role);
		return view;
	}
	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 *
	 * @param role 查询条件
	 * @param view 视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(Role role, ModelAndView view) {
		// 获得系统中所有的菜单项
		TreeNode root = MenuService.getSystemTree();
		if (root != null && root.hasSon()) {
			// 模块列表
			TreeNode[] modules = root.getSons();
			if(modules != null) {
				view.addObject("modules", modules);
			}
		}

		// 类别{1：权限角色2：审核角色}列表
		List<KeyValue> typeList = RoleService.getTypeList();
		view.addObject("typeList", typeList);
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 *
	 * @param temp
	 *            将要修改的角色POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(Role temp) {
		// 检查修改的角色是否有重复记录
		Role form = new Role();
		// 关键字限定条件
		String str = (temp.getId() == null ? null : "RoleId != " + temp.getId());
		// 其它信息限定条件
		// 角色名称
		form.setRoleName(temp.getRoleName());
		// 类别标识{1：权限角色2：审核角色}
		form.setTypeFlag(temp.getTypeFlag());

		return (RoleService.getObject(form, str) != null);
	}
}