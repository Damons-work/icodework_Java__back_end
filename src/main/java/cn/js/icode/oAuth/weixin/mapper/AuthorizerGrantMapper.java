package cn.js.icode.oAuth.weixin.mapper;

import org.apache.ibatis.annotations.Mapper;

import cn.js.icode.oAuth.weixin.data.AuthorizerGrant;
import team.bangbang.common.sql.IMybatisMapper;

/**
 * 公众号/小程序授权信息 - Mapper
 * 对应数据库表：weixin_authorizer_grant
 *
 * @author ICode Studio
 * @version 1.0  2018-07-01
 */
@Mapper
public interface AuthorizerGrantMapper extends IMybatisMapper<AuthorizerGrant> {
    /*****************************************************************************************
     * 如果有部分字段的查询需求，请不要使用getObject()、list()，应按以下步骤操作：
     * 1. 在下面自定义对应的方法；
     *    注意配置的方法不要使用IMapper已有方法：getObject()、list()
     * 2. 在相应的mapper.xml中配置SQL块。
     *****************************************************************************************/
}