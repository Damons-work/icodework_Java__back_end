package cn.js.icode.basis.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.js.icode.basis.data.Organization;
import cn.js.icode.basis.service.OrganizationService;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.common.utility.SetUtility;
import team.bangbang.common.utility.TreeNode;
import team.bangbang.common.utility.TreeUtil;
import team.bangbang.spring.parameter.EntityParam;
import team.bangbang.spring.parameter.SessionUser;
import team.bangbang.sso.SSOContext;
import team.bangbang.sso.data.Account;
import team.bangbang.sso.data.DataLimit;

/**
 * 组织机构 - Controller
 *
 * @author ICode Studio
 * @version 1.0 2018-09-23
 */
@Controller
@RequestMapping("/basis")
public class OrganizationController {

	/**
	 * 组织机构列表
	 *
	 * @param organization 查询条件
	 * @param pagination   分页数据
	 *
	 * @return 列表页面
	 */
	@RequestMapping("/organizationList.do")
	public ModelAndView doList(@EntityParam Organization organization, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("basis/organizationList");
		// 设置缺省值：有效标识
		// 默认查询有效记录
		if (organization.getActiveFlag() == null) {
			organization.setActiveFlag(true);
		}
		if (organization.getParentId() != null && organization.getParentId() == 0L) {
			// 避免查询条件代入0
			organization.setParentId(null);
		}
		// 默认查询顶级节点下的组织
		Long pId = organization.getParentId();
		if (pId == null)
			pId = 0L;

		// 父级组织节点
		List<Organization> parents = new ArrayList<Organization>();

		TreeNode tn = null;
		TreeNode root = OrganizationService.getRoot(true);
		while (pId != null && (tn = TreeUtil.findNode(root, pId)) != null) {
			Organization pOrg = new Organization();
			pOrg.setId((Long) tn.getId());
			pOrg.setParentId((Long) tn.getParentId());
			pOrg.setOrganizationName(pOrg.getParentId() != null ? tn.getName() : "[顶级组织]");

			parents.add(0, pOrg);
			// 获取上一级组织
			pId = pOrg.getParentId();
		}

		view.addObject("parents", parents);

		// 构造下拉框数据
		configDropdownList(organization, view);
		// 如果ParentId没有传入，则查询父节点不存在的数据
		String appendix = null;
		if (organization.getParentId() == null) {
			appendix = "(ParentId is null or ParentId not in "
					+ "(select OrganizationId from basis_organization_base))";
		}
		List<Organization> organizationList = OrganizationService.list(organization, appendix, pagination);

		// 统计符合条件的结果记录数量
		int recordCount = OrganizationService.count(organization, appendix);
		pagination.setRecordCount(recordCount);

		view.addObject("organization", organization);
		view.addObject("organizationList", organizationList);
		view.addObject("pagination", pagination);
		return view;
	}

