package cn.js.icode.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.js.icode.project.entity.ProjectCost;
import cn.js.icode.project.service.ProjectCostService;
import springfox.documentation.annotations.ApiIgnore;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.exception.BizException;
import team.bangbang.common.log.OperationLog;

/**
 * 项目成本 - Http服务
 *
 * 对应Feign：cn.js.icode.project.feign.ProjectCostFeign
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
@ApiIgnore
@RestController
@CrossOrigin(allowCredentials="true", allowedHeaders="*", origins="*", maxAge=3600)
@RequestMapping("/project/projectCost")
public class ProjectCostController {
	/**
	 * 查询一条项目成本，并转化为相应的POJO对象
	 *
	 * @param projectCost 查询条件，不能为null
	 *
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	@PostMapping("/get")
	public ResponseBase get(@RequestBody ProjectCost projectCost) {

		if(projectCost.getId() == null || projectCost.getId() == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 查询
		projectCost = ProjectCostService.getObject(projectCost, null);
		DataResponse<ProjectCost> result = new DataResponse<ProjectCost>();
		result.setData(projectCost);
		
		return result;
	}

	/**
	 * 查询多条项目成本，并转化为相应的POJO对象列表
	 *
	 * @param projectCost 查询条件
	 * @param pagination 分页参数
	 *
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	@PostMapping("/list")
	public ResponseBase list(@RequestBody ProjectCost projectCost, Pagination pagination) {

		// 附加限定条件，这里可以修改附加限定条件
		String appendix = null;
		
		if (pagination != null) {
			// 统计符合条件的结果记录数量
			int recordCount = ProjectCostService.count(projectCost, appendix);
			pagination.setRecordCount(recordCount);
		}
		// 符合条件的结果（根据pagination参数分页）
		List<ProjectCost> projectCostList = ProjectCostService.list(projectCost, appendix, pagination);	
		
		DataResponse<Map<String, Object>> result = new DataResponse<Map<String, Object>>();
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put("list", projectCostList);
		datas.put("pagination", pagination);

		result.setData(datas);
		
		return result;
	}

	/**
	 * 获得符合条件的项目成本数量
	 *
	 * @param projectCost 查询条件，不能为null
	 *
	 * @return 返回记录数量
	 */
	@PostMapping("/count")
	public ResponseBase count(@RequestBody ProjectCost projectCost) {
		// 附加限定条件，这里可以修改附加限定条件
		String appendix = null;
		
		int n = ProjectCostService.count(projectCost, appendix);
		DataResponse<Integer> result = new DataResponse<Integer>();

		result.setData(n);
		
		return result;
	}

	/**
	 * 插入一条项目成本
	 *
	 * @param projectCost 插入的数据，不能为null
     * @param request HTTP请求
	 *
	 * @return 插入结果
	 */
	@PostMapping("/insert")
	public ResponseBase insert(@RequestBody ProjectCost projectCost, HttpServletRequest request) {
		// 是否存在重复记录？
		if(exist(projectCost)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		int n = 0;
		try {
			n = ProjectCostService.insert(projectCost);
		} catch (BizException e) {
			e.printStackTrace();
		}		

		if(n != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("新增项目成本");
        log.setBizData(projectCost);
        request.setAttribute("log", log);
		
		DataResponse<ProjectCost> result = new DataResponse<ProjectCost>();
		result.setData(projectCost);
		
		return result;
	}

	/**
	 * 修改项目成本
	 *
	 * @param projectCost 更新条件，不能为null
     * @param request HTTP请求
	 *
	 * @return 成功修改的记录数量
	 */
	@PostMapping("/update")
	public ResponseBase update(@RequestBody ProjectCost projectCost, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long cost_id = projectCost.getId();

		if(cost_id == null || cost_id == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 是否存在重复记录？
		if(exist(projectCost)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		// 第1个参数projectCost，取关键字段projectCost.cost_id为条件
		// 第3个参数projectCost，取projectCost内关键字段以外其它属性数据
		int n = ProjectCostService.update(projectCost, null, projectCost);
		
		if(n != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("修改项目成本");
        log.setBizData(projectCost);
        request.setAttribute("log", log);		
		
		// 重新查询，返回到前端
		projectCost = ProjectCostService.getObject(projectCost, null);
		DataResponse<ProjectCost> result = new DataResponse<ProjectCost>();
		result.setData(projectCost);
		
		return result;
	}

	/**
	 * 删除项目成本
	 *
	 * @param projectCost 删除条件，必须包含关键字段
     * @param request HTTP请求
	 *
	 * @return 成功删除的记录数量
	 */
	@PostMapping("/delete")
	public ResponseBase delete(@RequestBody ProjectCost projectCost, HttpServletRequest request) {

		if(projectCost.getId() == null || projectCost.getId() == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 获取待删除的对象，用于日志记录
        projectCost = ProjectCostService.getObject(projectCost, null);
        if(projectCost == null) {
            return ResponseBase.DATA_NOT_FOUND;
        }

		// 限定条件
		ProjectCost where = new ProjectCost();
		where.setId(projectCost.getId());
		
		int result = ProjectCostService.delete(where, null);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("删除项目成本");
        log.setBizData(where);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 * 
	 * @param temp
	 *            将要修改的项目成本POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(ProjectCost temp) {
		// 检查修改的项目成本是否有重复记录
		ProjectCost form = new ProjectCost();
		
		// 关键字限定条件

		String str = (temp.getId() == null ? null : "cost_id != " + temp.getId());

		// 其它信息限定条件

		// 项目编号，关联project_project_base.project_id
		form.setProjectId(temp.getProjectId());

		// 所属年月，yyyyMM格式，如：202104
		form.setYearMonth(temp.getYearMonth());

		// 人员编号，关联project_person_base.person_id
		form.setPersonId(temp.getPersonId());

		// 月底或者当前日期（是几号？）
		form.setToDay(temp.getToDay());

		// 产生的成本（元）
		form.setPersonCost(temp.getPersonCost());

		return (ProjectCostService.getObject(form, str) != null);
	}
	
	/**
	 * 获得项目成本涉及到的字典
	 *
	 * @return 以字段名为KEY，字典项List<KeyValue>为VALUE
	 */
	@PostMapping("/dict")
	public ResponseBase getDictMap() {
		DataResponse<Map<String, List<KeyValue>>> dr = new DataResponse<Map<String, List<KeyValue>>>();
		Map<String, List<KeyValue>> mp = new HashMap<String, List<KeyValue>>();

		dr.setData(mp);
		
		return dr;
	}
}
