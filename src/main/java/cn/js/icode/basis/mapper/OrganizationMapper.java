package cn.js.icode.basis.mapper;
import team.bangbang.common.sql.IMybatisMapper;
import org.apache.ibatis.annotations.Mapper;
import cn.js.icode.basis.data.Organization;
/**
 * 组织机构 - Mapper
 * 对应数据库表：basis_organization_base
 *
 * @author ICode Studio
 * @version 1.0  2018-09-23
 */
@Mapper
public interface OrganizationMapper extends IMybatisMapper<Organization> {
	/*****************************************************************************************
	 * 如果有部分字段的查询需求，请不要使用getObject()、list()，应按以下步骤操作：
	 * 1. 在下面自定义对应的方法；
	 *    注意配置的方法不要使用IMapper已有方法：getObject()、list()
	 * 2. 在相应的mapper.xml中配置SQL块。
	 *****************************************************************************************/
}