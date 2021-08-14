package cn.js.icode.config.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.Pagination;
import cn.js.icode.config.data.Item;
import cn.js.icode.config.mapper.ItemMapper;

/**
 * 选项配置 - Service
 *
 * @author ICode Studio
 * @version 1.0 2018-10-13
 */
@Service
public final class ItemService {
	/* 选项配置（Item）Mapper */
	@Resource
	private ItemMapper _itemMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static ItemMapper itemMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		itemMapper = _itemMapper;
	}

	/**
	 * 得到指定的选项配置
	 *
	 * @param itemId 指定的选项编号
	 * @return 选项配置
	 */
	public static Item getObject(Long itemId) {
		if (itemMapper == null) {
			return null;
		}
		// 参数校验
		if (itemId == null || itemId == 0L) {
			return null;
		}
		// 查询条件
		Item form = new Item();
		form.setId(itemId);
		return itemMapper.getObject(form, null);
	}

	/**
	 * 插入一条选项配置
	 *
	 * @param data 插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Item data) {
		if (itemMapper == null) {
			return 0;
		}
		if (data.getId() == null) {
			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);
		}
		return itemMapper.insert(data);
	}

	/**
	 * 删除选项配置
	 *
	 * @param where    删除条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Item where, String appendix) {
		if (itemMapper == null) {
			return 0;
		}
		return itemMapper.delete(where, appendix);
	}

	/**
	 * 查询一条选项配置，并转化为相应的POJO对象
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Item getObject(Item where, String appendix) {
		if (itemMapper == null) {
			return null;
		}
		return itemMapper.getObject(where, appendix);
	}

	/**
	 * 修改选项配置
	 *
	 * @param where    更新条件，不能为null
	 * @param appendix 附加限定条件
	 * @param data     更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Item where, String appendix, Item data) {
		if (itemMapper == null) {
			return 0;
		}
		return itemMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条选项配置，并转化为相应的POJO对象列表
	 *
	 * @param where      更新条件，不能为null
	 * @param appendix   附加限定条件
	 * @param pagination 分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Item> list(Item where, String appendix, Pagination pagination) {
		if (itemMapper == null) {
			return Collections.emptyList();
		}
		return itemMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的选项配置数量
	 *
	 * @param where    查询条件，不能为null
	 * @param appendix 附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Item where, String appendix) {
		if (itemMapper == null) {
			return 0;
		}
		return itemMapper.count(where, appendix);
	}

	/**
	 * 获得下拉选项分类列表
	 * 
	 * @return 下拉选项分类列表
	 */
	public static List<String> getCategoryList() {
		if (itemMapper == null) {
			return Collections.emptyList();
		}

		return itemMapper.getCategoryList();
	}

	/**
	 * 获取选项列表
	 *
	 * @param category   所属分类
	 * @param parentCode 父级选项编码
	 *
	 * @return 获取选项列表
	 */
	public static List<Item> getItemList(String category, String parentCode) {
		if (category == null || category.trim().length() == 0) {
			return Collections.emptyList();
		}

		// 父级节点编号
		Long pId = null;
		if (parentCode != null && parentCode.trim().length() > 0) {
			// 查找父节点
			// 查询条件
			Item where = new Item();
			where.setCategory(category);
			where.setItemCode(parentCode);

			Item pIt = ItemService.getObject(where, null);

			if (pIt == null) {
				// 指定的父级节点不存在
				return Collections.emptyList();
			}

			pId = pIt.getId();
		}

		// 查询条件
		Item where = new Item();
		where.setCategory(category);
		where.setParentId(pId);
		// 附加限定条件
		String appendix = null;
		if (pId == null) {
			appendix = "ParentId is null";
		}

		List<Item> itemList = list(where, appendix, null);

		if (itemList == null) {
			return Collections.emptyList();
		}

		return itemList;
	}

	/**
	 * 获取一个选项
	 *
	 * @param category 所属分类，必填
	 * @param itemCode 选项编码，必填
	 *
	 * @return 一个选项
	 */
	public static Item getItem(String category, String itemCode) {
		return getItem(category, itemCode, null);
	}

	/**
	 * 通过选项编码或者选项名称获取一个选项
	 *
	 * @param category 所属分类，必填
	 * @param itemCode 选项编码，选项编码与选项名称条件二选一必填
	 * @param itemName 选项名称，选项编码与选项名称条件二选一必填
	 *
	 * @return 一个选项
	 */
	public static Item getItem(String category, String itemCode, String itemName) {
		if (category == null || category.trim().length() == 0) {
			return null;
		}

		if ((itemCode == null || itemCode.trim().length() == 0)
				&& (itemName == null || itemName.trim().length() == 0)) {
			return null;
		}

		// 查询条件
		Item where = new Item();
		where.setCategory(category);
		where.setItemCode(itemCode);
		where.setItemName(itemName);

		Item it = getObject(where, null);

		return it;
	}
}
