package team.bangbang.spring;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.config.Constants;
import team.bangbang.common.log.OperationLog;
import team.bangbang.sso.data.Account;

/**
 * 使用AOP记录请求日志
 *
 * @author 帮帮组
 * @version 1.0 2018-06-05
 */
@Aspect
@Component
public class WebLogAspect {
	/* 日志对象 */
	private  final static Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

	/**
	 * 定义AOP切面的对象方法
	 */
	@Pointcut("(execution (* com.nari..micro..*(..)))")
	public void webLog() {
	}

	/**
	 * 在AOP方法之前要执行的部分
	 * 
	 * @param joinPoint AOP切点
	 */
	@Before("webLog()")
	public void doBefore(JoinPoint joinPoint) {
	}

	/**
	 * 在AOP方法之后要执行的部分
	 * 
	 * @param res 返回值绑定的名称
	 */
	@AfterReturning(pointcut = "webLog()", returning = "res")
	public void doAfterReturning(Object res) {
		// 处理完请求，返回内容
		try {
			// 接收到请求，记录请求内容
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			if (null == attributes) {
				return;
			}
			HttpServletRequest request = attributes.getRequest();
			String requestIP = CommonMPI.getRequestIP(request);

			// 方法里面是否放置了日志数据？
			Object obj = request.getAttribute("log");
			if(obj != null && obj instanceof OperationLog) {
				OperationLog ol = (OperationLog)obj;
				// 日志中是否有账户名称？
				String userName = ol.getUserName();
				if(userName == null || userName.trim().length() == 0) {
					Account user = (Account)request.getAttribute(Constants.KEY_CURRENT_ACCOUNT);
					if(user != null) {
						ol.setUserName(user.getName() + "<" + requestIP + ">");
					} else {
						ol.setUserName("[未获取身份]<" + requestIP + ">");
					}
				}
				// 放置了日志数据
				logger.debug(JSONObject.toJSONString(ol));
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}
}
