package cn.js.icode.config.controller;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import cn.js.icode.config.data.Parameter;
import cn.js.icode.config.service.ParameterService;
import team.bangbang.spring.parameter.EntityParam;
/**
 * 系统参数 - Controller
 *
 * @author ICode Studio
 * @version 1.0  2018-10-13
 */
@Controller
@RequestMapping("/config")
public class ParameterController {
	/**
	 * 系统参数列表
	 *
	 * @param parameter 查询条件
	 * @param pagination 分页数据
	 *
	 * @return 列表页面
	 */
	@RequestMapping("/parameterList.do")
	public ModelAndView doList(@EntityParam Parameter parameter, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("config/parameterList");
		// 构造下拉框数据
		configDropdownList(parameter, view);
		List<Parameter> parameterList = ParameterService.list(parameter, null, pagination);
		// 统计符合条件的结果记录数量
		int recordCount = ParameterService.count(parameter, null);
		pagination.setRecordCount(recordCount);
		view.addObject("parameter", parameter);
		view.addObject("parameterList", parameterList);
		view.addObject("pagination", pagination);
		return view;
	}
	/**
	 * 新增页面显示
	 *
	 * @param parameter 预设定的数据，比如在指定的分类下新增记录
     * @param request HTTP请求
	 *
	 * @return 新增页面
	 */
	@GetMapping("/parameterAdd.do")
	public ModelAndView doAdd(@EntityParam Parameter parameter, HttpServletRequest request) {
		ModelAndView view = new ModelAndView("config/parameterAdd");
		// 构造下拉框数据
		configDropdownList(parameter, view);
		// 保存预设定的数据
		view.addObject("parameter", parameter);
		
		// 保存dialogId变量
		String dialogId = request.getParameter("dialogId");
		view.addObject("dialogId", dialogId);
		
		return view;
	}
	/**
	 * 新增页面数据提交
	 *
	 * @param parameter 系统参数
     * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/parameterAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam Parameter parameter, HttpServletRequest request) {
		// 是否存在重复记录？
		if(exist(parameter)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		int result = ParameterService.insert(parameter);
		if(result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}
        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("新增系统参数");
        log.setBizData(parameter);
        request.setAttribute("log", log);
		return ResponseBase.SUCCESS;
	}
	/**
	 * 修改页面显示
	 *
	 * @param parameterId 参数编号（关键字）
     * @param request HTTP请求
	 *
	 * @return 修改页面
	 */
	@GetMapping("/parameterUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value="id") Long parameterId, HttpServletRequest request) {
		ModelAndView view = new ModelAndView("config/parameterUpdate");
		if(parameterId == null || parameterId == 0L) {
			return view;
		}
		// 查询条件
		Parameter parameter = ParameterService.getObject(parameterId);
		
		if(parameter == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + parameterId + "的系统参数");
			return common;
		}
		
		view.addObject("parameter", parameter);
		// 构造下拉框数据
		configDropdownList(parameter, view);
		
		// 保存dialogId变量
		String dialogId = request.getParameter("dialogId");
		view.addObject("dialogId", dialogId);
		
		return view;
	}
	/**
	 * 修改页面数据提交
	 *
	 * @param parameter 系统参数
     * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/parameterUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam Parameter parameter, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long parameterId = parameter.getId();
		if(parameterId == null || parameterId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 是否存在重复记录？
		if(exist(parameter)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		// 第1个参数parameter，取关键字段parameter.parameterId为条件
		// 第3个参数parameter，取parameter内关键字段以外其它属性数据
		int result = ParameterService.update(parameter, null, parameter);
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("修改系统参数");
        log.setBizData(parameter);
        request.setAttribute("log", log);
		return ResponseBase.SUCCESS;
	}
	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 *
	 * @param parameterId 参数编号（关键字）
     * @param request HTTP请求
	 *
	 * @return 删除结果
	 */
	@PostMapping(value = "/parameterDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value="id") Long parameterId, HttpServletRequest request) {
		if(parameterId == null || parameterId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
        // 获取待删除的对象，用于日志记录
        Parameter parameter = ParameterService.getObject(parameterId);
        if(parameter == null) {
            return ResponseBase.DATA_NOT_FOUND;
        }
		// 限定条件
		Parameter where = new Parameter();
		where.setId(parameterId);
		int result = ParameterService.delete(where, null);
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("删除系统参数");
        log.setBizData(parameter);
        request.setAttribute("log", log);
		return ResponseBase.SUCCESS;
	}
	/**
	 * 展示页面
	 *
	 * @param parameterId 参数编号（关键字）
     * @param request HTTP请求
	 *
	 * @return 展示页面
	 */
	@RequestMapping("/parameterView.do")
	public ModelAndView doView(@RequestParam(value="id") Long parameterId, HttpServletRequest request) {
		ModelAndView view = new ModelAndView("config/parameterView");
		if(parameterId == null || parameterId == 0L) {
			return view;
		}
		// 查询条件
		Parameter where = new Parameter();
		where.setId(parameterId);
		Parameter parameter = ParameterService.getObject(where, null);
		
		if(parameter != null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + parameterId + "的系统参数");
			return common;
		}
		
		view.addObject("parameter", parameter);
		
		// 保存dialogId变量
		String dialogId = request.getParameter("dialogId");
		view.addObject("dialogId", dialogId);
		
		return view;
	}
	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 *
	 * @param parameter 查询条件
	 * @param view 视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(Parameter parameter, ModelAndView view) {
	}
	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 *
	 * @param temp
	 *            将要修改的系统参数POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(Parameter temp) {
		// 检查修改的系统参数是否有重复记录
		Parameter form = new Parameter();
		// 关键字限定条件
		String str = (temp.getId() == null ? null : "ParameterId != " + temp.getId());
		// 其它信息限定条件
		// 参数名称
		form.setParameterName(temp.getParameterName());
		// 参数数值
		form.setParameterValue(temp.getParameterValue());
		// 所属模块
		form.setModule(temp.getModule());
		// 参数说明
		form.setRemark(temp.getRemark());
		return (ParameterService.getObject(form, str) != null);
	}
}