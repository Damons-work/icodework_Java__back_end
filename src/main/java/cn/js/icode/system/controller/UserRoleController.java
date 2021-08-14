package cn.js.icode.system.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.spring.parameter.EntityParam;
import cn.js.icode.system.data.UserRole;
import cn.js.icode.system.service.UserRoleService;

/**
 * 账户具有的角色 - Controller
 *
 * @author ICode Studio
 * @version 1.0  2018-09-18
 */
@Controller
@RequestMapping("/system")
public class UserRoleController {
	/**
	 * 账户具有的角色列表
	 *
	 * @param userRole 查询条件
	 * @param pagination 分页数据
	 *
	 * @return 列表页面
	 */
	@RequestMapping("/userRoleList.do")
	public ModelAndView doList(@EntityParam UserRole userRole, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("system/userRoleList");
		// 构造下拉框数据
		configDropdownList(userRole, view);
		List<UserRole> userRoleList = UserRoleService.list(userRole, null, pagination);
		view.addObject("userRole", userRole);
		view.addObject("userRoleList", userRoleList);
		view.addObject("pagination", pagination);
		return view;
	}
	/**
	 * 新增页面显示
	 *
	 * @param userRole 预设定的数据，比如在指定的分类下新增记录
	 *
	 * @return 新增页面
	 */
	@GetMapping("/userRoleAdd.do")
	public ModelAndView doAdd(@EntityParam UserRole userRole) {
		ModelAndView view = new ModelAndView("system/userRoleAdd");
		// 构造下拉框数据
		configDropdownList(userRole, view);
		// 保存预设定的数据
		view.addObject("userRole", userRole);
		return view;
	}
	/**
	 * 新增页面数据提交
	 *
	 * @param userRole 账户具有的角色
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/userRoleAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam UserRole userRole) {
		int result = UserRoleService.insert(userRole);
		if(result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}
		return ResponseBase.SUCCESS;
	}
	/**
	 * 修改页面显示
	 *
	 * @param id 编号（关键字）
	 *
	 * @return 修改页面
	 */
	@GetMapping("/userRoleUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value="id") Long id) {
		ModelAndView view = new ModelAndView("system/userRoleUpdate");
		if(id == null || id == 0L) {
			return view;
		}
		// 查询条件
		UserRole where = new UserRole();
		where.setId(id);
		UserRole userRole = UserRoleService.getObject(where, null);
		if(userRole != null) view.addObject("userRole", userRole);
		// 构造下拉框数据
		configDropdownList(userRole, view);
		return view;
	}
	/**
	 * 修改页面数据提交
	 *
	 * @param userRole 账户具有的角色
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/userRoleUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam UserRole userRole) {
		// 为防止更新意外，必须传入id才能更新
		Long id = userRole.getId();
		if(id == null || id == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 第1个参数userRole，取关键字段userRole.id为条件
		// 第3个参数userRole，取userRole内关键字段以外其它属性数据
		int result = UserRoleService.update(userRole, null, userRole);
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		return ResponseBase.SUCCESS;
	}
	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 *
	 * @param id 编号（关键字）
	 *
	 * @return 删除结果
	 */
	@PostMapping(value = "/userRoleDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value="id") Long id) {
		if(id == null || id == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 限定条件
		UserRole where = new UserRole();
		where.setId(id);
		int result = UserRoleService.delete(where, null);
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		return ResponseBase.SUCCESS;
	}
	/**
	 * 展示页面
	 *
	 * @param id 编号（关键字）
	 *
	 * @return 展示页面
	 */
	@RequestMapping("/userRoleView.do")
	public ModelAndView doView(@RequestParam(value="id") Long id) {
		ModelAndView view = new ModelAndView("system/userRoleView");
		if(id == null || id == 0L) {
			return view;
		}
		// 查询条件
		UserRole where = new UserRole();
		where.setId(id);
		UserRole userRole = UserRoleService.getObject(where, null);
		if(userRole == null) {
			// 使用刚刚生成的新对象，避免到页面上出错
			userRole = where;
		}
		view.addObject("userRole", userRole);
		return view;
	}
	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 *
	 * @param userRole 查询条件
	 * @param view 视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(UserRole userRole, ModelAndView view) {
	}
}