package cn.js.icode.api;

import java.util.Collection;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.js.icode.basis.data.Organization;
import cn.js.icode.basis.data.Partner;
import cn.js.icode.basis.service.OrganizationService;
import cn.js.icode.basis.service.PartnerService;
import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * API服务 - 基础数据
 *
 * @author ICode Studio
 * @version 1.0 2018-12-·5
 */
@RestController
@RequestMapping("/api/basis")
// @CacheConfig(cacheNames = "BasisRestful")
@Api("基础数据API")
public class BasisRestful {
	/**
	 * 获得当前所有有效的商户列表
	 * 
	 * @return 当前所有有效的商户列表
	 */
	@PostMapping("/getActivePartnerList")
	@ApiOperation(value = "获得当前所有有效的商户列表", notes = "所有有效的商户列表", httpMethod = "POST")
	public ResponseBase getActivePartnerList() {
		List<Partner> pList = PartnerService.getActivePartnerList();

		if (pList == null || pList.size() == 0) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 返回数据
		DataResponse<List<Partner>> rb = new DataResponse<List<Partner>>();
		rb.setData(pList);

		return rb;
	}

	/**
	 * 获得商户信息
	 *
	 * @param partnerId 商户编号，36位UUID
	 *
	 * @return 一个商户
	 */
	@PostMapping("/getPartner")
	@ApiOperation(value = "获得商户信息", notes = "获得指定商户编号对应的商户信息", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "partnerId", value = "商户编号，36位UUID", required = true, dataType = "String")
		})
	public ResponseBase getPartner(String partnerId) {
		if (partnerId == null || partnerId.trim().length() == 0) {
			return ResponseBase.REQUEST_DATA_EXPECTED;
		}

		// 查询商户信息
		Partner p = PartnerService.getObject(partnerId);

		// 返回数据
		DataResponse<Partner> rb = new DataResponse<Partner>();
		rb.setData(p);

		return rb;
	}

	/**
	 * 获得一个组织信息
	 *
	 * @param organizationId 组织编号
	 *
	 * @return 一个组织
	 */
	@PostMapping("/getOrganization")
	@ApiOperation(value = "获得组织信息", notes = "获得指定组织编号对应的组织信息", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "organizationId", value = "组织编号", required = true, dataType = "Long")
		})
	public ResponseBase getOrganization(Long organizationId) {
		if (organizationId == null || organizationId == 0) {
			return ResponseBase.REQUEST_DATA_EXPECTED;
		}

		// 查询组织信息
		Organization org = OrganizationService.getObject(organizationId);

		// 返回数据
		DataResponse<Organization> rb = new DataResponse<Organization>();
		rb.setData(org);

		return rb;
	}

	/**
	 * 得到当前组织节点的名称（包含上级组织的名称路径，名称路径上不包括顶级集团节点）
	 *
	 * @param organizationId 组织机构节点的编号
	 * @return 组织节点的名称（包含上级组织的名称路径，名称路径上不包括顶级集团节点）
	 */
	@PostMapping("/getFullName")
	@ApiOperation(value = "获得组织层次全名称", notes = "获得组织节点的名称（包含上级组织的名称路径，名称路径上不包括顶级集团节点）", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "organizationId", value = "组织编号", required = true, dataType = "Long")
		})
	public ResponseBase getFullName(Long organizationId) {
		if (organizationId == null || organizationId == 0) {
			return ResponseBase.REQUEST_DATA_EXPECTED;
		}

		// 查询组织信息
		String fName = OrganizationService.getFullName(organizationId);

		// 返回数据
		DataResponse<String> rb = new DataResponse<String>();
		rb.setData(fName);

		return rb;
	}
	
	/**
	 * 刷新缓存中的组织机构树
	 *
	 * @return 刷新结果
	 */
	@GetMapping("/getAllOrganizationIds")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name = "containsInactive", value = "是否包含被删除标记的组织机构", required = true, dataType = "Boolean")
	})
	public ResponseBase getAllOrganizationIds(boolean containsInactive) {
		// 刷新缓存中的组织机构树
		Collection<Object> objs = OrganizationService.getAllIds(containsInactive);
		
		// 返回数据
		DataResponse<Collection<Object>> rb = new DataResponse<Collection<Object>>();
		rb.setData(objs);
		
		// 返回数据
		return rb;
	}

	/**
	 * 刷新缓存中的组织机构树
	 *
	 * @return 刷新结果
	 */
	@RequestMapping("/refreshOrganizationRoot")
	@ApiOperation(value = "刷新组织信息", notes = "刷新缓存中的组织机构树", httpMethod = "GET")
	public ResponseBase refreshOrganizationRoot() {
		// 刷新缓存中的组织机构树
		OrganizationService.refreshRoot();

		// 返回数据
		return ResponseBase.SUCCESS;
	}
}