package cn.js.icode.tool.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.js.icode.system.service.UserService;
import cn.js.icode.tool.data.Dbtable;
import cn.js.icode.tool.data.DbtableField;
import cn.js.icode.tool.data.Project;
import cn.js.icode.tool.service.DbtableFieldService;
import cn.js.icode.tool.service.DbtableService;
import cn.js.icode.tool.service.GeneratorMPI;
import cn.js.icode.tool.service.ProjectService;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.spring.parameter.EntityParam;
import team.bangbang.spring.parameter.SessionUser;
import team.bangbang.sso.data.Account;

/**
 * 数据表 - Controller
 *
 * @author ICode Studio
 * @version 1.0 2018-10-21
 */
@Controller
@RequestMapping("/tool")
public class DbtableController {
	/**
	 * 数据表列表
	 *
	 * @param dbtable    查询条件
	 * @param pagination 分页数据
	 *
	 * @return 列表页面
	 */
	@RequestMapping("/dbtableList.do")
	public ModelAndView doList(@EntityParam Dbtable dbtable, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("tool/dbtableList");
		// 构造下拉框数据
		configDropdownList(dbtable, view);

		List<Dbtable> dbtableList = DbtableService.list(dbtable, null, pagination);
		// 统计符合条件的结果记录数量
		int recordCount = DbtableService.count(dbtable, null);
		pagination.setRecordCount(recordCount);
		
		view.addObject("dbtable", dbtable);
		view.addObject("dbtableList", dbtableList);
		view.addObject("pagination", pagination);
		
		return view;
	}

	/**
	 * 新增页面显示
	 *
	 * @param dbtable 预设定的数据，比如在指定的分类下新增记录
	 *
	 * @return 新增页面
	 */
	@GetMapping("/dbtableAdd.do")
	public ModelAndView doAdd(@EntityParam Dbtable dbtable) {
		ModelAndView view = new ModelAndView("tool/dbtableAdd");
		// 构造下拉框数据
		configDropdownList(dbtable, view);
		// 保存预设定的数据
		view.addObject("dbtable", dbtable);
		return view;
	}

	/**
	 * 新增页面数据提交
	 *
	 * @param user	当前操作账户
	 * @param dbtable 数据表
	 * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/dbtableAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@SessionUser Account user, @EntityParam Dbtable dbtable, HttpServletRequest request) {
		// 是否存在重复记录？
		if (exist(dbtable)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		
		long userId = LogicUtility.parseLong(user.getId(), 0L);
		
		// 创建人编号
		dbtable.setCreatorId(userId);
		// 创建时间
		dbtable.setCreateTime(new Date());
		
		int result = DbtableService.insert(dbtable);
		if (result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}
		
		// 保存字段信息
		saveFields(dbtable, request);
		
		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("新增数据表");
		log.setBizData(dbtable);
		request.setAttribute("log", log);
		return ResponseBase.SUCCESS;
	}

	/**
	 * 修改页面显示
	 *
	 * @param dbtableId 表编号（关键字）
	 *
	 * @return 修改页面
	 */
	@GetMapping("/dbtableUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value = "id") Long dbtableId) {
		ModelAndView view = new ModelAndView("tool/dbtableUpdate");
		if (dbtableId == null || dbtableId == 0L) {
			return view;
		}
		// 查询条件
		Dbtable where = new Dbtable();
		where.setId(dbtableId);
		Dbtable dbtable = DbtableService.getObject(where, null);
		
		if (dbtable == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + dbtableId + "的数据表");
			return common;
		}
		
		view.addObject("dbtable", dbtable);
		
		// 查询字段信息
		DbtableField dfWhere = new DbtableField();
		dfWhere.setDbtableId(dbtableId);
		
		List<DbtableField> fieldList = DbtableFieldService.list(dfWhere, null, null);
		
		if(fieldList != null) {
			view.addObject("fieldList", fieldList);
		}
		
