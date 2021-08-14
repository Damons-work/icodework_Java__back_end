package cn.js.icode.tool.mapper;
import team.bangbang.common.sql.IMybatisMapper;
import org.apache.ibatis.annotations.Mapper;
import cn.js.icode.tool.data.Project;
/**
 * 工程 - Mapper
 * 对应数据库表：tool_project_base
 *
 * @author ICode Studio
 * @version 1.0  2018-10-08
 */
@Mapper
public interface ProjectMapper extends IMybatisMapper<Project> {
	/*****************************************************************************************
	 * 如果有部分字段的查询需求，请不要使用getObject()、list()，应按以下步骤操作：
	 * 1. 在下面自定义对应的方法；
	 *    注意配置的方法不要使用IMapper已有方法：getObject()、list()
	 * 2. 在相应的mapper.xml中配置SQL块。
	 *****************************************************************************************/
}