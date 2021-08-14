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
import cn.js.icode.project.service.PersonService;
import springfox.documentation.annotations.ApiIgnore;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.exception.BizException;
import team.bangbang.common.log.OperationLog;

/**
 * 人员信息 - Http服务
 *
 * 对应Feign：cn.js.icode.project.feign.PersonFeign
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
@ApiIgnore
@RestController
@CrossOrigin
@RequestMapping("/project/person")
public class PersonController {
	/**
	 * 查询一条人员信息，并转化为相应的POJO对象
	 *
	 * @param person 查询条件，不能为null
	 *
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	@PostMapping("/get")
	public ResponseBase get(@RequestBody Person person) {

		if(person.getId() == null || person.getId() == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 查询
		person = PersonService.getObject(person, null);
		DataResponse<Person> result = new DataResponse<Person>();
		result.setData(person);
		
		return result;
	}

	/**
	 * 查询多条人员信息，并转化为相应的POJO对象列表
	 *
	 * @param person 查询条件
	 * @param pagination 分页参数
	 *
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	@PostMapping("/list")
	public ResponseBase list(@RequestBody Person person, Pagination pagination) {

		// 设置缺省值：是否有效标识
//		if(person.getActiveFlag() == null) {
//			person.setActiveFlag(true);
//		}
		// 附加限定条件，这里可以修改附加限定条件
		String appendix = null;
		
		if (pagination != null) {
			// 统计符合条件的结果记录数量
			int recordCount = PersonService.count(person, appendix);
			pagination.setRecordCount(recordCount);
		}
		// 符合条件的结果（根据pagination参数分页）
		List<Person> personList = PersonService.list(person, appendix, pagination);	
		
		DataResponse<Map<String, Object>> result = new DataResponse<Map<String, Object>>();
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put("list", personList);
		datas.put("pagination", pagination);

		result.setData(datas);
		
		return result;
	}

	/**
	 * 获得符合条件的人员信息数量
	 *
	 * @param person 查询条件，不能为null
	 *
	 * @return 返回记录数量
	 */
	@PostMapping("/count")
	public ResponseBase count(@RequestBody Person person) {
		// 附加限定条件，这里可以修改附加限定条件
		String appendix = null;
		
		int n = PersonService.count(person, appendix);
		DataResponse<Integer> result = new DataResponse<Integer>();

		result.setData(n);
		
		return result;
	}

	/**
	 * 插入一条人员信息
	 *
	 * @param person 插入的数据，不能为null
     * @param request HTTP请求
	 *
	 * @return 插入结果
	 */
	@PostMapping("/insert")
	public ResponseBase insert(@RequestBody Person person, HttpServletRequest request) {
		// 是否存在重复记录？
		if(exist(person)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		int n = 0;
		try {
			n = PersonService.insert(person);
		} catch (BizException e) {
			e.printStackTrace();
		}		

		if(n != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("新增人员信息");
        log.setBizData(person);
        request.setAttribute("log", log);
		
		DataResponse<Person> result = new DataResponse<Person>();
		result.setData(person);
		
		return result;
	}

	/**
	 * 修改人员信息
	 *
	 * @param person 更新条件，不能为null
     * @param request HTTP请求
	 *
	 * @return 成功修改的记录数量
	 */
	@PostMapping("/update")
	public ResponseBase update(@RequestBody Person person, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long person_id = person.getId();

		if(person_id == null || person_id == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 是否存在重复记录？
		if(exist(person)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		// 第1个参数person，取关键字段person.person_id为条件
		// 第3个参数person，取person内关键字段以外其它属性数据
		int n = PersonService.update(person, null, person);
		
		if(n != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("修改人员信息");
        log.setBizData(person);
        request.setAttribute("log", log);		
		
		// 重新查询，返回到前端
		person = PersonService.getObject(person, null);
		DataResponse<Person> result = new DataResponse<Person>();
		result.setData(person);
		
		return result;
	}

	/**
	 * 删除人员信息
	 *
	 * @param person 删除条件，必须包含关键字段
     * @param request HTTP请求
	 *
	 * @return 成功删除的记录数量
	 */
	@PostMapping("/delete")
	public ResponseBase delete(@RequestBody Person person, HttpServletRequest request) {

		if(person.getId() == null || person.getId() == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 获取待删除的对象，用于日志记录
        person = PersonService.getObject(person, null);
        if(person == null) {
            return ResponseBase.DATA_NOT_FOUND;
        }

		// 限定条件
		Person where = new Person();
		where.setId(person.getId());
		
		int result = PersonService.delete(where, null);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("删除人员信息");
        log.setBizData(where);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 * 
	 * @param temp
	 *            将要修改的人员信息POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(Person temp) {
		// 检查修改的人员信息是否有重复记录
		Person form = new Person();
		
		// 关键字限定条件

		String str = (temp.getId() == null ? null : "person_id != " + temp.getId());

		// 其它信息限定条件

		// 姓名
		form.setPersonName(temp.getPersonName());

		// 成本（元）
		form.setCost(temp.getCost());

		// 微信小程序id
		form.setWxOpenId(temp.getWxOpenId());

		// 是否有效标识
		form.setActiveFlag(temp.getActiveFlag());

		return (PersonService.getObject(form, str) != null);
	}
	
	/**
	 * 获得人员信息涉及到的字典
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
