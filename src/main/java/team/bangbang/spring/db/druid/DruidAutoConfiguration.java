package team.bangbang.spring.db.druid;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import team.bangbang.common.sql.SQLPool;

/**
 * Druid的数据源配置
 *
 * @author Bangbang
 * @version 1.0  2021年1月25日
 */
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class DruidAutoConfiguration {
	/**
	 * @return 数据源
	 */
    @Bean
    @Primary
    public DataSource dataSource() {
        try {
			return SQLPool.getDataSource();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        return null;
    }

    /**
     * 使用数据源创建jdbcTemplate
     * 
     * @param keyDataSource 数据源
     * @return jdbcTemplate对象
     */
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate keyJdbcTemplate(DataSource keyDataSource) {
        return new JdbcTemplate(keyDataSource);
    }
}