	/**
	 * 组织机构选择，选择有效的组织机构
	 * 
	 * @param user 当前操作账户
	 * @param organization 查询条件
	 * @parameter isPublic （可选，默认为false）
	 *    true: 选取所有组织机构节点并形成为树
	 *    false: 选取当前用户有权限的组织机构节点并形成为树。
	 * @param excludeIds   排除在外的节点ID（可选），包括子节点都会被排除掉
	 * @param pagination   分页数据
	 * 
	 * @return 选择页面
	 */
	@RequestMapping("/organizationSelect.do")
	public ModelAndView doSelect(@SessionUser Account user, @EntityParam Organization organization,
			Boolean isPublic, String excludeIds, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("basis/organizationSelect");

		// 有效标识
		// 查询有效记录
		organization.setActiveFlag(true);
		// 设置缺省值
		if (isPublic == null) isPublic = false;

		if (organization.getParentId() != null && organization.getParentId() == 0L) {
			// 避免查询条件代入0
			organization.setParentId(null);
		}
		// 默认查询顶级节点下的组织
		Long pId = organization.getParentId();
		if (pId == null)
			pId = 0L;
		
		// 系统中所有有效组织机构节点集合
		Set<Long> allIds = SetUtility.toType(OrganizationService.getAllIds(true), Long.class);
		
		// 备选的节点集合
		Set<Long> scopeIds = null;
		if (isPublic) {
			scopeIds = allIds;
		} else if (user != null) {
			// 当前账号数据权限
			DataLimit dl = SSOContext.getDataLimitSSO().getDataLimit(null);
			if (dl != null) {
				scopeIds = new HashSet<Long>();
				for (Object id : dl.getInScope()) {
					scopeIds.add((Long)id);
				}
			}
		}
		
		if (scopeIds == null) {
			scopeIds = new HashSet<Long>();
		}

		// 本次请求需要排除的节点集合
		Set<Long> eIds = getExcludeIds(excludeIds);
		
		// 从备选节点中排除需要排除的节点
		// 剩下的节点就是本次的查询范围
		Set<Long> inIds = SetUtility.remove(scopeIds, eIds);
		
		// 本次查询范围之外的节点集合
		Set<Long> notInIds = SetUtility.remove(allIds, inIds);

		// 如果ParentId没有传入，则查询父节点不存在的数据
		String appendix = null;
		// 选择 in 还是 not in ？
		boolean noData = false;
		if (inIds.size() < notInIds.size()) {
			if (inIds.size() == 0) {
				noData = true;
			} else {
				// in集合数量少
				StringBuffer sb = new StringBuffer();
				for(Long l : inIds) {
					if(sb.length() > 0) sb.append(",");
					sb.append(l);
				}
	
				appendix = "OrganizationId in (" + sb + ")";
			}
		} else {
			if (notInIds.size() == 0) {
				// 不作限制
			} else {
				// not in集合数量少
				StringBuffer sb = new StringBuffer();
				for(Long l : notInIds) {
					if(sb.length() > 0) sb.append(",");
					sb.append(l);
				}
	
				appendix = "OrganizationId not in (" + sb + ")";
			}
		}

		TreeNode root = OrganizationService.getRoot(true);
		if (organization.getParentId() == null) {
			// 查询第一级
			String appendix2 = null;
			if(isPublic) {
				appendix2 = "ParentId is null or ParentId not in "
					+ "(select OrganizationId from basis_organization_base)";
			} else {
				// 当前用户可以查看的最高层级节点
				TreeNode[] tops = TreeUtil.filterWithoutAncestor(root, inIds);
				
				StringBuffer sb = new StringBuffer();
				for(int i = 0; tops != null && i < tops.length; i++) {				
					if(sb.length() > 0) sb.append(",");
					sb.append(tops[i].getId());
				}
				
				if(sb.length() > 0) {
					appendix2 = "OrganizationId in (" + sb + ")";
				}
			}
			
			if (appendix == null || appendix.trim().length() == 0) {
				appendix = appendix2;
			} else if(appendix2 != null) {
				appendix = appendix2 + " and " + appendix;
			}
		}
		

		List<Organization> organizationList = Collections.emptyList();
		if (noData) {
			pagination.setRecordCount(0);
		} else {			
			organizationList = OrganizationService.list(organization, appendix, pagination);

			// 统计符合条件的结果记录数量
			int recordCount = OrganizationService.count(organization, appendix);
			pagination.setRecordCount(recordCount);
		}
		
		// 父级组织节点
		List<Organization> parents = new ArrayList<Organization>();

		TreeNode tn = null;
		while (pId != null && (tn = TreeUtil.findNode(root, pId)) != null) {
			Organization pOrg = new Organization();
			pOrg.setId((Long) tn.getId());
			pOrg.setParentId((Long) tn.getParentId());
			if(pOrg.getParentId() == null || !scopeIds.contains(pId)) {
				pOrg.setId(0L);
				pOrg.setParentId(null);
				pOrg.setOrganizationName("[顶级组织]");
				parents.add(0, pOrg);
				break;
			} else {
				pOrg.setOrganizationName(tn.getName());
				parents.add(0, pOrg);
			}
			
			// 获取上一级组织
			pId = pOrg.getParentId();
		}

		view.addObject("parents", parents);

		// 构造下拉框数据
		configDropdownList(organization, view);

		view.addObject("organization", organization);
		view.addObject("organizationList", organizationList);
		view.addObject("pagination", pagination);

		return view;
	}

	private Set<Long> getExcludeIds(String excludeIds) {
		if (excludeIds == null || excludeIds.trim().length() == 0) {
			return null;
		}
		
		// 组织树
		TreeNode tree = OrganizationService.getRoot(true);
		if (tree == null) {
			return null;
		}

		Set<Long> ids = new HashSet<Long>();
		String[] ss = excludeIds.split(",");
		for (int i = 0; ss != null && i < ss.length; i++) {
			long l = LogicUtility.parseLong(ss[i], 0L);
			if(l <= 0) continue;
			
			TreeNode node = TreeUtil.findNode(tree, l);
			if (node == null) {
				continue;
			}
			// 该节点及子节点ID
			Collection<Object> objs = TreeUtil.getSelfAndSonIds(node);
			if (objs == null || objs.isEmpty()) {
				continue;
			}
			
			for (Object obj : objs) {
				if (obj != null && obj instanceof Long) {
					ids.add((Long)obj);
				}
			}
		}
		
		return ids;
	}

	/**
	 * 新增页面显示
	 *
	 * @param organization 预设定的数据，比如在指定的分类下新增记录
	 *
	 * @return 新增页面
	 */
	@GetMapping("/organizationAdd.do")
	public ModelAndView doAdd(@EntityParam Organization organization) {
		ModelAndView view = new ModelAndView("basis/organizationAdd");
		// 构造下拉框数据
		configDropdownList(organization, view);

		// 默认新增有效地组织
		organization.setActiveFlag(true);

		// 父组织
		Long pId = organization.getParentId();
		Organization pOrg = OrganizationService.getObject(pId);
		if (pOrg == null) {
			pOrg = new Organization();
		}
		organization.setParent(pOrg);

		// 保存预设定的数据
		view.addObject("organization", organization);
		return view;
	}

