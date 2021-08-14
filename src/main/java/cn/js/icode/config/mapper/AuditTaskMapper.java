package cn.js.icode.config.mapper;

import team.bangbang.common.sql.IMybatisMapper;
import org.apache.ibatis.annotations.Mapper;
import cn.js.icode.config.data.AuditTask;

/**
 * 审批任务 - Mapper
 * 对应数据库表：config_audit_task
 * 
 * @author ICode Studio
 * @version 1.0  2018-10-31
 */
@Mapper
public interface AuditTaskMapper extends IMybatisMapper<AuditTask> {
	/**************************************************************************
	 * ！！除非设计、指导人员有特别说明，否则此处不得随意增加、修改、删除！！
	 * －－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
	 *
	 * 如确需添加自定义方法，相应的mapper.xml中应配置SQL块，注意3点：
	 * 
	 * 1. SQL块的id须与方法名保持一致；
	 * 
	 * 2. 方法中的参数添加@Param注解；
	 * 
	 * 3. SQL块中的参数对象名与@Param注解内名称一致。
	 * 
	 *************************************************************************/
}
