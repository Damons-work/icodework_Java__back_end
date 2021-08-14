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

import cn.js.icode.project.entity.Person;
import cn.js.icode.project.entity.ProjectPerson;
import cn.js.icode.project.entity.ProjectX;
import cn.js.icode.project.service.PersonService;
import cn.js.icode.project.service.ProjectPersonService;
import cn.js.icode.project.service.ProjectXService;
import springfox.documentation.annotations.ApiIgnore;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.exception.BizException;
import team.bangbang.common.log.OperationLog;

/**
 * 项目信息 - Http服务
 *
 * 对应Feign：cn.js.icode.project.feign.ProjectXFeign
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
@ApiIgnore
@RestController
@CrossOrigin(allowCredentials="true", allowedHeaders="*", origins="*", maxAge=3600)
@RequestMapping("/project/projectX")
public class ProjectXController {
	/**
	 * 查询一条项目信息，并转化为相应的POJO对象
	 *
	 * @param projectX 查询条件，不能为null
	 *
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	@PostMapping("/get")
	public ResponseBase get(@RequestBody ProjectX projectX) {

		if(projectX.getId() == null || projectX.getId() == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 查询
		projectX = ProjectXService.getObject(projectX, null);
		if(projectX == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		
		// 数据装配
		// 钉子信息
		Person leader = PersonService.getObject(projectX.getId());
		projectX.setLeader(leader);
		
		// 参与人员列表
		// 查询条件
		ProjectPerson form = new ProjectPerson();
		form.setProjectId(projectX.getId());
		List<ProjectPerson> ppList = ProjectPersonService.list(form, null, null);
		projectX.setProjectPersonList(ppList);
		
		DataResponse<ProjectX> result = new DataResponse<ProjectX>();
		result.setData(projectX);
		
		return result;
	}

	/**
	 * 查询多条项目信息，并转化为相应的POJO对象列表
	 *
	 * @param projectX 查询条件
	 * @param pagination 分页参数
	 *
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	@PostMapping("/list")
	public ResponseBase list(@RequestBody ProjectX projectX, Pagination pagination) {

		// 附加限定条件，这里可以修改附加限定条件
		String appendix = null;
		
		if (pagination != null) {
			// 统计符合条件的结果记录数量
			int recordCount = ProjectXService.count(projectX, appendix);
			pagination.setRecordCount(recordCount);
		}
		// 符合条件的结果（根据pagination参数分页）
		List<ProjectX> projectXList = ProjectXService.list(projectX, appendix, pagination);	
		
		// 数据装配
		for (int i = 0; projectXList != null && i < projectXList.size(); i++) {
			ProjectX px = projectXList.get(i);
			if (px == null) continue;
			// 钉子信息
			Person leader = PersonService.getObject(px.getId());
			px.setLeader(leader);
		}
		
		DataResponse<Map<String, Object>> result = new DataResponse<Map<String, Object>>();
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put("list", projectXList);
		datas.put("pagination", pagination);

		result.setData(datas);
		
		return result;
	}

	/**
	 * 获得符合条件的项目信息数量
	 *
	 * @param projectX 查询条件，不能为null
	 *
	 * @return 返回记录数量
	 */
	@PostMapping("/count")
	public ResponseBase count(@RequestBody ProjectX projectX) {
		// 附加限定条件，这里可以修改附加限定条件
		String appendix = null;
		
		int n = ProjectXService.count(projectX, appendix);
		DataResponse<Integer> result = new DataResponse<Integer>();

		result.setData(n);
		
		return result;
	}

	/**
	 * 插入一条项目信息
	 *
	 * @param projectX 插入的数据，不能为null
     * @param request HTTP请求
	 *
	 * @return 插入结果
	 */
	@PostMapping("/insert")
	public ResponseBase insert(@RequestBody ProjectX projectX, HttpServletRequest request) {
		// 是否存在重复记录？
		if(exist(projectX)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		
		// 将钉子编号
		Long leaderId = 0L;
		// 遍历参与人员，获得钉子编号
		List<ProjectPerson> ppList = (projectX != null ? projectX.getProjectPersonList() : null);
		for (int i = 0; ppList != null && i < ppList.size(); i++) {
			ProjectPerson pp = ppList.get(i);
			if (pp == null) continue;
			// 参与人员是否是钉子
			Boolean isLeader = pp.getIsLeaderFlag();
			if (isLeader != null && isLeader.booleanValue()) {
				leaderId = pp.getId();
				break;
			}
		}
		
		projectX.setLeaderId(leaderId);

		int n = 0;
		try {
			n = ProjectXService.insert(projectX);
		} catch (BizException e) {
			e.printStackTrace();
		}		

		if(n != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}
		
		// 删除该项目下之前的参与人员信息
		ProjectPerson where = new ProjectPerson();
		where.setProjectId(projectX.getId());
		ProjectPersonService.delete(where, null);

		// 插入参与人员信息
		try {
			// 遍历参与人员
			for (int i = 0; ppList != null && i < ppList.size(); i++) {
				ProjectPerson pp = ppList.get(i);
				if (pp == null) continue;
				ProjectPersonService.insert(pp);
			}
		} catch (BizException e) {
			e.printStackTrace();
		}
		
        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("新增项目信息");
        log.setBizData(projectX);
        request.setAttribute("log", log);
		
		DataResponse<ProjectX> result = new DataResponse<ProjectX>();
		result.setData(projectX);
		
		return result;
	}

	/**
	 * 修改项目信息
	 *
	 * @param projectX 更新条件，不能为null
     * @param request HTTP请求
	 *
	 * @return 成功修改的记录数量
	 */
	@PostMapping("/update")
	public ResponseBase update(@RequestBody ProjectX projectX, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long project_id = projectX.getId();

		if(project_id == null || project_id == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 是否存在重复记录？
		if(exist(projectX)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		
		// 将钉子编号
		Long leaderId = 0L;
		// 遍历参与人员，获得钉子编号
		List<ProjectPerson> ppList = (projectX != null ? projectX.getProjectPersonList() : null);
		for (int i = 0; ppList != null && i < ppList.size(); i++) {
			ProjectPerson pp = ppList.get(i);
			if (pp == null) continue;
			// 参与人员是否是钉子
			Boolean isLeader = pp.getIsLeaderFlag();
			if (isLeader != null && isLeader.booleanValue()) {
				leaderId = pp.getId();
				break;
			}
		}
		
		projectX.setLeaderId(leaderId);

		// 第1个参数projectX，取关键字段projectX.project_id为条件
		// 第3个参数projectX，取projectX内关键字段以外其它属性数据
		int n = ProjectXService.update(projectX, null, projectX);
		
		if(n != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		
		// 删除该项目下之前的参与人员信息
		ProjectPerson where = new ProjectPerson();
		where.setProjectId(projectX.getId());
		ProjectPersonService.delete(where, null);

		// 插入参与人员信息
		try {
			// 遍历参与人员
			for (int i = 0; ppList != null && i < ppList.size(); i++) {
				ProjectPerson pp = ppList.get(i);
				if (pp == null) continue;
				ProjectPersonService.insert(pp);
			}
		} catch (BizException e) {
			e.printStackTrace();
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("修改项目信息");
        log.setBizData(projectX);
        request.setAttribute("log", log);		
		
		// 重新查询，返回到前端
		projectX = ProjectXService.getObject(projectX, null);
		DataResponse<ProjectX> result = new DataResponse<ProjectX>();
		result.setData(projectX);
		
		return result;
	}

	/**
	 * 删除项目信息
	 *
	 * @param projectX 删除条件，必须包含关键字段
     * @param request HTTP请求
	 *
	 * @return 成功删除的记录数量
	 */
	@PostMapping("/delete")
	public ResponseBase delete(@RequestBody ProjectX projectX, HttpServletRequest request) {

		if(projectX.getId() == null || projectX.getId() == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 获取待删除的对象，用于日志记录
        projectX = ProjectXService.getObject(projectX, null);
        if(projectX == null) {
            return ResponseBase.DATA_NOT_FOUND;
        }

		// 限定条件
		ProjectX where = new ProjectX();
		where.setId(projectX.getId());
		
		int result = ProjectXService.delete(where, null);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("删除项目信息");
        log.setBizData(where);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 * 
	 * @param temp
	 *            将要修改的项目信息POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(ProjectX temp) {
		// 检查修改的项目信息是否有重复记录
		ProjectX form = new ProjectX();
		
		// 关键字限定条件

		String str = (temp.getId() == null ? null : "project_id != " + temp.getId());

		// 其它信息限定条件
		// 项目名称
		form.setProjectName(temp.getProjectName());

		return (ProjectXService.getObject(form, str) != null);
	}
	
	/**
	 * 获得项目信息涉及到的字典
	 *
	 * @return 以字段名为KEY，字典项List<KeyValue>为VALUE
	 */
	@PostMapping("/dict")
	public ResponseBase getDictMap() {
		DataResponse<Map<String, List<KeyValue>>> dr = new DataResponse<Map<String, List<KeyValue>>>();
		Map<String, List<KeyValue>> mp = new HashMap<String, List<KeyValue>>();

		// 状态{1:进行中，2:已完成，3：已删除}列表
		List<KeyValue> statusList = ProjectXService.getStatusList();
		mp.put("status", statusList);

		dr.setData(mp);
		
		return dr;
	}
}
