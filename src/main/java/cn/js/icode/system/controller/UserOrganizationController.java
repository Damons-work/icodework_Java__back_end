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
import cn.js.icode.system.data.UserOrganization;
import cn.js.icode.system.service.UserOrganizationService;

/**
 * 账户管理的组织 - Controller
 *
 * @author ICode Studio
 * @version 1.0  2018-09-18
 */
@Controller
@RequestMapping("/system")
public class UserOrganizationController {
	/**
	 * 账户管理的组织列表
	 *
	 * @param userOrganization 查询条件
	 * @param pagination 分页数据
	 *
	 * @return 列表页面
	 */
	@RequestMapping("/userOrganizationList.do")
	public ModelAndView doList(@EntityParam UserOrganization userOrganization, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("system/userOrganizationList");
		// 构造下拉框数据
		configDropdownList(userOrganization, view);
		List<UserOrganization> userOrganizationList = UserOrganizationService.list(userOrganization, null, pagination);
		view.addObject("userOrganization", userOrganization);
		view.addObject("userOrganizationList", userOrganizationList);
		view.addObject("pagination", pagination);
		return view;
	}

	/**
	 * 新增页面显示
	 *
	 * @param userOrganization 预设定的数据，比如在指定的分类下新增记录
	 *
	 * @return 新增页面
	 */
	@GetMapping("/userOrganizationAdd.do")
	public ModelAndView doAdd(@EntityParam UserOrganization userOrganization) {
		ModelAndView view = new ModelAndView("system/userOrganizationAdd");
		// 构造下拉框数据
		configDropdownList(userOrganization, view);
		// 保存预设定的数据
		view.addObject("userOrganization", userOrganization);
		return view;
	}
	/**
	 * 新增页面数据提交
	 *
	 * @param userOrganization 账户管理的组织
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/userOrganizationAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam UserOrganization userOrganization) {
		int result = UserOrganizationService.insert(userOrganization);
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
	@GetMapping("/userOrganizationUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value="id") Long id) {
		ModelAndView view = new ModelAndView("system/userOrganizationUpdate");
		if(id == null || id == 0L) {
			return view;
		}
		// 查询条件
		UserOrganization where = new UserOrganization();
		where.setId(id);
		UserOrganization userOrganization = UserOrganizationService.getObject(where, null);
		if(userOrganization != null) view.addObject("userOrganization", userOrganization);
		// 构造下拉框数据
		configDropdownList(userOrganization, view);
		return view;
	}
	/**
	 * 修改页面数据提交
	 *
	 * @param userOrganization 账户管理的组织
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/userOrganizationUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam UserOrganization userOrganization) {
		// 为防止更新意外，必须传入id才能更新
		Long id = userOrganization.getId();
		if(id == null || id == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 第1个参数userOrganization，取关键字段userOrganization.id为条件
		// 第3个参数userOrganization，取userOrganization内关键字段以外其它属性数据
		int result = UserOrganizationService.update(userOrganization, null, userOrganization);
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
	@PostMapping(value = "/userOrganizationDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value="id") Long id) {
		if(id == null || id == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 限定条件
		UserOrganization where = new UserOrganization();
		where.setId(id);
		int result = UserOrganizationService.delete(where, null);
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
	@RequestMapping("/userOrganizationView.do")
	public ModelAndView doView(@RequestParam(value="id") Long id) {
		ModelAndView view = new ModelAndView("system/userOrganizationView");
		if(id == null || id == 0L) {
			return view;
		}
		// 查询条件
		UserOrganization where = new UserOrganization();
		where.setId(id);
		UserOrganization userOrganization = UserOrganizationService.getObject(where, null);
		if(userOrganization == null) {
			// 使用刚刚生成的新对象，避免到页面上出错
			userOrganization = where;
		}
		view.addObject("userOrganization", userOrganization);
		return view;
	}
	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 *
	 * @param userOrganization 查询条件
	 * @param view 视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(UserOrganization userOrganization, ModelAndView view) {
	}
}