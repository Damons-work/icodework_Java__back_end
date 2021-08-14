package cn.js.icode.system.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.js.icode.system.data.TreeSelectData;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.common.utility.TreeNode;
import team.bangbang.common.utility.TreeUtil;
import team.bangbang.spring.parameter.EntityParam;
import cn.js.icode.system.data.Menu;
import cn.js.icode.system.data.MenuFunction;
import cn.js.icode.system.data.RoleMenu;
import cn.js.icode.system.service.MenuFunctionService;
import cn.js.icode.system.service.MenuService;
import cn.js.icode.system.service.RoleMenuService;

/**
 * 菜单项 - Controller
 *
 * @author ICode Studio
 * @version 1.0 2018-09-14
 */
@Controller
@RequestMapping("/system")
public class MenuController {
	/**
	 * 菜单项列表
	 *
	 * @param menu 查询条件，其中menu.id表示当前选中的节点编号
	 *
	 * @return 列表页面
	 */
	@RequestMapping("/menuTree.do")
	public ModelAndView doTree(@EntityParam Menu menu) {
		ModelAndView view = new ModelAndView("system/menuTree");

		// 获得系统中所有的菜单项
		TreeNode root = MenuService.getSystemTree();
		if (root == null || !root.hasSon()) {
			return view;
		}

		// 模块列表
		TreeNode[] modules = root.getSons();
		if (modules == null) {
			return view;
		}

		view.addObject("modules", modules);
		
		Long menuId = menu.getId();

		// 默认选择第一个节点
		if (menuId == null && modules.length > 0) {
			menu.setId((Long) modules[0].getId());
		} else {
			// 传入的menuId是否存在？
			boolean exist = false;
			for(int i = 0; modules != null && i < modules.length; i++) {
				if(modules[i] != null && modules[i].getId() != null && modules[i].getId().equals(menuId)) {
					exist = true;
					break;
				}
			}
			
			if(!exist) {
				menu.setId((Long) modules[0].getId());
			}
		}
		
		view.addObject("menu", menu);

		return view;
	}

	/**
	 * 新增页面显示
	 *
	 * @param menu 预设定的数据，比如在指定的分类下新增记录
	 *
	 * @return 新增页面
	 */
	@GetMapping("/menuAdd.do")
	public ModelAndView doAdd(@EntityParam Menu menu) {
		ModelAndView view = new ModelAndView("system/menuAdd");
		// 构造下拉框数据
		configDropdownList(menu, view);

		// 查询所有路径列表
		Menu pojo = new Menu();
		// 所有节点都可以作为父节点
		List<Menu> parentMenu = MenuService.list(pojo, null, null);
		// 将parentMenu中节点梳理为树
		List<TreeNode> nodeList = MenuService.toTreeNodeList(parentMenu);
		TreeNode[] trees = TreeUtil.toTrees(nodeList);
		parentMenu.clear();
		// 添加缩进，再次转变为Menu元素，存储到列表中。
		fill(trees, "", parentMenu);

		// 保存父菜单的备选列表
		view.addObject("parentMenu", parentMenu);

		// 是否指定了parentId ？
		Long pId = menu.getParentId();
		menu.setParent(MenuService.getObject(pId));

		// 保存预设定的数据
		view.addObject("menu", menu);

		return view;
	}

