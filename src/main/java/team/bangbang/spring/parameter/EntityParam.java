package team.bangbang.spring.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Request参数转Java实体的注解
 *
 * @author 帮帮组
 * @version 1.0  2018-05-07
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityParam {
}
