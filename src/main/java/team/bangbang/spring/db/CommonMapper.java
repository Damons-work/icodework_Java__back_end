package team.bangbang.spring.db;

import org.apache.ibatis.annotations.Mapper;

/**
 * 共通 - Mapper
 *
 * @author 帮帮组
 * @version 1.0  2020-12-09
 */
@Mapper
public interface CommonMapper {

    /**
     * 针对MySQL自增长字段，获取最近插入的自增长字段值
     *
     * @return 最近插入的自增长字段值
     */
    int getLastInsertId();

    /**
     * 查询指定表 指定字段最大值
     *
     * @param tableName 数据库表名称
     * @param columnName 字段名称
     *
     * @return 指定字段最大值
     */
    int getMaxId(String tableName, String columnName);

    /**
     * 查询指定表 指定字段最大值
     *
     *
     * @return 指定字段最大值
     */
    int getCurrent();

    /**
     * 更新
     *
     *
     * @return 指定字段最大值
     */
     void updateCurrent(int id);

}
