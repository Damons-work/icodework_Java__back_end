package team.bangbang.spring.parameter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import team.bangbang.common.data.util.DataShaper;
import team.bangbang.common.data.util.Transport;
import team.bangbang.sso.IAccountSSO;
import team.bangbang.sso.SSOContext;
import team.bangbang.sso.data.Account;

/**
 * 自定义解析器
 *
 * @author 南瑞信通
 * @version 1.0 2018-06-05
 */
public class CustomizedResolver implements HandlerMethodArgumentResolver {
	private static final String TEMP_REQUEST_MAP = "TEMP_REQUEST_MAP";

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#
	 * supportsParameter(org.springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean bl = parameter.hasParameterAnnotation(EntityParam.class)
				|| parameter.hasParameterAnnotation(SessionUser.class);
		return bl;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#
	 * resolveArgument(org.springframework.core.MethodParameter,
	 * org.springframework.web.method.support.ModelAndViewContainer,
	 * org.springframework.web.context.request.NativeWebRequest,
	 * org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		// 处理EntityParam注解
		// 获取参数注解
		EntityParam ea = parameter.getParameterAnnotation(EntityParam.class);
		if (ea != null) {
			// 获取变量的名称
			String name = parameter.getParameterName();
			if (name == null || name.trim().length() == 0) {
				return null;
			}

			// 获取方法参数的类型
			Class<?> cls = parameter.getParameterType();

			// 获取请求数据
			Map<String, Object> datas = getRequestMap(webRequest);

			return requestToObject(datas, name, cls);
		}

		// 处理SessionUser注解
		// 获取参数注解
		SessionUser su = parameter.getParameterAnnotation(SessionUser.class);
		if (su != null) {
			// 必须使用SSOFilter，然后才能使用SSOContext
			IAccountSSO accClt = SSOContext.getAccountSSO();
			Account user = null;
			if (accClt != null) {
				user = accClt.getAccount();
			}
			
			if (user == null) {
				user = new Account();
			}

			return user;
		}

		return null;
	}

	private Map<String, Object> getRequestMap(NativeWebRequest webRequest) throws IOException {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

		// 从Request中重新读取
		@SuppressWarnings("unchecked")
		Map<String, Object> datas = (Map<String, Object>) request.getAttribute(TEMP_REQUEST_MAP);

		if (datas != null) {
			return datas;
		}

		datas = new HashMap<String, Object>();

		// 上传文件、传递数据
		Transport.transport(request, datas);

		request.setAttribute(TEMP_REQUEST_MAP, datas);

		return datas;
	}

	/**
	 * 单纯文本参数表单提交的处理，将请求参数对应到当前Action的成员变量中
	 *
	 * @param map 页面数据，如果存在文件上传，则此处的Map中保存了文件上传后的File对象
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ParseException
	 */
	private Object requestToObject(Map<String, Object> map, String entityName, Class<?> cls)
			throws Exception {
		Object objReturn = cls.getDeclaredConstructor().newInstance();
		Set<String> keys = map.keySet();
		if (keys == null || keys.isEmpty()) {
			return objReturn;
		}

		Object objValue = null;
		for (String strKey : keys) {
			if (strKey.equals(entityName)) {
				// 直接返回这个值
				objReturn = DataShaper.toObject(cls, map.get(strKey));
				continue;
			}

			// 获取相关的参数
			if (strKey.startsWith(entityName + ".")) {
				// 页面没有传入该值
				objValue = map.get(strKey);
				if (objValue == null) {
					continue;
				}

				// objReturn : a
				// strKey : a.b.c

				int nIndex = strKey.indexOf(".");
				strKey = strKey.substring(nIndex + 1);

				// strKey : b.c
				try {
					DataShaper.executeExpression(objReturn, strKey, objValue);
				} catch (Exception e) {
				}
			}
		} // end for

		return objReturn;
	}
}