		// 构造下拉框数据
		configDropdownList(dbtable, view);
		return view;
	}

	/**
	 * 修改页面数据提交
	 *
	 * @param dbtable 数据表
	 * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/dbtableUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam Dbtable dbtable, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long dbtableId = dbtable.getId();
		if (dbtableId == null || dbtableId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		
		// 是否存在重复记录？
		if (exist(dbtable)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		// 第1个参数dbtable，取关键字段dbtable.dbtableId为条件
		// 第3个参数dbtable，取dbtable内关键字段以外其它属性数据
		int result = DbtableService.update(dbtable, null, dbtable);
		if (result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		
		// 保存字段信息
		saveFields(dbtable, request);
		
		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("修改数据表");
		log.setBizData(dbtable);
		request.setAttribute("log", log);
		return ResponseBase.SUCCESS;
	}

	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 *
	 * @param dbtableId 表编号（关键字）
	 * @param request   HTTP请求
	 *
	 * @return 删除结果
	 */
	@PostMapping(value = "/dbtableDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value = "id") Long dbtableId, HttpServletRequest request) {
		if (dbtableId == null || dbtableId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 获取待删除的对象，用于日志记录
		Dbtable dbtable = DbtableService.getObject(dbtableId);
		if (dbtable == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 限定条件
		Dbtable where = new Dbtable();
		where.setId(dbtableId);
		int result = DbtableService.delete(where, null);
		if (result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("删除数据表");
		log.setBizData(dbtable);
		request.setAttribute("log", log);
		return ResponseBase.SUCCESS;
	}

	/**
	 * 展示页面
	 *
	 * @param dbtableId 表编号（关键字）
	 *
	 * @return 展示页面
	 */
	@RequestMapping("/dbtableView.do")
	public ModelAndView doView(@RequestParam(value = "id") Long dbtableId) {
		ModelAndView view = new ModelAndView("tool/dbtableView");
		if (dbtableId == null || dbtableId == 0L) {
			return view;
		}
		// 查询条件
		Dbtable where = new Dbtable();
		where.setId(dbtableId);
		Dbtable dbtable = DbtableService.getObject(where, null);
		
		if (dbtable == null) {			
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + dbtableId + "的数据表");
			return common;
		}
		
		view.addObject("dbtable", dbtable);
		
		// 查询字段信息
		DbtableField dfWhere = new DbtableField();
		dfWhere.setDbtableId(dbtableId);
		
		List<DbtableField> fieldList = DbtableFieldService.list(dfWhere, null, null);
		
		if(fieldList != null) {
			view.addObject("fieldList", fieldList);
		}
		
		// 代码模版列表
		String[] templates = DbtableService.getTemplates();
		view.addObject("templates", templates);
		
		return view;
	}

	/**
	 * 复制页面显示
	 *
	 * @param dbtableId 表编号（关键字）
	 *
	 * @return 复制页面
	 */
	@GetMapping("/dbtableCopy.do")
	public ModelAndView doCopy(@RequestParam(value = "id") Long dbtableId) {
		ModelAndView view = new ModelAndView("tool/dbtableCopy");
		if (dbtableId == null || dbtableId == 0L) {
			return view;
		}
		// 查询条件
		Dbtable where = new Dbtable();
		where.setId(dbtableId);
		Dbtable dbtable = DbtableService.getObject(where, null);
		
		if (dbtable == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + dbtableId + "的数据表");
			return common;
		}
		
		view.addObject("dbtable", dbtable);
		
		// 查询字段信息
		DbtableField dfWhere = new DbtableField();
		dfWhere.setDbtableId(dbtableId);
		
		List<DbtableField> fieldList = DbtableFieldService.list(dfWhere, null, null);
		
		if(fieldList != null) {
			view.addObject("fieldList", fieldList);
		}
		
		// 构造下拉框数据
		configDropdownList(dbtable, view);
		return view;
	}

	/**
	 * 代码生成
	 *
	 * @param user 当前登录账号
	 * @param dbtableId 表编号（关键字）
	 * @param codeTemplate Code模版
	 *
	 * @return 代码生成的结果
	 */
	@PostMapping("/codeGenerate.do")
	@ResponseBody
	public ResponseBase generateCode(@SessionUser Account user, Long dbtableId, String codeTemplate) {		
		if (dbtableId == null || dbtableId == 0L || codeTemplate == null || codeTemplate.trim().length() == 0) {
			return ResponseBase.REQUEST_DATA_EXPECTED;
		}
		
		// 查询条件
		Dbtable where = new Dbtable();
		where.setId(dbtableId);
		Dbtable dbtable = DbtableService.getObject(where, null);
		if (dbtable == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		
		// 查询字段信息
		DbtableField dfWhere = new DbtableField();
		dfWhere.setDbtableId(dbtableId);
		
		List<DbtableField> fieldList = DbtableFieldService.list(dfWhere, null, null);
		dbtable.setFieldList(fieldList);
		
		try {			
			// 生成代码
			// 生成的文件信息，Map<file_name, file_content>
			// 不需要将生成的文件写入到硬盘中
			Map<String, byte[]> codes = GeneratorMPI.doGenerate(codeTemplate, dbtable);
			String resultMessage = "文件生成完成！";

			// 将生成的文件压缩为zip包
			String downloadFile = UserService.getTemporaryFile(user);
			
			GeneratorMPI.zip(downloadFile, codes);

			
			resultMessage += "<br><br>所有生成的文件已经打包，请点击“确定”按钮下载压缩包。";
			
			DataResponse<String> res = new DataResponse<String>();
			res.setMessage(resultMessage);
			
			res.setData("Temp/" + user.getId() + ".dat");
			
			return res;
		} catch (IOException e) {
			return ResponseBase.EXCEPTION_OCCURED;
		}
	}

	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 *
	 * @param dbtable 查询条件
	 * @param view    视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(Dbtable dbtable, ModelAndView view) {
		// 传入了工程编号？
		Long pId = dbtable.getProjectId();
		if (pId != null) {
			Project p = ProjectService.getObject(pId);
			dbtable.setProject(p);
		}

		// 字段类型列表
		List<KeyValue> typeList = DbtableService.getTypeList();
		view.addObject("typeList", typeList);
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 *
	 * @param temp 将要修改的数据表POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(Dbtable temp) {
		// 检查修改的数据表是否有重复记录
		Dbtable form = new Dbtable();
		// 关键字限定条件
		String str = (temp.getId() == null ? null : "DbtableId != " + temp.getId());
		// 其它信息限定条件
		// 工程编号，关联tool_project_base.ProjectId
		form.setProjectId(temp.getProjectId());
		// 数据表名称
		form.setTableName(temp.getTableName());

		return (DbtableService.getObject(form, str) != null);
	}

	/**
	 * 保存数据库表的字段信息
	 * 
	 * @param dbtable 数据库表基本信息
	 * @param request HTTP请求
	 */
	private void saveFields(Dbtable dbtable, HttpServletRequest request) {
		Long dbtableId = dbtable.getId();
		if(dbtableId == null) {
			return;
		}
		
		// 删除之前的数据库字段
		DbtableField where = new DbtableField();
		where.setDbtableId(dbtableId);
		DbtableFieldService.delete(where, null);
		
		// 将页面传入的字段信息对应到对象中
		String[] fieldName = request.getParameterValues("fieldName");
		String[] fieldType = request.getParameterValues("fieldType");
		String[] fieldLength = request.getParameterValues("fieldLength");
		String[] pK = request.getParameterValues("pKValue");
		String[] notNull = request.getParameterValues("notNullValue");
		String[] fieldDescription = request.getParameterValues("fieldDescription");
		
		List<DbtableField> fieldList = new ArrayList<DbtableField>();
		dbtable.setFieldList(fieldList);
		
		// 构造DbtableFieldDAO	
		for (int i = 0; fieldName != null && i < fieldName.length; i++) {
			DbtableField field = new DbtableField();
			field.setFieldName(fieldName[i]);
			field.setFieldType(fieldType[i]);
			field.setFieldLength(fieldLength[i]);
			field.setIsPk("true".equals(pK[i]));
			field.setIsNotNull("true".equals(notNull[i]));
			field.setFieldDescription(fieldDescription[i]);
			field.setOrderBy(i);
			// 数据库表编号
			field.setDbtableId(dbtable.getId());

			int row = DbtableFieldService.insert(field);
			
			if(row > 0) {
				fieldList.add(field);
			}
		}

		// 计算评估基本工作量(人.天)
		double dbl = DbtableService.getWorkload(dbtable);
		
		Dbtable dWhere = new Dbtable();
		dWhere.setId(dbtable.getId());
		
		Dbtable dData = new Dbtable();
		dData.setWorkload(dbl);
		// 更新评估基本工作量(人.天)
		DbtableService.update(dWhere, null, dData);
	}
}