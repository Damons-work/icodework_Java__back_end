package cn.js.icode.tool.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.js.icode.system.data.User;
import cn.js.icode.system.service.UserService;
import cn.js.icode.tool.data.Dbtable;
import cn.js.icode.tool.data.Project;
import cn.js.icode.tool.service.DbtableService;
import cn.js.icode.tool.service.ProjectService;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.spring.parameter.EntityParam;
import team.bangbang.spring.parameter.SessionUser;
import team.bangbang.sso.data.Account;

/**
 * 工程 - Controller
 *
 * @author ICode Studio
 * @version 1.0 2018-10-08
 */
@Controller
@RequestMapping("/tool")
public class ProjectController {
	/**
	 * 工程列表
	 *
	 * @param user    当前操作账户
	 * @param project    查询条件
	 * @param pagination 分页数据
	 *
	 * @return 列表页面
	 */
	@RequestMapping("/projectList.do")
	public ModelAndView doList(@SessionUser Account user, @EntityParam Project project, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("tool/projectList");
		// 构造下拉框数据
		configDropdownList(project, view);
		
		// 1001: 系统管理员
		if(!user.hasRoleCode("1001")) {
			Long userId = LogicUtility.parseLong(user.getId(), 0L);
			project.setCreatorId(userId);
		}
		
		List<Project> projectList = ProjectService.list(project, null, pagination);
		
		// 记录数量
		int count = ProjectService.count(project, null);
		pagination.setRecordCount(count);		
		
		// 装配创建人信息
		for(int i = 0; projectList != null && i < projectList.size(); i++) {
			Project p = projectList.get(i);
			Long userId = p.getCreatorId();
			User creator = UserService.getObject(userId);
			
			p.setCreator(creator);
		}
		
		view.addObject("project", project);
		view.addObject("projectList", projectList);
		view.addObject("pagination", pagination);
		
		return view;
	}

	/**
	 * 新增页面显示
	 *
	 * @param project 预设定的数据，比如在指定的分类下新增记录
	 *
	 * @return 新增页面
	 */
	@GetMapping("/projectAdd.do")
	public ModelAndView doAdd(@EntityParam Project project) {
		ModelAndView view = new ModelAndView("tool/projectAdd");
		// 构造下拉框数据
		configDropdownList(project, view);
		// 保存预设定的数据
		view.addObject("project", project);
		return view;
	}

	/**
	 * 新增页面数据提交
	 *
	 * @param user    当前操作账户
	 * @param project 工程
	 * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/projectAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@SessionUser Account user, @EntityParam Project project, HttpServletRequest request) {
		// 是否存在重复记录？
		if (exist(project)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		// 创建人编号
		Long userId = LogicUtility.parseLong(user.getId(), 0L);
		project.setCreatorId(userId);
		// 创建时间
		project.setCreateTime(new Date());

		int result = ProjectService.insert(project);
		if (result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}
		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("新增工程");
		log.setBizData(project);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}

	/**
	 * 修改页面显示
	 *
	 * @param projectId 工程编号（关键字）
	 *
	 * @return 修改页面
	 */
	@GetMapping("/projectUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value = "id") Long projectId) {
		ModelAndView view = new ModelAndView("tool/projectUpdate");
		if (projectId == null || projectId == 0L) {
			return view;
		}
		// 查询条件
		Project where = new Project();
		where.setId(projectId);
		Project project = ProjectService.getObject(where, null);

		if(project == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + projectId + "的工程");
			return common;
		}
		
		view.addObject("project", project);
		
		// 装配创建人信息
		Long userId = project.getCreatorId();
		User creator = UserService.getObject(userId);
		
		project.setCreator(creator);
		
		// 构造下拉框数据
		configDropdownList(project, view);
		return view;
	}

	/**
	 * 修改页面数据提交
	 *
	 * @param user    当前操作账户
	 * @param project 工程
	 * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/projectUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@SessionUser Account user, @EntityParam Project project,
			HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long projectId = project.getId();
		if (projectId == null || projectId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		

		Project unt = ProjectService.getObject(project.getId());		
		if(unt == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		
		// R001: 系统管理员
		if(!user.hasRoleCode("1001") && !unt.getCreatorId().equals(user.getId())) {
			return ResponseBase.REQUEST_PERMISSION_DENIED;
		}
		
		// 是否存在重复记录？
		if (exist(project)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		// 第1个参数project，取关键字段project.projectId为条件
		// 第3个参数project，取project内关键字段以外其它属性数据
		int result = ProjectService.update(project, null, project);
		if (result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("修改工程");
		log.setBizData(project);
		request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}

	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 *
	 * @param user      当前操作账户
	 * @param projectId 工程编号（关键字）
	 * @param request   HTTP请求
	 *
	 * @return 删除结果
	 */
	@PostMapping(value = "/projectDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@SessionUser Account user, @RequestParam(value = "id") Long projectId,
			HttpServletRequest request) {
		if (projectId == null || projectId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 如果下面存在数据库表，则不能删除
		Dbtable dbWhere = new Dbtable();
		dbWhere.setProjectId(projectId);
		
		int count = DbtableService.count(dbWhere, null);
		if(count > 0) {
			ResponseBase rb = ResponseBase.DATA_STATUS_ERROR;
			rb.setMessage("当前工程下存在 " + count + " 张数据库表，不可删除！<br/>确实需要删除此工程，请先删除数据库表！");
			return rb;
		}
		
		// 获取待删除的对象，用于日志记录
		Project project = ProjectService.getObject(projectId);
		if (project == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		
		// R001: 系统管理员
		if(!user.hasRoleCode("1001") && !project.getCreatorId().equals(user.getId())) {
			return ResponseBase.AUTHENTICATION_TOKEN_INVALID;
		}
		
		// 限定条件
		Project where = new Project();
		where.setId(projectId);
		int result = ProjectService.delete(where, null);
		if (result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("删除工程");
		log.setBizData(project);
		request.setAttribute("log", log);
		return ResponseBase.SUCCESS;
	}

	/**
	 * 展示页面
	 *
	 * @param projectId 工程编号（关键字）
	 *
	 * @return 展示页面
	 */
	@RequestMapping("/projectView.do")
	public ModelAndView doView(@RequestParam(value = "id") Long projectId) {
		ModelAndView view = new ModelAndView("tool/projectView");
		if (projectId == null || projectId == 0L) {
			return view;
		}
		// 查询条件
		Project where = new Project();
		where.setId(projectId);
		Project project = ProjectService.getObject(where, null);
		
		if (project == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + projectId + "的工程");
			return common;
		}
		
		// 装配创建人信息
		Long userId = project.getCreatorId();
		User creator = UserService.getObject(userId);
		
		project.setCreator(creator);
		
		view.addObject("project", project);
		return view;
	}

	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 *
	 * @param project 查询条件
	 * @param view    视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(Project project, ModelAndView view) {
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 *
	 * @param temp 将要修改的工程POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(Project temp) {
		// 检查修改的工程是否有重复记录
		Project form = new Project();
		// 关键字限定条件
		String str = (temp.getId() == null ? null : "ProjectId != " + temp.getId());
		// 其它信息限定条件
		// 工程名称
		form.setProjectName(temp.getProjectName());
		// 创建人编号
		form.setCreatorId(temp.getCreatorId());

		return (ProjectService.getObject(form, str) != null);
	}
}