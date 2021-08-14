package cn.js.icode.api;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.js.icode.system.data.User;
import cn.js.icode.system.data.UserRole;
import cn.js.icode.system.service.UserRoleService;
import cn.js.icode.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.exception.BizException;
import team.bangbang.sso.data.DataLimit;

/**
 * API服务 - 操作账户
 * 
 * 今天六一，又是周六，天气挺好，24、5度，没有在家陪小孩，又来星火路加班了。
 * 
 * @author ICode Studio
 * @version 1.0 2019-06-01
 */
@RestController
@RequestMapping("/api/user")
@Api("操作账户API")
public class UserRestful {
	/**
	 * 得到指定的系统账户
	 *
	 * @param userId
	 *			指定的账户编号
	 * @return 系统账户
	 */
	@PostMapping("/getObject")
	@ApiOperation(value = "得到指定的系统账户", notes = "得到指定的系统账户", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "userId", value = "操作账户编号", required = true, dataType = "Long")
	})
	public ResponseBase getObject(Long userId) {
		if (userId == null || userId == 0L) {
			return ResponseBase.REQUEST_DATA_EXPECTED;
		}

		// 1. 查询操作用户
		User user = UserService.getObject(userId);
		if (user == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		
		// 脱敏：去除密码
		user.setPassword(null);

		// 2. 加载角色信息
		UserRole urWhere = new UserRole();
		urWhere.setUserId(user.getId());
		List<UserRole> urList = UserRoleService.list(urWhere, null, null);
		
		for(int i = 0; urList != null && i < urList.size(); i++) {
			UserRole ur = urList.get(i);
			if(ur == null || ur.getRoleId() == null) continue;
			
			user.getRoleIds().add(ur.getRoleId());
		}
		
		// 3. 数据查询的范围或者反范围
		DataLimit os = UserService.getDataLimit(userId);
		user.setDataLimit(os);

		// 返回数据
		DataResponse<User> rb = new DataResponse<User>();
		rb.setData(user);

		return rb;
	}
	
	/**
	 * 取得指定组织下（含下级组织）指定角色的用户列表
	 * 
	 * @param orgId  组织机构编号，可选，如果为null，则不进行组织机构的限定
	 * @param roleId 角色编号，可选，如果为null，则不进行角色的限定
	 * @return 指定组织下（含下级组织）指定角色的用户列表
	 */
	@PostMapping("/getUserList")
	// @Cacheable(value="Item", key="'ItemList:' + #category + ':' + #parentCode")
	@ApiOperation(value = "取得指定组织下（含下级组织）指定角色的用户列表", notes = "取得指定组织下（含下级组织）指定角色的用户列表", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "orgId", value = "组织机构编号，可选，如果为null，则不进行组织机构的限定", dataType = "Long"),
			@ApiImplicitParam(paramType = "query", name = "roleId", value = "角色编号，可选，如果为null，则不进行角色的限定", dataType = "Long") })
	public ResponseBase getUserList(Long orgId, Long roleId) {
		List<User> uList = null;
		try {
			uList = UserService.getUserList(orgId, roleId);
		} catch (BizException e) {
			e.printStackTrace();
		}

		if (uList == null || uList.size() == 0) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 返回数据
		DataResponse<List<User>> rb = new DataResponse<List<User>>();
		rb.setData(uList);

		return rb;
	}
	
	/**
	 * 获取指定账户管辖的组织机构节点编号集合，包括所有子节点
	 *
	 * @param userId 指定的账户编号
	 * @return  指定账户管辖的组织机构节点编号集合，包括所有子节点
	 */
	@PostMapping("/getPermissionOrganizationIds")
	@ApiOperation(value = "获得权限范围内的组织机构节点集合", notes = "获取指定账户权限范围内的组织机构节点编号集合，包括所有子节点", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "userId", value = "操作账户编号", required = true, dataType = "Long")
		})
	public ResponseBase getPermissionOrganizationIds(Long userId) {
		if (userId == null || userId == 0L) {
			return ResponseBase.REQUEST_DATA_EXPECTED;
		}

		// 查询商户信息
		Set<Long> poIds = UserService.getPermissionOrganizationIds(userId);

		// 返回数据
		DataResponse<Set<Long>> rb = new DataResponse<Set<Long>>();
		rb.setData(poIds);

		return rb;
	}
}