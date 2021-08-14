package cn.js.icode.system.service;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.file.ResourceLoader;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.common.utility.TreeNode;
import team.bangbang.common.utility.TreeUtil;
import cn.js.icode.system.data.Menu;
import cn.js.icode.system.data.MenuFunction;
import cn.js.icode.system.mapper.MenuMapper;
/**
 * 菜单项 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-09-14
 */
@Service
public final class MenuService {
    /** 图标的html class列表 */
    public static List<String> ICON_CLASS_LIST = null;
	/* 菜单项（Menu）Mapper */
	@Resource
	private MenuMapper _menuMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static MenuMapper menuMapper = null;
	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		menuMapper = _menuMapper;
	}
	/**
	 * 得到指定的菜单项
	 *
	 * @param menuId
	 *            指定的菜单项编号
	 * @return 菜单项
	 */
	public static Menu getObject(Long menuId) {
		if(menuMapper == null) {
			return null;
		}
		// 参数校验
		if(menuId == null || menuId == 0L) {
			return null;
		}
		// 查询条件
		Menu form = new Menu();
		form.setId(menuId);
		return menuMapper.getObject(form, null);
	}
	/**
	 * 插入一条菜单项
	 *
	 * @param data
	 *            插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Menu data) {
		if(menuMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		return menuMapper.insert(data);
	}
	/**
	 * 删除菜单项
	 *
	 * @param where
	 *            删除条件，不能为null
	 * @param appendix
	 *            附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Menu where, String appendix) {
		if(menuMapper == null) {
			return 0;
		}
		return menuMapper.delete(where, appendix);
	}
	/**
	 * 查询一条菜单项，并转化为相应的POJO对象
	 *
	 * @param where
	 *            查询条件，不能为null
	 * @param appendix
	 *            附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Menu getObject(Menu where, String appendix) {
		if(menuMapper == null) {
			return null;
		}
		return menuMapper.getObject(where, appendix);
	}
	/**
	 * 修改菜单项
	 *
	 * @param where
	 *            更新条件，不能为null
	 * @param appendix
	 *            附加限定条件
	 * @param data
	 *            更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Menu where, String appendix, Menu data) {
		if(menuMapper == null) {
			return 0;
		}
		return menuMapper.update(where, appendix, data);
	}
	/**
	 * 查询多条菜单项，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *            更新条件，不能为null
	 * @param appendix
	 *            附加限定条件
	 * @param pagination
	 *            分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Menu> list(Menu where, String appendix, Pagination pagination) {
		if(menuMapper == null) {
			return Collections.emptyList();
		}
		return menuMapper.list(where, appendix, pagination);
	}
	/**
	 * 获得符合条件的菜单项数量
	 *
	 * @param where
	 *            查询条件，不能为null
	 * @param appendix
	 *            附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Menu where, String appendix) {
		if(menuMapper == null) {
			return 0;
		}
		return menuMapper.count(where, appendix);
	}

	/* 系统中菜单项树 */
	private static TreeNode systemRoot = null;
	/* 权限类型{查看, 新增, 修改, 删除, 导入, 导出, 审核, 其它} */
	private final static String[] typeFlags = { "查看", "新增", "修改", "删除", "导入",
			"导出", "审核", "其它" };
	/** 图标文件的目录 */
	public final static String ICON_DIRECTORY = "/images/icons/";
	/** 属性值 菜单名称 */
	public final static String MENU_NAME = "MENU_NAME";
	/** 属性值 菜单地址 */
	public final static String MENU_URL = "MENU_URL";
	/** 属性值 图标文件 */
	public final static String ICON_IMAGE = "ICON_IMAGE";
	/** 属性值 菜单项本身数据 */
	public final static String MENU_DATA = "MENU_DATA";
	/** 权限序号位（与权限类型typeFlags对应）：所有权限 */
	public static final int PERMISSION_ALL = 0;
	/** 权限序号位（与权限类型typeFlags对应）：查看权限 */
	public static final int PERMISSION_VIEW = 1;
	/** 权限序号位（与权限类型typeFlags对应）：新增权限 */
	public static final int PERMISSION_ADD = 2;
	/** 权限序号位（与权限类型typeFlags对应）：修改权限 */
	public static final int PERMISSION_UPDATE = 3;
	/** 权限序号位（与权限类型typeFlags对应）：删除权限 */
	public static final int PERMISSION_DELETE = 4;
	/** 权限序号位（与权限类型typeFlags对应）：导入权限 */
	public static final int PERMISSION_IMPORT = 5;
	/** 权限序号位（与权限类型typeFlags对应）：导出权限 */
	public static final int PERMISSION_EXPORT = 6;
	/** 权限序号位（与权限类型typeFlags对应）：审核权限 */
	public static final int PERMISSION_AUDIT = 7;
	/** 权限序号位（与权限类型对应）：其它权限 */
	public static final int PERMISSION_OTHER = 8;

	/**
	 * 得到权限类型{查看, 新增, 修改, 删除, 导入, 导出, 审核, 其它}列表
	 *
	 * @return 权限类型{查看, 新增, 修改, 删除, 导入, 导出, 审核, 其它}列表
	 */
	public static List<KeyValue> getTypeList() {
		return CommonMPI.getDictionaryList(typeFlags);
	}

	/**
	 * 得到指定标识对应的权限类型{查看, 新增, 修改, 删除, 导入, 导出, 审核, 其它}名称
	 *
	 * @param typeFlag
	 *            权限类型{查看, 新增, 修改, 删除, 导入, 导出, 审核, 其它}
	 * @return 指定标识对应的权限类型{查看, 新增, 修改, 删除, 导入, 导出, 审核, 其它}名称
	 */
	public static String getTypeName(Integer typeFlag) {
		return CommonMPI.getDictionaryName(typeFlags, typeFlag);
	}

	/**
	 * 得到系统中菜单项树，真正的菜单项树是返回TreeNode对象的sons
	 *
	 * @return 整棵系统中菜单项树
	 */
	public synchronized static TreeNode getSystemTree() {
		if(systemRoot != null) {			
			// 为了不让页面的修改影响系统中菜单项树，复制当前树作为返回值
			TreeNode tempRoot = systemRoot.clone();
			
			return tempRoot;
		}

		// 得到整棵菜单项树
		systemRoot = new TreeNode();
		systemRoot.setId("");
		systemRoot.setParentId("");

		// 查询所有菜单项
		List<Menu> lstMenu = menuMapper.list(new Menu(), null, null);

		// 得到所有菜单下的功能
		// 查询所有菜单下的功能
		List<MenuFunction> lstMf = MenuFunctionService.list(new MenuFunction(), null, null);
		// 将菜单下的功能数据填写到菜单项数据中
		fillMenuFunction(lstMenu, lstMf);

		// 把所有模块菜单变为TreeNode列表
		List<TreeNode> lstNodes = toTreeNodeList(lstMenu);

		// 把TreeNode列表梳理成树
		TreeNode[] trees = TreeUtil.toTrees(lstNodes);

		systemRoot.addSons(trees);
		
		// 为了不让页面的修改影响系统中菜单项树，复制当前树作为返回值
		TreeNode tempRoot = systemRoot.clone();

		return tempRoot;
	}

	/**
	 * 将菜单下的功能数据填写到菜单项数据中
	 *
	 * @param lstM
	 *            菜单项列表
	 * @param lstMf
	 *            菜单下的功能列表
	 */
	private static void fillMenuFunction(List<Menu> lstM,
										 List<MenuFunction> lstMf) {
		if (lstM == null || lstM.size() == 0 || lstMf == null
				|| lstMf.size() == 0) {
			return;
		}

		for (Menu m : lstM) {
			List<MenuFunction> mfList = new ArrayList<MenuFunction>();
			m.setFunctionList(mfList);
			// 菜单编号
			Long menuId = m.getId();
			if (menuId == null || menuId == 0)
				continue;

			for (MenuFunction mf : lstMf) {
				if (!menuId.equals(mf.getMenuId())) {
					continue;
				}

				mfList.add(mf);
			}
		}
	}


	/**
	 * 把菜单项转变为TreeNode对象。
	 *
	 * @param menu
	 *            菜单项
	 * @return TreeNode对象
	 */
	public static TreeNode toTreeNode(Menu menu) {
		TreeNode node = new TreeNode();
		// 编号
		node.setId(menu.getId());
		// 父编号
		node.setParentId(menu.getParentId());
		// 排序号
		node.setOrderBy(menu.getOrderBy());
		// 菜单名称
		node.setName(menu.getMenuName());
		// 其它属性
		// 菜单地址
		node.setAttribute(MENU_URL, menu.getMenuUrl());
		// 图标文件
		node.setAttribute(ICON_IMAGE, menu.getIconImage());
		// 菜单项本身数据
		node.setAttribute(MENU_DATA, menu);

		return node;
	}

	/**
	 * 把菜单列表中的每个菜单元素转变为TreeNode对象。
	 *
	 * @param lstMenu
	 *            菜单列表
	 * @return TreeNode对象列表
	 */
	public static List<TreeNode> toTreeNodeList(List<Menu> lstMenu) {
		List<TreeNode> lstNodes = new ArrayList<TreeNode>();
		for (int i = 0; i < lstMenu.size(); i++) {
			Menu menu = lstMenu.get(i);

			TreeNode node = toTreeNode(menu);

			lstNodes.add(node);
		}

		return lstNodes;
	}



    /**
     * 获得当前应用中图标的html class数组
	 *
     * @return 图标文件的html class数组
     */
    public static List<String> getIconClasses() {
        if (ICON_CLASS_LIST == null || ICON_CLASS_LIST.isEmpty()) {
            ICON_CLASS_LIST = new ArrayList<String>();
            // 图标列表文件
            InputStream is = null;
            try {
                is = ResourceLoader.getInputStream(MenuService.class, "icon_classes.txt");

                byte[] data = LogicUtility.readInputStream(is);
                String s = new String(data, "UTF-8");

                String[] ss = s.split("[\n|\r]+");
                for (String temp : ss) {
                    if(temp == null) continue;
                    temp = temp.trim();
                    if(temp.length() == 0 || temp.startsWith("#")) continue;

                    ICON_CLASS_LIST.add(temp);
                }
            } catch (Exception ex) {
				ex.printStackTrace();
            } finally {
                if (is != null)
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
            }
        }

        return ICON_CLASS_LIST;
    }

	/**
	 * 刷新系统整体菜单项树
	 */
	public static void refreshSystemTree() {
		systemRoot = null;
	}

    /**
     * 获得指定菜单的编号及所有子节点的编号
     *
     * @param menuId 指定菜单的编号
     * @return 指定菜单的编号及所有子节点的编号，以半角逗号间隔。
     */
	public static String getSelfAndSonIds(long menuId) {
        TreeNode root = MenuService.getSystemTree();
        TreeNode tn = TreeUtil.findNode(root, menuId);
        Collection<Object> ids = TreeUtil.getSelfAndSonIds(tn);

        StringBuffer sb = new StringBuffer();
        if(ids != null) {
            for(Object obj : ids) {
                if(sb.length() > 0) sb.append(", ");
                sb.append(obj);
            }
        }

        return sb.toString();
    }
}
