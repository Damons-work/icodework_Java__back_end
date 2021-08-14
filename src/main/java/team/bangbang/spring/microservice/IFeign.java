package team.bangbang.spring.microservice;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//import team.bangbang.common.data.Pagination;

//************************************************************************
//系统名称：WEB开发基础平台类库
//class名称：微服务Feign调用操作父接口

/**
 * 所有微服务的父接口
 *
 * @author 帮帮组
 * @version 1.0 2018-10-22
 */
//************************************************************************
public interface IFeign<T> {
    /**
     * 插入一条记录
     *
     * @param data 插入的数据，不能为null
     *
     * @return 1：成功 其它：失败
     */
	@PostMapping("/insert")
    int insert(@RequestBody T data);

    /**
     * 删除记录
     *
     * @param where    删除条件，不能为null
     *
     * @return 成功删除的记录数量
     */
	@PostMapping("/delete")
    int delete(@RequestBody T where);

    /**
     * 查询一条记录，并转化为相应的POJO对象
     *
     * @param where    查询条件，不能为null
     *
     * @return 返回结果记录，并转化为相应的POJO对象
     */
	@PostMapping("/getObject")
    T getObject(@RequestBody T where);

    /**
     * 根据ID编号修改记录
     *
     * @param where    更新条件，不能为null
     *
     * @return 成功修改的记录数量
     */
	@PostMapping("/update")
    int update(@RequestBody T where);

    /**
     * 查询多条记录，并转化为相应的POJO对象列表
     *
     * @param where      更新条件，不能为null
     *
     * @return 返回结果记录，并转化为相应的POJO对象列表
     */
	@PostMapping("/list")
    List<T> list(@RequestBody T where);

    /**
     * 获得符合条件的记录数量
     *
     * @param where    查询条件，不能为null
     *
     * @return 返回记录数量
     */
	@PostMapping("/count")
    int count(@RequestBody T where);
}
