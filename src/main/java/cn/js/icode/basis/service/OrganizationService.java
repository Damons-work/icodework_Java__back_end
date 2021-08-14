package cn.js.icode.basis.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.js.icode.basis.data.Organization;
import cn.js.icode.basis.mapper.OrganizationMapper;
import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.common.utility.TreeNode;
import team.bangbang.common.utility.TreeUtil;

/**
 * 组织机构 - Service
 *
 * @author ICode Studio
 * @version 1.0 2018-09-23
 */
@Service
public final class OrganizationService {
	/* 组织机构（Organization）Mapper */
	@Resource
	private OrganizationMapper _organizationMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static OrganizationMapper organizationMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		organizationMapper = _organizationMapper;
	}

	/*
	 * 固定数据字典 类型{1：集团2：公司3：部门4：小组}
	 */
	private static final String[] typeFlags = { "集团", "公司", "部门", "小组" };

	/**
	 * 得到指定的组织机构
	 *
	 * @param organizationId 指定的组织机构编号
	 * @return 组织机构
	 */
	public static Organization getObject(Long organizationId) {
		if (organizationMapper == null) {
			return null;
		}
		// 参数校验
		if (organizationId == null || organizationId == 0L) {
			return null;
		}
		// 查询条件
		Organization form = new Organization();
		form.setId(organizationId);
		return organizationMapper.getObject(form, null);
	}

	/**
	 * 获得类型{1：集团2：公司3：部门4：小组}列表
	 *
	 * @return 类型{1：集团2：公司3：部门4：小组}列表
	 */
	public static List<KeyValue> getTypeList() {
		return CommonMPI.getDictionaryList(typeFlags);
	}

	/**
	 * 获得类型{1：集团2：公司3：部门4：小组}名称
	 *
	 * @return 类型{1：集团2：公司3：部门4：小组}名称
	 */
	public static String getTypeName(Integer typeFlag) {
		return CommonMPI.getDictionaryName(typeFlags, typeFlag);
	}

	/**
	 * 插入一条组织机构
	 *
	 * @param data 插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Organization data) {
		if (organizationMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		// 设置缺省值：有效标识
		if (data.getActiveFlag() == null) {
			data.setActiveFlag(false);
		}
		return organizationMapper.insert(data);
	}

	/**
	 * 删除组织机构
	 *
	 * @param where    删除条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Organization where, String appendix) {
		if (organizationMapper == null) {
			return 0;
		}
		return organizationMapper.delete(where, appendix);
	}

	/**
	 * 查询一条组织机构，并转化为相应的POJO对象
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Organization getObject(Organization where, String appendix) {
		if (organizationMapper == null) {
			return null;
		}
		return organizationMapper.getObject(where, appendix);
	}

	/**
	 * 修改组织机构
	 *
	 * @param where    更新条件，不能为null
	 * @param appendix 附加限定条件
	 * @param data     更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Organization where, String appendix, Organization data) {
		if (organizationMapper == null) {
			return 0;
		}
		return organizationMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条组织机构，并转化为相应的POJO对象列表
	 *
	 * @param where      更新条件，不能为null
	 * @param appendix   附加限定条件
	 * @param pagination 分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Organization> list(Organization where, String appendix, Pagination pagination) {
		if (organizationMapper == null) {
			return Collections.emptyList();
		}
		return organizationMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的组织机构数量
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Organization where, String appendix) {
		if (organizationMapper == null) {
			return 0;
		}
		return organizationMapper.count(where, appendix);
	}

	/* 系统组织机构中整棵树 */
	private static TreeNode root = null;
	/* 顶级的组织编号 */
	private static Long topId = null;
	/** 属性值 组织机构编码 */
	public final static String ORGANIZATION_CODE = "ORGANIZATION_CODE";
	/** 属性值 组织机构简称 */
	public final static String BRIEF_NAME = "BRIEF_NAME";
	/** 属性值 组织类别名称 */
	public final static String TYPE_FLAG = "TYPE_FLAG";
	/** 属性值 组织类别备注 */
	public final static String REMARK = "REMARK";

	/**
	 * 获得顶级的组织编号
	 *
	 * @return 顶级的组织编号
	 */
	public static Long getTopId() {
		if (topId != null) {
			return topId;
		}

		// 查询获得TopId
		Organization topOrg = getObject(new Organization(), "ParentId = 0 or ParentId is null");
		if (topOrg == null) {
			topId = 0L;
		} else {
			topId = topOrg.getId();
		}

		return topId;
	}

	/**
	 * 得到组织机构树
	 *
	 * @param blContainsInactive 是否包含被删除标记的组织机构
	 *
	 * @return 整棵组织机构树
	 */
	public static TreeNode getRoot(boolean blContainsInactive) {
		// 得到整棵组织机构树
		if (root == null) {
			// 构造组织机构树
			try {
				// 查询条件
				Organization form = new Organization();

				List<Organization> lstOrganization = list(form, null, null);

				// 把所有模块路径变为TreeNode列表
				List<TreeNode> lstNodes = toTreeNodeList(lstOrganization);

				// 把TreeNode列表梳理成树
				TreeNode[] trees = TreeUtil.toTrees(lstNodes);

				if (trees == null || trees.length == 0) {
					// 没有数据
					root = new TreeNode();
					root.setId(0L);
					root.setActiveFlag(true);
				} else if (trees.length == 1) {
					// 只有一棵树
					root = trees[0];					
				} else {
					// 有多棵树
					root = new TreeNode();
					root.setId(0L);
					root.setActiveFlag(true);
					root.addSons(trees);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if (root == null) {
			return new TreeNode();
		}

		// 不包含被删除的节点
		if (!blContainsInactive) {
			// 检查被复制的节点是否已经被删除
			Boolean bl = (Boolean) root.getActiveFlag();
			if (!bl.booleanValue()) {
				// 被复制的节点已经被删除
				return new TreeNode();
			}
		}

		// 复制当前顶级节点
		TreeNode tempRoot = root.cloneNode();

		// 从整棵组织机构树中克隆出现有的符合条件的组织机构
		copySonsTree(root, tempRoot, blContainsInactive);

		return tempRoot;
	}

	/**
	 * 此方法未使用缓存，请在业务系统考虑缓存和刷新机制
	 * 
	 * @param blContainsInactive 是否包含被删除标记的组织机构
	 * 
	 * @return 系统中所有节点集合
	 */
	public static Collection<Object> getAllIds(boolean blContainsInactive) {
		// 所有的节点都参与备选
		TreeNode tree = OrganizationService.getRoot(blContainsInactive);
		Collection<Object> ss = TreeUtil.getSelfAndSonIds(tree);
		
		return ss;
	}
	
	/**
	 * 刷新缓存中的组织机构树
	 */
	public static void refreshRoot() {
		root = null;
	}

	/**
	 * 把一个树节点插入到整棵树中
	 *
	 * @param tree 树节点
	 */
	public static void insertNode(TreeNode tree) {
		if (root == null) {
			root = tree;
			return;
		}

		// 遍历整棵树，找到它的父节点
		TreeNode father = TreeUtil.findNode(root, tree.getParentId());
		if (father == null) {
			return;
		}

		// 在父节点下添加该节点
		TreeNode[] temp = new TreeNode[1];
		temp[0] = tree;
		father.addSons(temp);
		// 将各子节点进行重新排序
		father.sortSons();
	}

	/**
	 * 从整棵树中找到一个树节点并更新之
	 *
	 * @param tree 树节点
	 */
	public static void updateTree(TreeNode tree) {
		if (root == null) {
			return;
		}

		// 遍历整棵树，找到它的原有节点
		TreeNode old = TreeUtil.findNode(root, tree.getId());
		if (old == null) {
			return;
		}

		tree.addSons(old.getSons());
		// 判断是否和原有的节点删除标志相同
		Boolean af1 = old.getActiveFlag();
		Boolean af2 = tree.getActiveFlag();
		if (!af1.equals(af2)) {
			setStatus(tree, af2);
		}

		Long oldPID = (Long) old.getParentId();
		if (oldPID == null || oldPID == 0L) {
			// 发现的old节点是root树的顶节点
			// 可能有多个节点的ParentId为空，即多棵树
			// root = tree;
			// return;
		}

		// 找到原有节点的父节点
		TreeNode oldFather = TreeUtil.findNode(root, oldPID);
		if (oldFather == null) {
			return;
		}
		// 删除原有子节点
		oldFather.removeSon(old);

		// 重新添加该节点
		insertNode(tree);
	}

	/**
	 * 从整棵树中找到指定的树节点，将该树节点下所有子节点的删除标记和该节点状态统一。
	 *
	 * @param tree 树节点
	 */
	public static void cleanStatus(TreeNode tree) {
		if (root == null) {
			return;
		}

		// 遍历整棵树，找到它的原有节点
		TreeNode old = TreeUtil.findNode(root, tree.getId());
		// 给定的树节点的删除标记
		Boolean bl = (Boolean) tree.getActiveFlag();
		// 刷新该节点及其下所有子节点的删除标记
		setStatus(old, bl);
	}

	/**
	 * 刷新给定的树节点及其下所有子节点的删除标记
	 *
	 * @param tree 给定的树节点
	 * @param bl   新的删除标记
	 */
	private static void setStatus(TreeNode tree, Boolean bl) {
		tree.setActiveFlag(bl);
		// 处理子节点
		if (tree.hasSon()) {
			TreeNode[] sons = tree.getSons();
			for (int i = 0; i < sons.length; i++) {
				setStatus(sons[i], bl);
			}
		}
	}

	/**
	 * 将整棵树的所有子节点Clone到另外一棵树下
	 *
	 * @param src                源树
	 * @param dest               目标树
	 * @param blContainsInactive 是否包含已被删除的节点
	 */
	private static void copySonsTree(TreeNode src, TreeNode dest, boolean blContainsInactive) {
		// 循环遍历子节点
		if (src.hasSon()) {
			TreeNode[] sons = src.getSons();
			for (int i = 0; i < sons.length; i++) {
				// 不包含被删除的节点
				if (!blContainsInactive) {
					// 检查被复制的节点是否已经被删除
					Boolean bl = (Boolean) sons[i].getActiveFlag();
					if (!bl.booleanValue()) {
						// 被复制的节点已经被删除
						continue;
					}
				}

				TreeNode[] temp = new TreeNode[1];
				temp[0] = sons[i].cloneNode();
				dest.addSons(temp);
				// 处理子节点
				copySonsTree(sons[i], temp[0], blContainsInactive);
			}
		}
	}

	/**
	 * 把组织机构列表中的每个组织机构元素转变为TreeNode对象。
	 *
	 * @param lstOrganization 组织机构列表
	 * @return TreeNode对象列表
	 */
	public static List<TreeNode> toTreeNodeList(List<Organization> lstOrganization) {
		List<TreeNode> lstNodes = new ArrayList<TreeNode>();
		for (int i = 0; i < lstOrganization.size(); i++) {
			Organization org = lstOrganization.get(i);
			lstNodes.add(toTreeNode(org));
		}

		return lstNodes;

	}

	/**
	 * 将一个组织机构形成一个组织机构树的节点
	 *
	 * @param org 组织机构
	 * @return 组织机构树的节点
	 */
	public static TreeNode toTreeNode(Organization org) {
		TreeNode node = new TreeNode();
		// 编号
		node.setId(org.getId());
		// 父编号
		node.setParentId(org.getParentId());
		// 名称
		node.setName(org.getOrganizationName());
		// 排序号
		node.setOrderBy(org.getOrganizationCode());
		// 其它属性
		// 组织机构编码
		node.setAttribute(ORGANIZATION_CODE, org.getOrganizationCode());
		// 组织机构简称
		node.setAttribute(BRIEF_NAME, org.getBriefName());
		// 组织类别标识{1：集团 2：公司 3：部门 4：项目部}
		node.setAttribute(TYPE_FLAG, org.getTypeFlag());

		// 是否有效
		node.setActiveFlag(org.getActiveFlag());

		return node;
	}

	/**
	 * 得到当前组织节点的名称（包含上级组织的名称路径，名称路径上不包括顶级集团节点）
	 *
	 * @param orgId orgId 组织机构节点的编号
	 * @return 组织节点的名称（包含上级组织的名称路径，名称路径上不包括顶级集团节点）
	 */
	public static String getFullName(Long orgId) {
		if (orgId == null || orgId == 0L) {
			return "";
		}

		// 得到完整的组织机构树，包含被删除的节点
		TreeNode tempRoot = getRoot(true);
		if (tempRoot == null) {
			return "";
		}

		// 查找当前节点
		TreeNode node = TreeUtil.findNode(tempRoot, orgId);
		if (node == null) {
			return "";
		}

		String name = node.getName();

		// 查找父节点
		while ((node = TreeUtil.findNode(tempRoot, node.getParentId())) != null) {
			// 是顶节点吗?
			Object pId = node.getParentId();
			if (pId == null || "".equals(pId)) {
				break;
			}

			// 不是顶节点
			name = node.getName() + "/" + name;
		}

		return name;
	}

	/**
	 * 去除重复的组织机构节点
	 *
	 * @param orgIds 组织机构节点数组
	 * @return 去除重复的组织机构节点
	 */
	public static Long[] removeDuplication(String[] orgIds) {
		if (orgIds == null)
			return null;

		List<Long> lstOrgIds = new ArrayList<Long>();
		for (int i = 0; i < orgIds.length; i++) {
			if (orgIds[i] != null) {
				lstOrgIds.add(LogicUtility.parseLong(orgIds[i], 0L));
			}
		}
		// 得到所有有效的节点组成的树
		TreeNode root = getRoot(false);

		// 获得节点所辖的树
		TreeNode[] tree = TreeUtil.filterWithoutAncestor(root, lstOrgIds);
		if (tree == null)
			return null;
		Long[] result = new Long[tree.length];
		for (int i = 0; i < tree.length; i++) {
			result[i] = (Long) tree[i].getId();
		}

		return result;
	}
}
