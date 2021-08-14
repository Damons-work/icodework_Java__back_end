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

import cn.js.icode.project.entity.ProjectPerson;
import cn.js.icode.project.service.ProjectPersonService;
import springfox.documentation.annotations.ApiIgnore;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.exception.BizException;
import team.bangbang.common.log.OperationLog;

/**
 * 项目内参与人员 - Http服务
 *
 * 对应Feign：cn.js.icode.project.feign.ProjectPersonFeign
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
@ApiIgnore
@RestController
@CrossOrigin(allowCredentials="true", allowedHeaders="*", origins="*", maxAge=3600)
@RequestMapping("/project/projectPerson")
public class ProjectPersonController {
	/**
	 * 查询一条项目内参与人员，并转化为相应的POJO对象
	 *
	 * @param projectPerson 查询条件，不能为null
	 *
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	@PostMapping("/get")
	public ResponseBase get(@RequestBody ProjectPerson projectPerson) {

		if(projectPerson.getId() == null || projectPerson.getId() == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 查询
		projectPerson = ProjectPersonService.getObject(projectPerson, null);
		DataResponse<ProjectPerson> result = new DataResponse<ProjectPerson>();
		result.setData(projectPerson);
		
		return result;
	}

	/**
	 * 查询多条项目内参与人员，并转化为相应的POJO对象列表
	 *
	 * @param projectPerson 查询条件
	 * @param pagination 分页参数
	 *
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	@PostMapping("/list")
	public ResponseBase list(@RequestBody ProjectPerson projectPerson, Pagination pagination) {

		// 设置缺省值：是否是钉子标识
		if(projectPerson.getIsLeaderFlag() == null) {
			projectPerson.setIsLeaderFlag(true);
		}
	
		// 附加限定条件，这里可以修改附加限定条件
		String appendix = null;
		
		if (pagination != null) {
			// 统计符合条件的结果记录数量
			int recordCount = ProjectPersonService.count(projectPerson, appendix);
			pagination.setRecordCount(recordCount);
		}
		// 符合条件的结果（根据pagination参数分页）
		List<ProjectPerson> projectPersonList = ProjectPersonService.list(projectPerson, appendix, pagination);	
		
		DataResponse<Map<String, Object>> result = new DataResponse<Map<String, Object>>();
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put("list", projectPersonList);
		datas.put("pagination", pagination);

		result.setData(datas);
		
		return result;
	}

	/**
	 * 获得符合条件的项目内参与人员数量
	 *
	 * @param projectPerson 查询条件，不能为null
	 *
	 * @return 返回记录数量
	 */
	@PostMapping("/count")
	public ResponseBase count(@RequestBody ProjectPerson projectPerson) {
		// 附加限定条件，这里可以修改附加限定条件
		String appendix = null;
		
		int n = ProjectPersonService.count(projectPerson, appendix);
		DataResponse<Integer> result = new DataResponse<Integer>();

		result.setData(n);
		
		return result;
	}

	/**
	 * 插入一条项目内参与人员
	 *
	 * @param projectPerson 插入的数据，不能为null
     * @param request HTTP请求
	 *
	 * @return 插入结果
	 */
	@PostMapping("/insert")
	public ResponseBase insert(@RequestBody ProjectPerson projectPerson, HttpServletRequest request) {
		// 是否存在重复记录？
		if(exist(projectPerson)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		int n = 0;
		try {
			n = ProjectPersonService.insert(projectPerson);
		} catch (BizException e) {
			e.printStackTrace();
		}		

		if(n != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("新增项目内参与人员");
        log.setBizData(projectPerson);
        request.setAttribute("log", log);
		
		DataResponse<ProjectPerson> result = new DataResponse<ProjectPerson>();
		result.setData(projectPerson);
		
		return result;
	}

	/**
	 * 修改项目内参与人员
	 *
	 * @param projectPerson 更新条件，不能为null
     * @param request HTTP请求
	 *
	 * @return 成功修改的记录数量
	 */
	@PostMapping("/update")
	public ResponseBase update(@RequestBody ProjectPerson projectPerson, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long id = projectPerson.getId();

		if(id == null || id == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 是否存在重复记录？
		if(exist(projectPerson)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		// 第1个参数projectPerson，取关键字段projectPerson.id为条件
		// 第3个参数projectPerson，取projectPerson内关键字段以外其它属性数据
		int n = ProjectPersonService.update(projectPerson, null, projectPerson);
		
		if(n != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("修改项目内参与人员");
        log.setBizData(projectPerson);
        request.setAttribute("log", log);		
		
		// 重新查询，返回到前端
		projectPerson = ProjectPersonService.getObject(projectPerson, null);
		DataResponse<ProjectPerson> result = new DataResponse<ProjectPerson>();
		result.setData(projectPerson);
		
		return result;
	}

	/**
	 * 删除项目内参与人员
	 *
	 * @param projectPerson 删除条件，必须包含关键字段
     * @param request HTTP请求
	 *
	 * @return 成功删除的记录数量
	 */
	@PostMapping("/delete")
	public ResponseBase delete(@RequestBody ProjectPerson projectPerson, HttpServletRequest request) {

		if(projectPerson.getId() == null || projectPerson.getId() == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 获取待删除的对象，用于日志记录
        projectPerson = ProjectPersonService.getObject(projectPerson, null);
        if(projectPerson == null) {
            return ResponseBase.DATA_NOT_FOUND;
        }

		// 限定条件
		ProjectPerson where = new ProjectPerson();
		where.setId(projectPerson.getId());
		
		int result = ProjectPersonService.delete(where, null);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("删除项目内参与人员");
        log.setBizData(where);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 * 
	 * @param temp
	 *            将要修改的项目内参与人员POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(ProjectPerson temp) {
		// 检查修改的项目内参与人员是否有重复记录
		ProjectPerson form = new ProjectPerson();
		
		// 关键字限定条件

		String str = (temp.getId() == null ? null : "id != " + temp.getId());

		// 其它信息限定条件

		// 项目编号
		form.setProjectId(temp.getProjectId());

		// 参与人员编号，关联project_person_base.person_id
		form.setPersonId(temp.getPersonId());

		// 是否是钉子标识
		form.setIsLeaderFlag(temp.getIsLeaderFlag());

		// 开始日期
		form.setBeginDate(temp.getBeginDate());

		// 结束日期
		form.setEndDate(temp.getEndDate());

		// 投入时间百分比
		form.setTimePercent(temp.getTimePercent());

		return (ProjectPersonService.getObject(form, str) != null);
	}
	
	/**
	 * 获得项目内参与人员涉及到的字典
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
