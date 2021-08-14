package team.bangbang.spring.filter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import com.alibaba.druid.support.http.WebStatFilter;

/**
 * Druid执行统计过滤器
 *
 * @author Bangbang
 * @version 1.0  2021年1月25日
 */
@WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",
        initParams={
                @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*", description="忽略资源")
        })
public class DruidStatFilter extends WebStatFilter {
}
