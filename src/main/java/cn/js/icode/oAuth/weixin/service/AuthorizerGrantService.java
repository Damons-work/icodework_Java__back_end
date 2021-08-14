package cn.js.icode.oAuth.weixin.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.data.Pagination;
import team.bangbang.common.redis.RedisUtil;
import team.bangbang.common.utility.LogicUtility;
import cn.js.icode.oAuth.weixin.data.AuthorizerGrant;
import cn.js.icode.oAuth.weixin.mapper.AuthorizerGrantMapper;

/**
 * 公众号/小程序授权信息 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-07-01
 */
@Service
public final class AuthorizerGrantService {
	/** appid - grantid对应关系前缀 */
	public static final String KEY_AUTHORIZERID_GRANT_PREFIX = "authorizerId_grantId:";
	
	/* 公众号/小程序授权信息（AuthorizerGrant）Mapper */
	@Resource
	private AuthorizerGrantMapper _authorizerGrantMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static AuthorizerGrantMapper authorizerGrantMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		authorizerGrantMapper = _authorizerGrantMapper;
	}
	
	/**
	 * 得到指定的公众号/小程序授权信息
	 *
	 * @param grantId
	 *            指定的授权编号
	 * @return 公众号/小程序授权信息
	 */
	public static AuthorizerGrant getObject(String grantId) {
		if(authorizerGrantMapper == null) {
			return null;
		}
		
		// 参数校验
		if(grantId == null || grantId.trim().length() == 0) {
			return null;
		}
		// 查询条件
		AuthorizerGrant form = new AuthorizerGrant();
		form.setId(grantId);
		return authorizerGrantMapper.getObject(form, null);
	}
	/**
	 * 插入一条公众号/小程序授权信息
	 *
	 * @param data
	 *            插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(AuthorizerGrant data) {
		if(authorizerGrantMapper == null) {
			return 0;
		}
		
		if (data.getId() == null) {
			// 获取最大的GrantId
			AuthorizerGrant ag = getObject(new AuthorizerGrant(), null);
			String maxId = (ag != null ? ag.getId() : "G0000");
			int seq = LogicUtility.parseInt(maxId.substring(1), 0) + 1;
			maxId = "G" + LogicUtility.toDigitString(seq, 4);
			data.setId(maxId);
		}
		
		// appid - grantid 对应关系保存到缓存中
		RedisUtil.setString(KEY_AUTHORIZERID_GRANT_PREFIX + data.getAuthorizerId(), data.getId(), 0);
		
		return authorizerGrantMapper.insert(data);
	}
	
	/**
	 * 删除公众号/小程序授权信息
	 *
	 * @param where
	 *            删除条件，不能为null
	 * @param appendix
	 *            附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(AuthorizerGrant where, String appendix) {
		if(authorizerGrantMapper == null) {
			return 0;
		}
		
		return authorizerGrantMapper.delete(where, appendix);
	}
	/**
	 * 查询一条公众号/小程序授权信息，并转化为相应的POJO对象
	 *
	 * @param where
	 *            查询条件，不能为null
	 * @param appendix
	 *            附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static AuthorizerGrant getObject(AuthorizerGrant where, String appendix) {
		if(authorizerGrantMapper == null) {
			return null;
		}
		
		return authorizerGrantMapper.getObject(where, appendix);
	}
	
	/**
	 * 修改公众号/小程序授权信息
	 *
	 * @param where
	 *            更新条件，不能为null
	 * @param appendix
	 *            附加限定条件
	 * @param data
	 *            更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(AuthorizerGrant where, String appendix, AuthorizerGrant data) {
		if(authorizerGrantMapper == null) {
			return 0;
		}
		
		return authorizerGrantMapper.update(where, appendix, data);
	}
	/**
	 * 查询多条公众号/小程序授权信息，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *            更新条件，不能为null
	 * @param appendix
	 *            附加限定条件
	 * @param pagination
	 *            分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<AuthorizerGrant> list(AuthorizerGrant where, String appendix, Pagination pagination) {
		if(authorizerGrantMapper == null) {
			return Collections.emptyList();
		}
		
		return authorizerGrantMapper.list(where, appendix, pagination);
	}
	/**
	 * 获得符合条件的公众号/小程序授权信息数量
	 *
	 * @param where
	 *            查询条件，不能为null
	 * @param appendix
	 *            附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(AuthorizerGrant where, String appendix) {
		if(authorizerGrantMapper == null) {
			return 0;
		}
		
		return authorizerGrantMapper.count(where, appendix);
	}
}