	/**
	 * 新增页面数据提交
	 *
	 * @param menu    菜单项
	 * @param request HTTP请求
	 *
	 * @return 结果消息页面
	 */
	@PostMapping(value = "/menuAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam Menu menu, HttpServletRequest request) {
		// 是否存在重复记录？
		if (exist(menu)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		// 插入菜单项
		int result = MenuService.insert(menu);

		// 插入菜单路径
		String[] typeFlags = request.getParameterValues("menuFunction.typeFlag");
		String[] functionUrls = request.getParameterValues("menuFunction.functionUrl");

		if (typeFlags != null && functionUrls != null && typeFlags.length == functionUrls.length) {
			for (int i = 0; i < typeFlags.length; i++) {
				MenuFunction temp = new MenuFunction();
				temp.setMenuId(menu.getId());
				temp.setFunctionUrl(functionUrls[i]);
				int tf = LogicUtility.parseInt(typeFlags[i], 0);
				temp.setTypeFlag(tf);

				MenuFunctionService.insert(temp);
			}
		}

		if (result < 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}
		// 刷新系统的菜单项树
		MenuService.refreshSystemTree();

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("新增菜单");
		log.setBizData(menu);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}

	/**
	 * 修改页面显示
	 *
	 * @param menuId 等级编号
	 *
	 * @return 修改页面
	 */
	@GetMapping("/menuUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value = "id") Long menuId) {
		ModelAndView view = new ModelAndView("system/menuUpdate");
		if (menuId == null || menuId == 0L) {
			return view;
		}

		// 查询条件
		Menu where = new Menu();
		where.setId(menuId);
		Menu menu = MenuService.getObject(where, null);
		if (menu == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + menuId + "的菜单信息");
			return common;
		}
		
		view.addObject("menu", menu);
		// 构造下拉框数据
		configDropdownList(menu, view);

		// 限定条件
		// 指定菜单的编号及所有子节点的编号 除外
		String ids = MenuService.getSelfAndSonIds(menuId);
		List<Menu> parentMenu = MenuService.list(new Menu(), "MenuId not in (" + ids + ")", null);

		// 将parentMenu中节点梳理为树
		List<TreeNode> nodeList = MenuService.toTreeNodeList(parentMenu);
		TreeNode[] trees = TreeUtil.toTrees(nodeList);
		parentMenu.clear();
		// 添加缩进，再次转变为Menu元素，存储到列表中。
		fill(trees, "", parentMenu);

		// 保存父菜单的备选列表
		view.addObject("parentMenu", parentMenu);

		// 查询菜单功能
		MenuFunction mfWhere = new MenuFunction();
		mfWhere.setMenuId(menuId);
		List<MenuFunction> mfList = MenuFunctionService.list(mfWhere, null, null);
		if (mfList != null) {
			view.addObject("mfList", mfList);
		}

		return view;
	}

