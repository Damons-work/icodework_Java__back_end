package team.bangbang.spring.db;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 数据源设置切面。
 *
 * 添加@Transactional注解，使用读写数据库，没有配置读写数据库的情况下使用默认数据库；
 *
 * 未添加@Transactional注解，使用只读数据库，没有配置只读数据库的情况下使用默认数据库。
 *
 * @author 帮帮组
 * @version 1.0 2017年8月30日
 */
@Aspect
@Component
public class DataSourceAspect {
	/* 日志对象 */
	private  final static Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);

	/**
	 * 定义读写分离的写注解
	 */
	@Pointcut("@annotation(team.bangbang.spring.db.WriteConnection)")
	public void write() {
	}
	
	/**
	 * 定义读写分离的读注解
	 */
	@Pointcut("@annotation(team.bangbang.spring.db.ReadConnection)")
	public void read() {
	}
	
	/**
	 * 定义读写分离的写注解
	 * 
	 * @param thisJoinPoint AOP切点
	 * 
	 * @return 返回值
	 */
	@Around("write()")
	public Object writeAround(ProceedingJoinPoint thisJoinPoint) {
		// 可以写
		DbContextHolder.setReadOnly(false);
		logger.debug("dataSource switch to: write");

		Object obj = null;
		try {
			obj = thisJoinPoint.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		// 恢复到只读
		DbContextHolder.setReadOnly(true);
		logger.debug("dataSource reset to: read");

		return obj;
	}
	
	/**
	 * 定义读写分离的读注解
	 * @param joinPoint AOP切点
	 */
	@Before("read()")
	public void readAround(JoinPoint joinPoint) {
		// 可以写
		DbContextHolder.setReadOnly(true);
		logger.debug("dataSource switch to: read");
	}
}
