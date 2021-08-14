package cn.js.icode.system.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.js.icode.basis.data.Organization;
import cn.js.icode.basis.service.OrganizationService;
import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.Pagination;
import cn.js.icode.system.data.UserOrganization;
import cn.js.icode.system.mapper.UserOrganizationMapper;

/**
 * 账户管理的组织 - Service
 *
 * @author ICode Studio
 * @version 1.0  2018-09-18
 */
@Service
public final class UserOrganizationService {
    /* 账户管理的组织（UserOrganization）Mapper */
    @Resource
    private UserOrganizationMapper _userOrganizationMapper = null;
    /* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
    private static UserOrganizationMapper userOrganizationMapper = null;

    @PostConstruct
    public void init() {
        // 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
        userOrganizationMapper = _userOrganizationMapper;
    }

    /**
     * 得到指定的账户管理的组织
     *
     * @param id 指定的编号
     * @return 账户管理的组织
     */
    public UserOrganization getObject(Long id) {
        if (userOrganizationMapper == null) {
            return null;
        }
        // 参数校验
        if (id == null || id == 0L) {
            return null;
        }
        // 查询条件
        UserOrganization form = new UserOrganization();
        form.setId(id);
        return userOrganizationMapper.getObject(form, null);
    }

    /**
     * 插入一条账户管理的组织
     *
     * @param data 插入的数据，不能为null
     * @return 1：成功 其它：失败
     */
    public static int insert(UserOrganization data) {
        if (userOrganizationMapper == null) {
            return 0;
        }
        if (data.getId() == null) {
            // 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
            long id = CommonMPI.generateSequenceId();
            data.setId(id);
        }
        return userOrganizationMapper.insert(data);
    }

    /**
     * 删除账户管理的组织
     *
     * @param where    删除条件，不能为null
     * @param appendix 附加限定条件
     * @return 成功删除的记录数量
     */
    public static int delete(UserOrganization where, String appendix) {
        if (userOrganizationMapper == null) {
            return 0;
        }
        return userOrganizationMapper.delete(where, appendix);
    }

    /**
     * 查询一条账户管理的组织，并转化为相应的POJO对象
     *
     * @param where    查询条件，不能为null
     * @param appendix 附加限定条件
     * @return 返回结果记录，并转化为相应的POJO对象
     */
    public static UserOrganization getObject(UserOrganization where, String appendix) {
        if (userOrganizationMapper == null) {
            return null;
        }
        return userOrganizationMapper.getObject(where, appendix);
    }

    /**
     * 修改账户管理的组织
     *
     * @param where    更新条件，不能为null
     * @param appendix 附加限定条件
     * @param data     更新数据，不能为null
     * @return 成功修改的记录数量
     */
    public static int update(UserOrganization where, String appendix, UserOrganization data) {
        if (userOrganizationMapper == null) {
            return 0;
        }
        return userOrganizationMapper.update(where, appendix, data);
    }

    /**
     * 查询多条账户管理的组织，并转化为相应的POJO对象列表
     *
     * @param where      更新条件，不能为null
     * @param appendix   附加限定条件
     * @param pagination 分页参数，如果分页参数为空，表示不分页
     * @return 返回结果记录，并转化为相应的POJO对象列表
     */
    public static List<UserOrganization> list(UserOrganization where, String appendix, Pagination pagination) {
        if (userOrganizationMapper == null) {
            return Collections.emptyList();
        }
        return userOrganizationMapper.list(where, appendix, pagination);
    }

    /**
     * 获得符合条件的账户管理的组织数量
     *
     * @param where    查询条件，不能为null
     * @param appendix 附加限定条件
     * @return 返回记录数量
     */
    public static int count(UserOrganization where, String appendix) {
        if (userOrganizationMapper == null) {
            return 0;
        }
        return userOrganizationMapper.count(where, appendix);
    }


    /**
     * 得到指定账户管理的组织机构列表
     *
     * @param userId 指定用户的编号
     * @return 指定账户管理的组织机构列表
     */
    public static List<Organization> getOrganizationList(Long userId) {
        if (userId == null || userId == 0L) {
            return Collections.emptyList();
        }

        // 查询账户管理的组织机构关系表
        UserOrganization uoWhere = new UserOrganization();
        uoWhere.setUserId(userId);

        List<UserOrganization> uoList = UserOrganizationService.list(uoWhere, null, null);
        if (uoList == null || uoList.size() == 0) {
            return Collections.emptyList();
        }

        // 角色编号
        StringBuffer sb = new StringBuffer();
        for (UserOrganization uo : uoList) {
            if (sb.length() > 0) sb.append(" or ");
            sb.append("OrganizationId = " + uo.getOrganizationId());
        }
        List<Organization> oList = OrganizationService.list(new Organization(), "(" + sb + ")", null);
        return oList;
    }
}