	/**
	 * 新增页面数据提交
	 *
	 * @param organization 组织机构
	 * @param request      HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/organizationAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam Organization organization, HttpServletRequest request) {
		// 是否存在重复记录？
		if (exist(organization)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		int result = OrganizationService.insert(organization);
		if (result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}

		// 将该组织机构形成一个TreeNode插入到OrganizationMPI.root中。
		TreeNode tree = OrganizationService.toTreeNode(organization);
		OrganizationService.insertNode(tree);

		// TODO: 重新设定当前用户的可管理组织机构编号集合
		// updateAllIdsCover(organization);

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("新增组织机构");
		log.setBizData(organization);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}

	/**
	 * 修改页面显示
	 *
	 * @param organizationId 组织机构编号（关键字）
	 *
	 * @return 修改页面
	 */
	@GetMapping("/organizationUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value = "id") Long organizationId) {
		ModelAndView view = new ModelAndView("basis/organizationUpdate");
		if (organizationId == null || organizationId == 0L) {
			return view;
		}
		// 查询条件
		Organization where = new Organization();
		where.setId(organizationId);
		Organization organization = OrganizationService.getObject(where, null);

		if (organization == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + organizationId + "的组织机构");
			return common;
		}

		view.addObject("organization", organization);

		// 父组织
		Long pId = organization.getParentId();
		Organization pOrg = OrganizationService.getObject(pId);
		if (pOrg == null) {
			pOrg = new Organization();
		}
		organization.setParent(pOrg);

		// 构造下拉框数据
		configDropdownList(organization, view);
		return view;
	}

	/**
	 * 修改页面数据提交
	 *
	 * @param organization 组织机构
	 * @param request      HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/organizationUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam Organization organization, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long organizationId = organization.getId();
		if (organizationId == null || organizationId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 是否存在重复记录？
		if (exist(organization)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		// 第1个参数organization，取关键字段organization.organizationId为条件
		// 第3个参数organization，取organization内关键字段以外其它属性数据
		int result = OrganizationService.update(organization, null, organization);
		if (result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 修改OrganizationMPI.root中将该组织机构对应的TreeNode。
		TreeNode tree = OrganizationService.toTreeNode(organization);
		OrganizationService.updateTree(tree);

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("修改组织机构");
		log.setBizData(organization);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}

	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 *
	 * @param organizationId 组织机构编号（关键字）
	 * @param request        HTTP请求
	 *
	 * @return 删除结果
	 */
	@PostMapping(value = "/organizationDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value = "id") Long organizationId, HttpServletRequest request) {
		if (organizationId == null || organizationId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 获取待删除的对象，用于日志记录
		Organization organization = OrganizationService.getObject(organizationId);
		if (organization == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 限定条件
		Organization where = new Organization();
		where.setId(organizationId);
		int result = OrganizationService.delete(where, null);
		if (result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("删除账户");
		log.setBizData(organization);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}

	/**
	 * 展示页面
	 *
	 * @param organizationId 组织机构编号（关键字）
	 *
	 * @return 展示页面
	 */
	@RequestMapping("/organizationView.do")
	public ModelAndView doView(@RequestParam(value = "id") Long organizationId) {
		ModelAndView view = new ModelAndView("basis/organizationView");
		if (organizationId == null || organizationId == 0L) {
			return view;
		}
		// 查询条件
		Organization where = new Organization();
		where.setId(organizationId);
		Organization organization = OrganizationService.getObject(where, null);

		if (organization == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + organizationId + "的组织机构");
			return common;
		}

		// 父组织
		Long pId = organization.getParentId();
		Organization pOrg = OrganizationService.getObject(pId);
		if (pOrg == null) {
			pOrg = new Organization();
		}
		organization.setParent(pOrg);

		view.addObject("organization", organization);
		return view;
	}

	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 *
	 * @param organization 查询条件
	 * @param view         视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(Organization organization, ModelAndView view) {
		// 类型{1：集团2：公司3：部门4：小组}列表
		List<KeyValue> typeList = OrganizationService.getTypeList();
		view.addObject("typeList", typeList);
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 *
	 * @param temp 将要修改的组织机构POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(Organization temp) {
		// 检查修改的组织机构是否有重复记录
		Organization form = new Organization();
		// 关键字限定条件
		String str = (temp.getId() == null ? null : "OrganizationId != " + temp.getId());
		// 其它信息限定条件
		// 父组织编号，关联basis_organization_base.OrganizationId
		if (temp.getParentId() == null) {
			if (str == null)
				str = "ParentId is null";
			else
				str += " and ParentId is null";
		} else {
			// 参与限定
			form.setParentId(temp.getParentId());
		}

		// 组织名称
		form.setOrganizationName(temp.getOrganizationName());

		return (OrganizationService.getObject(form, str) != null);
	}
}