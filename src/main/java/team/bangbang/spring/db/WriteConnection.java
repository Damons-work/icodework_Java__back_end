package team.bangbang.spring.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记读数据库连接，用于service层的方法标注
 *
 * 对于Service层涉及数据库操作的方法，标记 WriteConnection 注解表示使用读写数据库（即默认数据库）。
 *
 * 缺省为 ReadConnection，表示使用只读数据库。
 *
 * @author 帮帮组
 * @version 1.0 2017年8月30日
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WriteConnection {

}
