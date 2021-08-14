package cn.js.icode.basis.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.js.icode.basis.data.Partner;
import cn.js.icode.basis.mapper.PartnerMapper;
import team.bangbang.common.data.Pagination;

/**
 * 商户信息 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-12-02
 */
@Service
public final class PartnerService {
	/* 商户信息（Partner）Mapper */
	@Resource
	private PartnerMapper _partnerMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static PartnerMapper partnerMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		partnerMapper = _partnerMapper;
	}

	/**
	 * 得到指定的商户信息
	 * 
	 * @param partnerId
	 *			指定的商户编号
	 * @return 商户信息
	 */
	public static Partner getObject(String partnerId) {
		if(partnerMapper == null) {
			return null;
		}

		// 参数校验

		if(partnerId == null || partnerId.trim().length() == 0) {
			return null;
		}

		// 查询条件
		Partner form = new Partner();
		form.setId(partnerId);
		
		return partnerMapper.getObject(form, null);
	}

	/**
	 * 插入一条商户信息
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Partner data) {
		if(partnerMapper == null) {
			return 0;
		}

		if (data.getId() == null) {

			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			String id = UUID.randomUUID().toString();
			data.setId(id);

		}

		// 设置缺省值：是否有效
		if(data.getActiveFlag() == null) {
			data.setActiveFlag(false);
		}
	
		return partnerMapper.insert(data);
	}

	/**
	 * 删除商户信息
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Partner where, String appendix) {
		if(partnerMapper == null) {
			return 0;
		}

		return partnerMapper.delete(where, appendix);
	}

	/**
	 * 查询一条商户信息，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Partner getObject(Partner where, String appendix) {
		if(partnerMapper == null) {
			return null;
		}

		return partnerMapper.getObject(where, appendix);
	}

	/**
	 * 修改商户信息
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Partner where, String appendix, Partner data) {
		if(partnerMapper == null) {
			return 0;
		}

		return partnerMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条商户信息，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Partner> list(Partner where, String appendix, Pagination pagination) {
		if(partnerMapper == null) {
			return Collections.emptyList();
		}

		return partnerMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的商户信息数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Partner where, String appendix) {
		if(partnerMapper == null) {
			return 0;
		}

		return partnerMapper.count(where, appendix);
	}
	
	/**
	 * 获得当前所有有效的商户列表
	 * 
	 * @return 当前所有有效的商户列表
	 */
	public static List<Partner> getActivePartnerList() {
		// 查询条件
		Partner p = new Partner();
		p.setActiveFlag(true);
		
		return list(p, null, null);
	}
}
