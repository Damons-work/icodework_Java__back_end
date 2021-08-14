package cn.js.icode.config.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import cn.js.icode.config.data.Item;
import team.bangbang.common.sql.IMybatisMapper;
/**
 * 选项配置 - Mapper
 * 对应数据库表：config_item_base
 *
 * @author ICode Studio
 * @version 1.0  2018-10-13
 */
@Mapper
public interface ItemMapper extends IMybatisMapper<Item> {
	/*****************************************************************************************
	 * 如果有部分字段的查询需求，请不要使用getObject()、list()，应按以下步骤操作：
	 * 1. 在下面自定义对应的方法；
	 *    注意配置的方法不要使用IMapper已有方法：getObject()、list()
	 * 2. 在相应的mapper.xml中配置SQL块。
	 *****************************************************************************************/
	
	/**
	 * @return 分类列表
	 */
	public List<String> getCategoryList();
}