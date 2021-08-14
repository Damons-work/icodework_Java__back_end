package cn.js.icode.project.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.js.icode.project.entity.Person;
import cn.js.icode.project.mapper.PersonMapper;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.exception.BizException;

/**
 * 人员信息 - Service
 *
 * @author Bangbang
 * @version 1.0  2021-07-15
 */
@Service
public class PersonService {
	/* 人员信息（Person）Mapper */
	@Resource
	private PersonMapper _personMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static PersonMapper personMapper = null;

	/**
	 * IOC初始化之后执行的方法
	 */
	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		personMapper = _personMapper;
	}

	/**
	 * 得到指定的人员信息
	 *
	 * @param person_id
	 *			指定的人员编号
	 * @return 人员信息
	 */
	public static Person getObject(Long person_id) {
		if(personMapper == null) {
			return null;
		}

		// 参数校验

		if(person_id == null || person_id == 0L) {
			return null;
		}

		// 查询条件
		Person form = new Person();
		form.setId(person_id);

		Person unt = getObject(form, null);
		if (unt == null) {
			return unt;
		}
		// 加载动态数据字典

		return unt;
	}

	/**
	 * 查询一条人员信息，并转化为相应的POJO对象
	 *
	 * @param person
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Person getObject(Person person, String appendix) {
		if(personMapper == null) {
			return null;
		}

		// 参数校验
		if(person == null && (appendix == null || appendix.trim().length() == 0)) {
			return null;
		}

		return personMapper.getObject(person, appendix);
	}

	/**
	 * 插入一条人员信息
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Person data) throws BizException {
		if(personMapper == null) {
			return 0;
		}

		// 关键字段是int类型，默认为自增长，直接用Mybatis下面的配置获得新增后的关键字段值

		// 非空判断
		StringBuffer sb = new StringBuffer();

		// 检查非空值：姓名
		if(data.getPersonName() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("personName");
		}
	
		// 检查非空值：成本（元）
		if(data.getCost() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("cost");
		}
	
		// 检查非空值：微信小程序id
		if(data.getWxOpenId() == null) {
			if (sb.length() > 0) sb.append("，");
			sb.append("wxOpenId");
		}
	
		// 设置缺省值：是否有效标识
		if(data.getActiveFlag() == null) {
			data.setActiveFlag(false);
		}
	
		if (sb.length() > 0) {
			throw new BizException("以下字段未传入值：" + sb);
		}

		return personMapper.insert(data);
	}

	/**
	 * 删除人员信息
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Person where, String appendix) {
		if(personMapper == null) {
			return 0;
		}

		return personMapper.delete(where, appendix);
	}

	/**
	 * 修改人员信息
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Person where, String appendix, Person data) {
		if(personMapper == null) {
			return 0;
		}

		return personMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条人员信息，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Person> list(Person where, String appendix, Pagination pagination) {
		if(personMapper == null) {
			return Collections.emptyList();
		}

		List<Person> lst = personMapper.list(where, appendix, pagination);
		if (lst == null || lst.isEmpty()) {
			return lst;
		}
		// 加载动态数据字典

		return lst;
	}

	/**
	 * 获得符合条件的人员信息数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Person where, String appendix) {
		if(personMapper == null) {
			return 0;
		}

		return personMapper.count(where, appendix);
	}

}
