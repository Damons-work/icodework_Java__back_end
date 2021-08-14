package cn.js.icode.api;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.utility.TreeNode;
import team.bangbang.common.utility.TreeUtil;
import cn.js.icode.system.data.RoleMenu;
import cn.js.icode.system.service.MenuService;
import cn.js.icode.system.service.RoleMenuService;
import springfox.documentation.annotations.ApiIgnore;

/**
 * API服务 - 菜单
 *
 * @author ICode Studio
 * @version 1.0 2018-10-5
 */
@ApiIgnore
@RestController
@RequestMapping("/api/menu")
public class MenuRestful {
	/**
	 * 获取模块节点，该模块节点是一个树节点，可以含有子节点
	 *
	 * @param moduleId 模块节点编号
	 *
	 * @return 指定模块节点
	 */
	@RequestMapping("/getModule")
	public ResponseBase getModule(Long moduleId) {
		// 获得系统中所有的菜单项
		TreeNode root = MenuService.getSystemTree();
		if (root == null || !root.hasSon()) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 模块列表
		TreeNode[] modules = root.getSons();
		if (modules == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		for (TreeNode tn : modules) {
			if (tn.getId().equals(moduleId)) {
				DataResponse<TreeNode> rb = new DataResponse<TreeNode>();
				rb.setData(tn);
				return rb;
			}
		}

		return ResponseBase.DATA_NOT_FOUND;
	}

	/**
	 * 获取指定角色在所有模块下的权限
	 *
	 * @param roleId   指定角色编号，如果是null，表示此时在处理新增
	 * @param moduleId 指定模块编号，不可以为null
	 *
	 * @return 指定角色在所有模块下的权限
	 */
	@RequestMapping(value = "/getMenuStringForRole")
	public ResponseBase getMenuStringForRole(Long roleId, Long moduleId) {
		// 取得所有菜单路径
		TreeNode tree = MenuService.getSystemTree();
		if (tree == null || !tree.hasSon()) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 找到模块
		TreeNode module = TreeUtil.findNode(tree, moduleId);
		if (module == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		DataResponse<String> dto = new DataResponse<String>();
		if (roleId == null || roleId == 0) {
			// 新增
			String result = RoleMenuService.getMenuStringForRoleAdd(module);
			dto.setData(result);
			return dto;
		}

		// 取得拥有的所有路径编号
		RoleMenu rp = new RoleMenu();
		rp.setRoleId(roleId);
		List<RoleMenu> lstRoleMenu = RoleMenuService.list(rp, null, null);
		String result = RoleMenuService.getMenuStringForRoleUpdate(module, lstRoleMenu);
		dto.setData(result);

		return dto;
	}
}