	/**
	 * 修改页面数据提交
	 *
	 * @param menu    菜单项
	 * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/menuUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam Menu menu, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long menuId = menu.getId();
		if (menuId == null || menuId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		if (menuId == 10000L) {
			return ResponseBase.DATA_PROTECTED;
		}

		// 是否存在重复记录？
		if (exist(menu)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		if (menu.getParentId() != null && menu.getParentId() == 10000L) {
			return ResponseBase.DATA_PROTECTED;
		}

		// 第1个参数menu，取关键字段menu.menuId为条件
		// 第3个参数menu，取menu内关键字段以外其它属性数据
		int result = MenuService.update(menu, null, menu);
		if (result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 删除原先的菜单功能
		MenuFunction where = new MenuFunction();
		where.setMenuId(menuId);
		MenuFunctionService.delete(where, null);

		// 插入菜单路径
		String[] typeFlags = request.getParameterValues("menuFunction.typeFlag");
		String[] functionUrls = request.getParameterValues("menuFunction.functionUrl");

		if (typeFlags != null && functionUrls != null && typeFlags.length == functionUrls.length) {
			for (int i = 0; i < typeFlags.length; i++) {
				MenuFunction temp = new MenuFunction();
				temp.setMenuId(menu.getId());
				temp.setFunctionUrl(functionUrls[i]);

				int tf = LogicUtility.parseInt(typeFlags[i], 0);
				temp.setTypeFlag(tf);

				MenuFunctionService.insert(temp);
			}
		}

		// 刷新系统的菜单项树
		MenuService.refreshSystemTree();

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("修改菜单");
		log.setBizData(menu);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}

	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 *
	 * @param menuId  等级编号
	 * @param request HTTP请求
	 *
	 * @return 删除结果
	 */
	@PostMapping(value = "/menuDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value = "id") Long menuId, HttpServletRequest request) {
		if (menuId == null || menuId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		if (menuId == 10000L) {
			return ResponseBase.DATA_PROTECTED;
		}

		// 获取待删除的对象，用于日志记录
		Menu menu = MenuService.getObject(menuId);
		if (menu == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		if (menu.getParentId() != null && menu.getParentId() == 10000L) {
			return ResponseBase.DATA_PROTECTED;
		}

		// 获得指定菜单的编号及所有子节点的编号
		String ids = MenuService.getSelfAndSonIds(menuId);

		// 限定条件
		Menu where = new Menu();
		// 1. 删除菜单主信息
		int result = MenuService.delete(where, "MenuId in (" + ids + ")");
		if (result < 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 2. 删除菜单功能
		MenuFunctionService.delete(new MenuFunction(), "MenuId in (" + ids + ")");

		// 3. 删除角色关联
		RoleMenuService.delete(new RoleMenu(), "MenuId in (" + ids + ")");

		// 刷新系统的菜单项树
		MenuService.refreshSystemTree();

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("删除菜单");
		log.setBizData(menu);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}

    /**
     * 菜单项列表树形结构
     *
     * @param
     *
     * @return 列表页面
     */
    @RequestMapping("/menuSelect.do")
    public ModelAndView doSelect() {
        ModelAndView view = new ModelAndView("system/menuSelect");

        // 得到数据库中完整的路径树，包含菜单项本身数据
        TreeNode temp = MenuService.getSystemTree();
        List<TreeSelectData> treeSelectDataList = new ArrayList<TreeSelectData>();
        if (temp != null && temp.getSons() != null) {
            for (TreeNode treeNode : temp.getSons()) {
                treeSelectDataList = getSelectList(treeSelectDataList, treeNode);
            }

        }
        view.addObject("treeSelectDataList", JSON.toJSONString(treeSelectDataList));
        return view;
    }

    private List<TreeSelectData> getSelectList(List<TreeSelectData> resultList, TreeNode treeNode) {
        TreeSelectData treeSelectData = new TreeSelectData();
        treeSelectData.setId(treeNode.getId().toString());
        treeSelectData.setName(treeNode.getName());
        // 子节点
        List<TreeSelectData> childrenNodeList = new ArrayList<>();
        if (treeNode.getSons() != null) {
            for (TreeNode treeNodeItem : treeNode.getSons()) {
                getSelectList(childrenNodeList, treeNodeItem);
            }
        }

        treeSelectData.setChildren(childrenNodeList);
        resultList.add(treeSelectData);

        return resultList;
    }

	/**
	 * 展示页面
	 *
	 * @param menuId 关键字段的值
	 *
	 * @return 展示页面
	 */
	@RequestMapping("/menuView.do")
	public ModelAndView doView(@RequestParam(value = "id") Long menuId) {
		ModelAndView view = new ModelAndView("system/menuView");
		if (menuId == null || menuId == 0L) {
			return view;
		}

		// 查询条件
		Menu where = new Menu();
		where.setId(menuId);
		Menu menu = MenuService.getObject(where, null);
		
		if (menu == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + menuId + "的菜单信息");
			return common;
		}
		
		view.addObject("menu", menu);
		return view;
	}

	/**
	 * 图标选择页面
	 *
	 * @return 图标选择页面页面
	 */
	@GetMapping("/iconSelect.do")
	public ModelAndView selectIcon() {
		ModelAndView view = new ModelAndView("system/iconSelect");
		// 图标列表
		List<String> iconList = MenuService.getIconClasses();

		view.addObject("iconList", iconList);

		return view;
	}

	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 *
	 * @param menu 查询条件
	 * @param view 视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(Menu menu, ModelAndView view) {
		// 权限类型{查看, 新增, 修改, 删除, 导入, 导出, 审核, 其它}列表
		List<KeyValue> typeList = MenuService.getTypeList();
		view.addObject("typeList", typeList);
	}

	/**
	 * 为树节点添加缩进，将每个节点转变为Menu元素，存储到列表中。
	 *
	 * @param trees      树
	 * @param indent     缩进字符串
	 * @param parentList 列表容器
	 */
	private void fill(TreeNode[] trees, String indent, List<Menu> parentList) {
		if (trees == null || trees.length == 0) {
			return;
		}

		// 遍历
		// 缩进长度
		int nLen = indent.length();
		for (int i = 0; i < trees.length; i++) {
			Menu p = new Menu();
			p.setId((Long) trees[i].getId());
			// 带有缩进的菜单项名称
			String indentTemp = (nLen > 0) ? indent + "|-" : indent;
			// 菜单项名成
			String name = (String) trees[i].getName();
			if (nLen == 0) {
				name = "【" + name + "】";
			}

			p.setMenuName(indentTemp + name);
			parentList.add(p);

			if (!trees[i].hasSon()) {
				continue;
			}

			TreeNode[] sons = trees[i].getSons();
			// 子模块的缩进字符串
			// 当前节点有兄弟节点，且不是第一级节点，则使用"| "
			String indent2 = (i < trees.length - 1 && indent.length() > 0) ? indent + "|　" : indent + "　";
			fill(sons, indent2, parentList);
		}
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 *
	 * @param temp 将要修改的菜单项POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(Menu temp) {
		// 检查修改的菜单项是否有重复记录
		Menu form = new Menu();
		// 关键字限定条件
		String str = (temp.getId() == null ? null : "MenuId != " + temp.getId());
		// 其它信息限定条件
		// 父菜单项编号，关联system_user_base.MenuId
		if (temp.getParentId() == null) {
			if (str == null)
				str = "ParentId is null";
			else
				str += " and ParentId is null";
		} else {
			// 参与限定
			form.setParentId(temp.getParentId());
		}
		// 菜单名称
		form.setMenuName(temp.getMenuName());

		return (MenuService.getObject(form, str) != null);
	}
}