package cn.js.icode.system.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.alibaba.fastjson.JSONObject;

import cn.js.icode.basis.data.Organization;
import cn.js.icode.basis.data.Partner;
import cn.js.icode.basis.service.OrganizationService;
import cn.js.icode.basis.service.PartnerService;
import cn.js.icode.system.data.Menu;
import cn.js.icode.system.data.MenuFunction;
import cn.js.icode.system.data.Role;
import cn.js.icode.system.data.RoleMenu;
import cn.js.icode.system.data.User;
import cn.js.icode.system.data.UserOrganization;
import cn.js.icode.system.data.UserRole;
import cn.js.icode.system.service.MenuService;
import cn.js.icode.system.service.RoleMenuService;
import cn.js.icode.system.service.RoleService;
import cn.js.icode.system.service.UserOrganizationService;
import cn.js.icode.system.service.UserRoleService;
import cn.js.icode.system.service.UserService;
import team.bangbang.common.CommonMPI;
import team.bangbang.common.cipher.BangbangCipher;
import team.bangbang.common.config.Config;
import team.bangbang.common.config.Constants;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.StatusCode;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import team.bangbang.common.sql.DbUtil;
import team.bangbang.common.utility.CookieUtility;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.common.utility.TreeNode;
import team.bangbang.common.utility.TreeUtil;
import team.bangbang.spring.parameter.EntityParam;
import team.bangbang.spring.parameter.SessionUser;
import team.bangbang.sso.SSOContext;
import team.bangbang.sso.TokenBinder;
import team.bangbang.sso.data.Account;
import team.bangbang.sso.data.DataLimit;

/**
 * 系统账户 - Controller
 *
 * @author ICode Studio
 * @version 1.0 2018-09-18
 */
@Controller
@RequestMapping("/system")
public class UserController {
	/* 日志对象 */
	private static Logger logger = LoggerFactory.getLogger(UserController.class);
	/* 使用使用验证码，默认false */
	private static Boolean useValicationCode = null;

	/**
	 * 系统账户登录 - GET方式请求
	 *
	 * @param user 之前的登录账户信息
	 * @param response HTTP响应对象
	 *
	 * @return 检查是否存在登录信息，如果存在，则转向frame页面；否则显示登录logon页
	 */
	@GetMapping("/logon.do")
	public Object doLogon(@SessionUser Account user, HttpServletResponse response) {
		// 使用使用验证码，默认false
		if (useValicationCode == null) {
			String v = Config.getProperty("login.validation.code");
			if (v == null)
				v = "false";
			useValicationCode = "true".equals(v.trim().toLowerCase());
		}

		RedirectView mvFrame = new RedirectView(CommonMPI.getConsoleRoot() + "system/frame.do");

		// 检查当前是否登录了
		if (user != null && user.getId() != null) {
			return mvFrame;
		}

		ModelAndView mvLogon = new ModelAndView("system/logon");
		mvLogon.addObject("useValicationCode", useValicationCode);
		
		// 生成1个票据
		String token = (String) SSOContext.getToken();
		// 客户端使用Cookie保存token
		CookieUtility.addCookie(response, "token", token, -1);
		
		// 登录成功，转向到frame页面
		return mvLogon;
	}

	/**
	 * 系统账户登录 - Post方式请求
	 * 
	 * @param account  之前已经登录的系统账户
	 * @param user     当前登录表单提交过来的登录数据
	 * @param vCode    页面输入的验证码
	 * @param remember 是否记住身份
	 * @param request  HTTP请求对象
	 * @param response HTTP响应对象
	 *
	 * @return 登录成功，转向frame页面，否则停留在logon页面
	 */
	@PostMapping("/logon.do")
	@ResponseBody
	public ResponseBase doLogonAction(@SessionUser Account account, @EntityParam User user, String vCode, boolean remember,
			HttpServletRequest request, HttpServletResponse response) {
		// 使用使用验证码，默认false
		if (useValicationCode == null) {
			String v = Config.getProperty("login.validation.code");
			if (v == null)
				v = "false";
			useValicationCode = "true".equals(v.trim().toLowerCase());
		}

		if (account != null && account.getId() != null) {
			ResponseBase rb = new ResponseBase(StatusCode.DATA_DUPLICATE, "已经存在登录信息，刷新当前页面即可进入系统！");
			return rb;
		}

		if (user == null || user.getEmail() == null || user.getEmail().trim().length() == 0
				|| user.getPassword() == null || user.getPassword().trim().length() == 0) {
			// 第一次访问登录
			return ResponseBase.REQUEST_DATA_EXPECTED;
		}

		// 验证码校验
		if (useValicationCode) {
			// session中的验证码
			String letter = TokenBinder.getValidationCode(SSOContext.getToken());
			if (vCode == null)
				vCode = "";
			if (!vCode.trim().equalsIgnoreCase(letter)) {
				// 验证码不正确
				ResponseBase rb = new ResponseBase(StatusCode.AUTHENTICATION_SIGN_INVALID, "验证码错误！");
				return rb;
			}
		}

		// 处理登录
		// 密码MD5变换
		String pwd = user.getPassword();
		pwd = BangbangCipher.byte2hex(BangbangCipher.digest(pwd.getBytes(), BangbangCipher.MD5));

		user.setPassword(pwd);
		User unt = UserService.getObject(user, null);

		// 登录成功，记录日志
		JSONObject json = new JSONObject();

		// 检查返回的身份信息
		if (unt == null) {
			json.put("user", account.getAccountNo());
			json.put("type", "管理后台登录失败");
			json.put("requestIP", CommonMPI.getRequestIP(request));
			logger.error(json.toJSONString());

			return ResponseBase.DATA_NOT_MATCH;
		}

		// 账号已经被禁用
		if (!unt.getActiveFlag().booleanValue()) {
			json.put("user", unt.getUserName() + "(" + unt.getEmail() + ")");
			json.put("type", "管理后台登录成功，但账号禁用");
			json.put("requestIP", CommonMPI.getRequestIP(request));
			logger.error(json.toJSONString());
			return ResponseBase.DATA_STATUS_ERROR;
		}

		// 判断所在组织是否有效,无效提示被禁用
		if (!StringUtils.isEmpty(unt.getOrganizationId())) {
			Organization organization = OrganizationService.getObject(unt.getOrganizationId());
			if (organization == null) {
				json.put("user", unt.getUserName() + "(" + unt.getEmail() + ")");
				json.put("type", "贵单位组织数据不存在，您不能登录系统");
				json.put("requestIP", CommonMPI.getRequestIP(request));
				logger.error(json.toJSONString());
				ResponseBase rb = new ResponseBase(StatusCode.DATA_STATUS_ERROR, "贵单位已经被禁用，您不能登录系统！");
				return rb;
			}
			
			if (!organization.getActiveFlag()) {
				json.put("user", unt.getUserName() + "(" + unt.getEmail() + ")");
				json.put("type", "贵单位已经被禁用，您不能登录系统");
				json.put("requestIP", CommonMPI.getRequestIP(request));
				logger.error(json.toJSONString());
				ResponseBase rb = new ResponseBase(StatusCode.DATA_STATUS_ERROR, "贵单位已经被禁用，您不能登录系统！");
				return rb;
			}
			
			unt.setOrganization(organization);
		}

		// 登录成功删除验证码
		TokenBinder.deleteValidationCode(SSOContext.getToken());

		json.put("user", unt.getUserName() + "(" + unt.getEmail() + ")");
		json.put("type", "管理后台登录成功");
		json.put("requestIP", CommonMPI.getRequestIP(request));

		logger.info(json.toJSONString());

		// 登录成功，把登录的账号信息写入Cookie
		// 去除敏感字段
		unt.setPassword(null);

		// 具有的角色
		fillRoles(unt);

		// 将User转化为Account，保存起来
		Account acc = toAccount(unt);

		// 当前token
		String token = SSOContext.getToken();
		TokenBinder.saveAccount(token, acc);
		
		// 失效时间，单位是秒
		// -1表示存储在内存中，浏览器关闭即消失。
		int exp = (remember ? Integer.MAX_VALUE : -1);
		// 客户端使用Cookie保存token
		CookieUtility.addCookie(response, "token", token, exp);

		// 登录成功
		return ResponseBase.SUCCESS;
	}

	/**
	 * 将User对象转变为Account对象
	 * @param user User对象
	 * @return Account对象
	 */
	private Account toAccount(User user) {
		if (user == null) return null;
		
		Account acc = new Account();
		acc.setId(String.valueOf(user.getId()));
		acc.setAccountNo(user.getEmail());
		acc.setName(user.getUserName());
		acc.setWelcomePage(user.getWelcomePage());
		acc.setActiveFlag(user.getActiveFlag());
		Set<Long> roleIds = user.getRoleIds();
		Set<String> roleCodes = new HashSet<String>();
		if (roleIds != null && roleIds.size() > 0) {
			for (Long id : roleIds) {
				roleCodes.add(String.valueOf(id));
			}
		}
		acc.setRoleCodes(roleCodes);
		Organization org = user.getOrganization();
		if (org != null) {
			acc.setOrganizationCode(org.getOrganizationCode());
		}
		
		return acc;
	}

	/**
	 * 获取登录账户具有的角色，将角色信息填入账户信息内
	 *
	 * @param form 账户
	 */
	private void fillRoles(User form) {
		// 该账户的所有角色信息
		UserRole ur = new UserRole();
		ur.setUserId(form.getId());
		List<UserRole> urList = UserRoleService.list(ur, null, null);
		if (urList == null || urList.size() == 0) {
			return;
		}

		for (UserRole ur2 : urList) {
			form.getRoleIds().add(ur2.getRoleId());
		}
	}

	/**
	 * 系统内框架页面
	 *
	 * @param account 当前登录账户信息
	 * @param request  HTTP请求对象
	 * @param response HTTP响应对象
	 *
	 * @return 系统内框架页面
	 */
	@RequestMapping("/frame.do")
	public Object frame(@SessionUser Account account, HttpServletRequest request, HttpServletResponse response) {
		// 使用使用验证码，默认false
		if (useValicationCode == null) {
			String v = Config.getProperty("login.validation.code");
			if (v == null)
				v = "false";
			useValicationCode = "true".equals(v.trim().toLowerCase());
		}

		// forward模式
		ModelAndView mvFrame = new ModelAndView("system/frame");

		// 检查当前是否登录了
		if (account == null || account.getId() == null) {
			// 没有登录，跳转到登录页面
			RedirectView mvLogon = new RedirectView(CommonMPI.getConsoleRoot() + "system/logon.do");

			logger.error("0x01. 没有获得登录账户信息");

			return mvLogon;
		}

		// 将登录的账户信息带到页面上
		mvFrame.addObject("account", account);

		// 取得当前账户所有角色编码
		Collection<String> roleCodes = account.getRoleCodes();
		// 无角色
		if (roleCodes == null || roleCodes.size() == 0) {
			logger.error("0x02. 当前账户(" + account.getAccountNo() + ")无角色");

			// 无角色
			return mvFrame;
		}

		logger.info( "0x03. 当前账户(" + account.getAccountNo() + ")有 " + roleCodes.size() + " 个角色(包括直接权限角色)");

		StringBuffer sb = new StringBuffer();
		Set<Long> roleIds = new HashSet<Long>();
		for (String rId : roleCodes) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(rId);

			long roleId = LogicUtility.parseLong(rId, 0L);
			roleIds.add(roleId);
			// 保留当前操做账户登录后的欢迎页
			if (account.getWelcomePage() == null || account.getWelcomePage().trim().length() == 0) {
				Role r = RoleService.getObject(roleId);
				account.setWelcomePage(r != null ? r.getWelcomePage() : null);
			}
		}

		// 当前账户可以查看的菜单项节点
		Set<Long> menuIds = UserService.getPermissionMenuIds(roleIds);
		if (menuIds.size() == 0) {
			logger.error("0x04. 当前账户(" + account.getAccountNo() + ")无可访问的菜单项节点");
			// 无路径
			return mvFrame;
		}

		StringBuffer sb2 = new StringBuffer();
		for (Long l : menuIds) {
			if (sb2.length() > 0)
				sb2.append(",");
			sb2.append(l);
		}

		logger.info(
				"0x05. 当前账户(" + account.getAccountNo() + ")登录信息中有 " + menuIds.size() + " 个菜单项节点menuIds[" + sb2 + "]");

		// 获得当前账户有权限操作的菜单树
		TreeNode[] menuTree = getMenuTree(menuIds);
		if (menuTree == null || menuTree.length == 0) {
			logger.error("0x09. 当前账户(" + account.getAccountNo() + ")可访问的菜单项节点不存在");
			// 无路径
			return mvFrame;
		}

		logger.info( "0x10. 当前账户(" + account.getAccountNo() + ")可访问的菜单项节点有 " + menuTree.length + " 个");

		// 将系统管理的菜单分离出来
		TreeNode cap = new TreeNode();
		cap.setId("");
		cap.addSons(menuTree);

		// 复制一份，避免影响
		cap = cap.clone();

		// 菜单编号10000L：系统管理
		TreeNode setting = TreeUtil.findNode(cap, 10000L);
		if (setting != null && setting.hasSon()) {
			logger.info( "0x11. 当前账户(" + account.getAccountNo() + ")可以操作“系统管理”");
			mvFrame.addObject("setting", setting.getSons());
			TreeUtil.deleteNode(cap, setting);
		} else {
			logger.info( "0x12. 当前账户(" + account.getAccountNo() + ")无权限操作“系统管理”");
		}

		int sonCount = (cap.getSons() != null ? cap.getSons().length : 0);
		logger.info( "0x13. 当前账户(" + account.getAccountNo() + ")左侧菜单节点有 " + sonCount + " 项");

		// 生成菜单树的HTML
		String menuHtml = writeMenu(cap.getSons());

		// 将功能权限树传递到页面上
		mvFrame.addObject("menuHtml", menuHtml);

		// 得到当前用户在各菜单项上的功能权限
		List<RoleMenu> lstRoleMenu = RoleMenuService.list(new RoleMenu(), "RoleId in (" + sb.toString() + ")", null);

		Set<String> buff = new HashSet<String>();
		// 针对当前用户有权限操作的菜单树上每个节点，写入CRUD等权限
		fillPermission(menuTree, lstRoleMenu, null, buff);

		// 保存当前账户可以访问的所有菜单地址
		Map<String, Set<String>> functions = new HashMap<String, Set<String>>();
		functions.put(SSOContext.getApplicationId(), buff);
		account.setFunctions(functions);

		// 保存到缓存中
		TokenBinder.saveAccount(SSOContext.getToken(), account);

		// 登录成功，转向到frame页面
		return mvFrame;
	}

	private TreeNode[] getMenuTree(Collection<Long> menuIds) {
		if (menuIds == null || menuIds.size() == 0) {
			return new TreeNode[0];
		}

		// 得到数据库中完整的路径树，包含菜单项本身数据
		TreeNode temp = MenuService.getSystemTree();

		if (temp == null) {
			logger.error("0x06. MenuService.getSystemTree()返回的菜单为null");
		}

		if (temp != null && !temp.hasSon()) {
			logger.error("0x07. MenuService.getSystemTree()返回的菜单没有子节点");
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; temp.getSons() != null && i < temp.getSons().length; i++) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(temp.getSons()[i].getName());
		}

		logger.info(
				"0x08. MenuService.getSystemTree()返回的菜单有 " + temp.getSons().length + " 个子节点：" + sb);

		// 滤除不在允许范围内的节点，保留允许范围内节点的祖先节点
		temp = TreeUtil.filterWithAncestor(temp, menuIds);

		return temp.getSons();
	}

	/**
	 * 系统账户退出
	 *
	 * @param account    当前的登录账户信息
	 * @param request  HTTP请求对象
	 * @param response HTTP响应对象
	 *
	 * @return 转向logon页面
	 */
	@RequestMapping("/logout.do")
	public RedirectView logout(@SessionUser Account account, HttpServletRequest request, HttpServletResponse response) {
		// 删除session
		HttpSession hs = request.getSession();
		Enumeration<?> er = hs.getAttributeNames();

		while (er.hasMoreElements()) {
			String str = (String) er.nextElement();
			hs.removeAttribute(str);
		}

		// 使session失效
		hs.invalidate();

		// 清除所有Cookie
		Cookie cookies[] = request.getCookies();
		for (int i = 0; cookies != null && i < cookies.length; i++) {
			CookieUtility.addCookie(response, cookies[i].getName(), cookies[i].getValue(), 0);
		}

		if (account != null) {
			// 清除缓存
			String token = SSOContext.getToken();
			TokenBinder.deleteAccount(token);
			TokenBinder.deleteDataLimit(token);

			// 退出成功，记录日志
			JSONObject json = new JSONObject();
			json.put("user", account.getName() + "(" + account.getAccountNo() + ")");
			json.put("type", "管理后台退出成功");
			json.put("requestIP", CommonMPI.getRequestIP(request));
			logger.info(json.toJSONString());
		}

		RedirectView mvLogon = new RedirectView(CommonMPI.getConsoleRoot() + "system/frame.do");

		// 转向到logon页面
		return mvLogon;
	}

	/**
	 * 系统账户列表
	 *
	 * @param user       查询条件
	 * @param pagination 分页数据
	 * @param cUser      当前操作账户
	 *
	 * @return 列表页面
	 */
	@RequestMapping("/userList.do")
	public ModelAndView doList(@EntityParam User user, @EntityParam Pagination pagination, @SessionUser Account cUser) {
		ModelAndView view = new ModelAndView("system/userList");
		// 设置缺省值：有效标识
		// 默认查询有效记录
		if (user.getActiveFlag() == null) {
			user.setActiveFlag(true);
		}
		// 构造下拉框数据
		configDropdownList(user, view);

		String appendix = "";
		if (user.getOrganization() != null && user.getOrganization().getOrganizationName() != null
				&& user.getOrganization().getOrganizationName().trim().length() > 0) {
			appendix = "OrganizationId in (select OrganizationId from "
					+ "basis_organization_base where OrganizationName like '%"
					+ DbUtil.getDataString(user.getOrganization().getOrganizationName().trim()) + "%')";
		}

		// 当前是否是平台管理员
		boolean isPlatformAdmin = cUser.hasRoleCode("1001");
		view.addObject("isPlatformAdmin", isPlatformAdmin);
		List<User> userList = null;
		if (isPlatformAdmin) {
			userList = UserService.list(user, appendix, pagination);

			// 统计符合条件的结果记录数量
			int recordCount = UserService.count(user, appendix);
			pagination.setRecordCount(recordCount);
		} else {
			// 查询管理范围内的账户
			DataLimit os = SSOContext.getDataLimitSSO().getDataLimit(null);

			// 当前用户管理的组织机构节点
			String sqlWhere = os.toSQLWhere();

			/*
			 * SQLWhere规范： null：管辖范围为空，不查询任何数据 ""：长度为0的字符串，查询不作限定，可以访问任何数据 其它：in或者not
			 * in的sql，但不包括字段，如：in (2,3,4,5)
			 */
			if (sqlWhere == null) {
				userList = Collections.emptyList();
				// 统计符合条件的结果记录数量
				pagination.setRecordCount(0);
			} else if (sqlWhere.trim().length() == 0) {
				userList = UserService.list(user, appendix, pagination);
				// 统计符合条件的结果记录数量
				int recordCount = UserService.count(user, appendix);
				pagination.setRecordCount(recordCount);
			} else {
				if (appendix.length() > 0)
					appendix += " and ";
				appendix += " OrganizationId " + sqlWhere;
				userList = UserService.list(user, appendix, pagination);

				// 统计符合条件的结果记录数量
				int recordCount = UserService.count(user, appendix);
				pagination.setRecordCount(recordCount);
			}
		}

		// 填充组织机构、所属商户
		for (int i = 0; userList != null && i < userList.size(); i++) {
			User u = userList.get(i);
			Organization org = OrganizationService.getObject(u.getOrganizationId());
			u.setOrganization(org);

			Partner p = PartnerService.getObject(u.getPartnerId());
			u.setPartner(p);
		}

		view.addObject("user", user);
		view.addObject("userList", userList);
		view.addObject("pagination", pagination);
		return view;
	}

	/**
	 * 系统账户选择
	 * 
	 * @param user       查询条件
	 * @param pagination 分页数据
	 * 
	 * @return 选择页面
	 */
	@RequestMapping("/userSelect.do")
	public ModelAndView doSelect(@EntityParam User user, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("system/userSelect");

		// 设置缺省值：有效标识
		// 查询有效记录
		user.setActiveFlag(true);

		// 构造下拉框数据
		configDropdownList(user, view);

		List<User> userList = UserService.list(user, null, pagination);

		// 统计符合条件的结果记录数量
		int recordCount = UserService.count(user, null);
		pagination.setRecordCount(recordCount);

		// 填充组织机构
		for (int i = 0; userList != null && i < userList.size(); i++) {
			User u = userList.get(i);
			Organization org = OrganizationService.getObject(u.getOrganizationId());
			u.setOrganization(org);
		}

		view.addObject("user", user);
		view.addObject("userList", userList);
		view.addObject("pagination", pagination);

		return view;
	}

	/**
	 * 新增页面显示
	 *
	 * @param user  预设定的数据，比如在指定的分类下新增记录
	 * @param cUser 当前操作账户
	 *
	 * @return 新增页面
	 */
	@GetMapping("/userAdd.do")
	public ModelAndView doAdd(@EntityParam User user, @SessionUser Account cUser) {
		ModelAndView view = new ModelAndView("system/userAdd");
		// 构造下拉框数据
		configDropdownList(user, view);

		// 取得所有可供分配的角色列表
		List<Role> lstAllRole = getAvailableRoleList(cUser);

		// 产生可以在HTML字符串
		String roleForAdd = UserRoleService.getRoleStringForUserAdd(lstAllRole);

		// 保存预设定的数据
		view.addObject("roleForAdd", roleForAdd);

		// 默认添加有效的账号
		user.setActiveFlag(new Boolean(true));

		// 保存预设定的数据
		view.addObject("user", user);

		// 当前是否是平台管理员
		boolean isPlatformAdmin = cUser.hasRoleCode("1001");
		view.addObject("isPlatformAdmin", isPlatformAdmin);

		return view;
	}

	/**
	 * 新增页面数据提交
	 *
	 * @param user    系统账户
	 * @param request HTTP请求
	 * @param cUser   当前操作账户
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/userAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam User user, HttpServletRequest request, @SessionUser Account cUser) {
		// 是否存在重复记录？
		if (exist(user)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		// 密码变换
		String psd = user.getPassword();
		if (psd != null && psd.length() > 0) {
			psd = BangbangCipher.byte2hex(BangbangCipher.digest(psd.getBytes(), BangbangCipher.MD5));
			user.setPassword(psd);
		}

		int result = UserService.insert(user);
		if (result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}

		// 保存账户角色
		saveRoles(user, request, cUser);

		// 保存组织机构
		saveOrganization(user, request, cUser);

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("新增账户");
		log.setBizData(user);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}

	/**
	 * 修改页面显示
	 *
	 * @param userId 账户编号（关键字）
	 * @param cUser  当前操作账户
	 *
	 * @return 修改页面
	 */
	@GetMapping("/userUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value = "id") Long userId, @SessionUser Account cUser) {
		ModelAndView view = new ModelAndView("system/userUpdate");
		if (userId == null || userId == 0L) {
			return view;
		}

		// 查询条件
		User where = new User();
		where.setId(userId);
		User user = UserService.getObject(where, null);

		if (user == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + userId + "的系统账户");
			return common;
		}

		// 获取商户信息
		Partner p = PartnerService.getObject(user.getPartnerId());
		user.setPartner(p);

		// 所在组织
		user.setOrganization(OrganizationService.getObject(user.getOrganizationId()));

		view.addObject("user", user);

		// 管理的组织
		List<Organization> orgList = UserOrganizationService.getOrganizationList(userId);

		if (orgList != null)
			view.addObject("organizationList", orgList);

		// 构造下拉框数据
		configDropdownList(user, view);

		// 取得所有可供分配的角色列表
		List<Role> lstAllRole = getAvailableRoleList(cUser);

		// 当前账户具有的角色
		List<Role> lstCurrRoles = UserRoleService.getRoleList(userId);

		String roleForUpdate = UserRoleService.getRoleStringForUserUpdate(lstAllRole, lstCurrRoles);
		view.addObject("roleForUpdate", roleForUpdate);

		// 当前是否是平台管理员
		boolean isPlatformAdmin = cUser.hasRoleCode("1001");
		view.addObject("isPlatformAdmin", isPlatformAdmin);

		return view;
	}

	/**
	 * 修改页面数据提交
	 *
	 * @param user    系统账户
	 * @param request HTTP请求
	 * @param cUser   当前操作账户
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/userUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam User user, HttpServletRequest request, @SessionUser Account cUser) {
		// 为防止更新意外，必须传入id才能更新
		Long userId = user.getId();
		if (userId == null || userId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 是否存在重复记录？
		if (exist(user)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		// 密码变换
		String psd = user.getPassword();
		if (psd != null && psd.length() > 0) {
			psd = BangbangCipher.byte2hex(BangbangCipher.digest(psd.getBytes(), BangbangCipher.MD5));
			user.setPassword(psd);
		} else {
			// 密码留空，不改变
			user.setPassword(null);
		}

		// 第1个参数user，取关键字段user.userId为条件
		// 第3个参数user，取user内关键字段以外其它属性数据
		int result = UserService.update(user, null, user);
		if (result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 保存账户角色
		saveRoles(user, request, cUser);

		// 保存组织机构
		saveOrganization(user, request, cUser);

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("修改账户");
		log.setBizData(user);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}

	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 *
	 * @param userId  账户编号（关键字）
	 * @param request HTTP请求
	 *
	 * @return 删除结果
	 */
	@PostMapping(value = "/userDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value = "id") Long userId, HttpServletRequest request) {
		if (userId == null || userId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		if (userId == 100001L) {
			return ResponseBase.DATA_PROTECTED;
		}

		// 获取待删除的对象，用于日志记录
		User user = UserService.getObject(userId);
		if (user == null) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 限定条件
		User where = new User();
		where.setId(userId);
		int result = UserService.delete(where, null);
		if (result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 删除之前的账户角色关联关系
		UserRole urWhere = new UserRole();
		urWhere.setUserId(userId);
		UserRoleService.delete(urWhere, null);

		// 删除之前账户管理的组织机构
		UserOrganization uoWhere = new UserOrganization();
		uoWhere.setUserId(userId);
		UserOrganizationService.delete(uoWhere, null);

		// 记录日志
		OperationLog log = new OperationLog();
		log.setType("删除账户");
		log.setBizData(user);
		request.setAttribute("log", log);

		return ResponseBase.SUCCESS;
	}

	/**
	 * 展示页面
	 *
	 * @param userId 账户编号（关键字）
	 *
	 * @return 展示页面
	 */
	@RequestMapping("/userView.do")
	public ModelAndView doView(@RequestParam(value = "id") Long userId) {
		ModelAndView view = new ModelAndView("system/userView");
		if (userId == null || userId == 0L) {
			return view;
		}
		// 查询条件
		User where = new User();
		where.setId(userId);
		User user = UserService.getObject(where, null);

		if (user == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + userId + "的系统账户");
			return common;
		}

		// 获取商户信息
		Partner p = PartnerService.getObject(user.getPartnerId());
		user.setPartner(p);

		// 所在组织
		user.setOrganization(OrganizationService.getObject(user.getOrganizationId()));
		view.addObject("user", user);

		// 管理的组织
		List<Organization> orgList = UserOrganizationService.getOrganizationList(userId);

		if (orgList != null)
			view.addObject("organizationList", orgList);

		// 当前账户具有的角色
		List<Role> lstCurrRoles = UserRoleService.getRoleList(userId);

		String roleForView = getRoleForView(lstCurrRoles);

		view.addObject("roleForView", roleForView);

		return view;
	}

	/**
	 * 展示当前登录账户信息
	 * 
	 * @param cUser 当前操作账户
	 *
	 * @return 当前登录账户信息展示页面
	 */
	@RequestMapping("/myAccountView.do")
	public ModelAndView doViewMyAccount(@SessionUser Account account) {
		ModelAndView view = new ModelAndView("system/userView");
		
		long userId = LogicUtility.parseLong(account.getId(), 0L);
		
		User user = UserService.getObject(userId);
		if (user == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到当前账户信息，请重新登录");
			return common;
		}

		// 获取商户信息
		Partner p = PartnerService.getObject(user.getPartnerId());
		user.setPartner(p);

		// 所在组织
		user.setOrganization(OrganizationService.getObject(user.getOrganizationId()));
		view.addObject("user", user);

		// 管理的组织
		List<Organization> orgList = UserOrganizationService.getOrganizationList(user.getId());

		if (orgList != null)
			view.addObject("organizationList", orgList);

		// 当前账户具有的角色
		List<Role> lstCurrRoles = UserRoleService.getRoleList(user.getId());

		String roleForView = getRoleForView(lstCurrRoles);

		view.addObject("roleForView", roleForView);

		return view;
	}

	/**
	 * 修改密码页面
	 *
	 * @return 修改页面
	 */
	@GetMapping("/passwordUpdate.do")
	public ModelAndView doPasswordUpdate() {
		ModelAndView view = new ModelAndView("system/passwordUpdate");

		return view;
	}

	/**
	 * 修改密码数据提交
	 *
	 * @param user        系统账户
	 * @param oldPassword 当前密码
	 * @param password    新密码
	 *
	 * @return JSON格式的修改密码结果
	 */
	@PostMapping(value = "/passwordUpdate.do")
	@ResponseBody
	public ResponseBase doPasswordUpdateAction(@SessionUser Account account, String oldPassword, String password) {
		if (account == null || account.getId() == null) {
			return ResponseBase.AUTHENTICATION_IDENTITY_MISS;
		}

		// 新密码是否有值？
		if (oldPassword == null || oldPassword.length() == 0) {
			ResponseBase rb = new ResponseBase(999, "当前密码不能为空");
			return rb;
		}

		// 新密码是否有值？
		if (password == null || password.length() == 0) {
			ResponseBase rb = new ResponseBase(999, "新密码不能为空");
			return rb;
		}
		
		long userId = LogicUtility.parseLong(account.getId(), 0L);

		// 校验当前密码
		User unit = UserService.getObject(userId);

		oldPassword = BangbangCipher.byte2hex(BangbangCipher.digest(oldPassword.getBytes(), BangbangCipher.MD5));
		if (!unit.getPassword().equals(oldPassword)) {
			// 当前密码不匹配
			ResponseBase rb = new ResponseBase(999, "当前密码不正确");
			return rb;
		}

		// 更新密码
		User where = new User();
		where.setId(userId);

		password = BangbangCipher.byte2hex(BangbangCipher.digest(password.getBytes(), BangbangCipher.MD5));
		unit.setPassword(password);
		int nCount = UserService.update(where, null, unit);

		if (nCount <= 0) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		ResponseBase rb = new ResponseBase(StatusCode.SUCCESS, "密码修改成功");
		return rb;
	}

	/**
	 * 为指定账户设置直接权限
	 *
	 * @param user   查询条件
	 * @param userId 指定账户的编号
	 *
	 * @return 设置直接权限页面
	 */
	@GetMapping("/userMenuUpdate.do")
	public ModelAndView doUpdateUserMenu(@SessionUser Account cUser, Long userId) {
		// 当前是否是平台管理员
		boolean isPlatformAdmin = cUser.hasRoleCode("1001");
		if (!isPlatformAdmin) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "您无权操作直接权限！");
			return common;
		}

		ModelAndView view = new ModelAndView("system/userMenuUpdate");
		if (userId == null || userId == 0L) {
			return view;
		}

		// 获取账户信息
		User user = UserService.getObject(userId);
		if (user == null) {
			return view;
		}

		view.addObject("user", user);

		// 获得私有角色
		Role role = UserRoleService.getPrivateRole(userId);
		view.addObject("role", role);

		// 获得系统中所有的菜单项
		TreeNode root = MenuService.getSystemTree();
		if (root != null && root.hasSon()) {
			// 模块列表
			TreeNode[] modules = root.getSons();
			if (modules != null) {
				view.addObject("modules", modules);
			}
		}

		return view;
	}

	/**
	 * 指定账户设置直接权限数据提交
	 *
	 * @param user    查询条件
	 * @param role    角色
	 * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/userMenuUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateUserMenuAction(@SessionUser Account cUser, @EntityParam Role role,
			HttpServletRequest request) {
		// 当前是否是平台管理员
		boolean isPlatformAdmin = cUser.hasRoleCode("1001");
		if (!isPlatformAdmin) {
			ResponseBase rb = new ResponseBase(StatusCode.REQUEST_PERMISSION_DENIED, "您无权操作直接权限！");
			return rb;
		}

		// 为防止更新意外，必须传入id才能更新
		Long roleId = role.getId();
		if (roleId == null || roleId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 角色信息不需要修改

		// 保存角色路径
		RoleController.saveRoleMenus(role, request);

		return ResponseBase.SUCCESS;
	}

	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 *
	 * @param user 查询条件
	 * @param view 视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(User user, ModelAndView view) {
	}

	/**
	 * 获得指定账户可供分配的角色列表
	 *
	 * @param cUser 指定账户
	 * @return 可以访问的角色列表
	 */
	private List<Role> getAvailableRoleList(Account cUser) {
		// 取得所有角色
		List<Role> lstAllRole = RoleService.list(new Role(), "RoleName not like '_temp_%'", null);
		if (lstAllRole == null || lstAllRole.size() == 0) {
			return lstAllRole;
		}

		if (cUser.hasRoleCode("1001")) {
			// 平台管理员，可以操作所有角色
		} else if (cUser.hasRoleCode("1002")) {
			// 商户管理员，屏蔽平台管理员角色
			Role rle = new Role();
			rle.setId(1001L);
			lstAllRole.remove(rle);
		} else {
			// 其它角色，屏蔽平台管理员、商户管理员角色
			Role rle = new Role();
			rle.setId(1001L);
			lstAllRole.remove(rle);

			rle.setId(1002L);
			lstAllRole.remove(rle);
		}

		return lstAllRole;
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 *
	 * @param temp 将要修改的系统账户POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(User temp) {
		// 检查修改的系统账户是否有重复记录
		User form = new User();
		// 关键字限定条件
		String str = (temp.getId() == null ? null : "UserId != " + temp.getId());
		// 其它信息限定条件
		// 电子信箱
		form.setEmail(temp.getEmail());

		return (UserService.getObject(form, str) != null);
	}

	/**
	 * 保存账户角色
	 *
	 * @param temp    当前操作的账户对象
	 * @param request HTTP请求
	 * @param cUser   当前操作账户
	 */
	private void saveRoles(User temp, HttpServletRequest request, Account cUser) {
		// 插入账户角色
		String arr[] = request.getParameterValues("checkboxrole");

		// 删除之前的账户角色关联关系
		UserRole where = new UserRole();
		where.setUserId(temp.getId());
		UserRoleService.delete(where, null);

		// 当前账户可以操作的角色
		List<Role> rList = getAvailableRoleList(cUser);

		// 得到要插入UserRole的角色编号
		for (int i = 0; arr != null && i < arr.length; i++) {

			long rId = LogicUtility.parseLong(arr[i], 0L);
			Role r = new Role();
			r.setId(rId);
			if (rList == null || !rList.contains(r))
				continue;

			UserRole ur = new UserRole();
			ur.setUserId(temp.getId());
			ur.setRoleId(rId);
			UserRoleService.insert(ur);
		}
	}

	/**
	 * 保存账户可以管理的组织机构
	 *
	 * @param temp    当前操作的账户对象
	 * @param request HTTP请求
	 * @param cUser   当前操作账户
	 */
	private void saveOrganization(User temp, HttpServletRequest request, Account cUser) {
		// 删除之前账户管理的组织机构
		UserOrganization where = new UserOrganization();
		where.setUserId(temp.getId());
		UserOrganizationService.delete(where, null);

		Long topOrgId = OrganizationService.getTopId();
		if (temp.getId() == 100001L && topOrgId != null) {
			// 系统管理员，管理顶级节点组织即可
			UserOrganization uoTemp = new UserOrganization();
			uoTemp.setUserId(temp.getId());
			uoTemp.setOrganizationId(topOrgId);
			UserOrganizationService.insert(uoTemp);
			return;
		}

		String arr[] = request.getParameterValues("id");
		// 把ID重新梳理，去除重复的组织机构节点
		Long[] ids = OrganizationService.removeDuplication(arr);
		// 得到要插入UserRole的角色编号
		for (int i = 0; ids != null && i < ids.length; i++) {
			UserOrganization uoTemp = new UserOrganization();
			uoTemp.setUserId(temp.getId());
			uoTemp.setOrganizationId(ids[i]);
			UserOrganizationService.insert(uoTemp);
		}
	} // end saveOrganization()

	private String getRoleForView(List<Role> lstRole) {
		if (lstRole.size() == 0)
			return "（无角色）";

		StringBuffer buff = new StringBuffer();
		buff.append("\t\t\t  <table width=\"100%\" border=\"0\">\r\n");
		int i = 0;
		for (; i < lstRole.size(); i++) {
			Role temp = lstRole.get(i);
			// 私有角色除外
			if (temp.getRoleName() == null || temp.getRoleName().startsWith("_temp_")) {
				lstRole.remove(i--);
				continue;
			}
			if (i % 4 == 0)
				buff.append("\t\t\t\t<tr>\r\n");
			buff.append("\t\t\t\t  <td width=\"25%\">" + temp.getRoleName() + "</td>\r\n");
			if (i % 4 == 3)
				buff.append("\t\t\t\t</tr>\r\n");
		}

		if (i % 4 != 0) {
			for (; i % 4 != 0; i++) {
				buff.append("\t\t\t\t  <td width=\"25%\">&nbsp;</td>\r\n");
			}
			buff.append("\t\t\t\t</tr>\r\n");
		}

		buff.append("\t\t\t  </table>\r\n");
		return buff.toString();
	}

	/* 菜单层级：最多两层菜单 */
	private static final int menuLevel = 2;

	/**
	 * 将当前账户可操作的菜单写为html
	 * 
	 * @param menuTree 菜单树
	 * @return html菜单
	 */
	private String writeMenu(TreeNode[] menuTree) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; menuTree != null && i < menuTree.length; i++) {
			TreeNode tn = menuTree[i];
			sb.append("<li data-name=\"menu" + tn.getId() + "\" class=\"layui-nav-item\">\r\n");
			// 是否有子菜单
			boolean hasSon = tn.hasSon();
			// 图标
			String icon = (String) tn.getAttribute(MenuService.ICON_IMAGE);
			// 链接地址
			String url = (String) tn.getAttribute(MenuService.MENU_URL);
			// 如果有子菜单，则忽略当前节点的链接
			if (hasSon)
				url = null;
			/*
			 * <a href="javascript:;" lay-tips="应用" lay-direction="2"> <i
			 * class="layui-icon layui-icon-app"></i> <cite>应用</cite> </a>
			 */
			sb.append("<a href=\"javascript:;\"");
			if (url != null && url.length() > 0) {
				if (url.startsWith("-")) {
					sb.append(" href=\"" + url.substring(1) + "\" target=\"top\"");
				} else {
					sb.append(" lay-href=\"" + url + "\"");
				}
			}
			sb.append(" lay-tips=\"" + tn.getName() + "\" lay-direction=\"" + menuLevel + "\">\r\n");

			if (icon != null && icon.length() > 0) {
				sb.append("  <i class=\"layui-icon " + icon + "\"></i>\r\n");
			}

			sb.append("  <cite>" + tn.getName() + "</cite>\r\n");
			sb.append("</a>\r\n");

			// 处理子菜单
			if (hasSon) {
				sb.append(writeMenuChild(tn.getSons()));
			}

			sb.append("</li>").append(Constants.LINE_SEPARATOR);
		}

		return sb.toString();
	}

	private StringBuffer writeMenuChild(TreeNode[] sons) {
		StringBuffer sb = new StringBuffer();
		sb.append("    <dl class=\"layui-nav-child\">\r\n");
		/*
		 * <dd data-name="content"> <a href="javascript:;">内容系统</a> <dl
		 * class="layui-nav-child"> <dd data-name="list"><a
		 * lay-href="../views/app/content/list.html">文章列表</a></dd> <dd
		 * data-name="tags"><a lay-href="../views/app/content/tags.html">分类管理</a></dd>
		 * <dd data-name="comment"><a
		 * lay-href="../views/app/content/comment.html">评论管理</a></dd> </dl> </dd>
		 */
		for (int i = 0; sons != null && i < sons.length; i++) {
			TreeNode tn = sons[i];
			// 是否有子菜单
			boolean hasSon = tn.hasSon();
			// 图标
			// String icon = (String)tn.getAttribute(MenuService.ICON_IMAGE);
			// 链接地址
			String url = (String) tn.getAttribute(MenuService.MENU_URL);
			// 如果有子菜单，则忽略当前节点的链接
			if (hasSon)
				url = null;

			sb.append("        <dd data-name=\"menu" + tn.getId() + "\">\r\n");

			// if(icon != null && icon.length() > 0) {
			// sb.append(" <i class=\"layui-icon " + icon + "\"></i>\r\n");
			// }

			sb.append("        	<a");
			if (url != null && url.length() > 0) {
				if (url.startsWith("-")) {
					sb.append(" href=\"" + url.substring(1) + "\" target=\"top\"");
				} else {
					sb.append(" lay-href=\"" + url + "\"");
				}
			}
			sb.append(">" + tn.getName() + "</a>\r\n");

			// 处理子菜单
			if (hasSon) {
				sb.append(writeMenuChild(tn.getSons()));
			}

			sb.append("        </dd>\r\n");
		}

		sb.append("    </dl>\r\n");
		return sb;
	}

	/**
	 * 针对当前用户有权限操作的菜单树上每个节点，写入CRUD等权限
	 * 
	 * @param menuTree2   有权限的菜单项树(多个)
	 * @param lstRoleMenu 权限设置列表（包括增删改查全的权限设置）
	 * @param parent      父级节点使用的权限设置
	 * @param buff        存储有权限的访问地址
	 */
	private void fillPermission(TreeNode[] pt, List<RoleMenu> lst, RoleMenu parent, Set<String> buff) {
		if (pt == null || pt.length == 0) {
			return;
		}

		for (int i = 0; i < pt.length; i++) {
			// 得到权限设置列表中当前叶子的权限设置
			RoleMenu rpCurrent = getLeafRoleMenu(pt[i], lst);
			// 叠加父节点的功能定义
			combine(rpCurrent, parent);

			// 当前节点放入权限设置
			pt[i].setAttribute("PERMISSION", rpCurrent);

			// 当前菜单的URL
			String mUrl = (String) pt[i].getAttribute(MenuService.MENU_URL);
			if (mUrl != null && mUrl.trim().length() > 0) {
				// 对应team.bangbang.common.spring.filter.PermissionFilter
				// 所有*Select.ext、*Frame.ext结尾的请求都不需要验证
				int nIndex = mUrl.lastIndexOf(".");
				String strTemp = mUrl;
				if (nIndex > 0) {
					strTemp = strTemp.substring(0, nIndex);
				}
				if (strTemp.endsWith("Select") || strTemp.endsWith("Frame")) {
					buff.add(mUrl.trim());
				}
			}

			// 获得当前节点上可以操作的路径信息
			Menu m = (Menu) pt[i].getAttribute(MenuService.MENU_DATA);
			List<MenuFunction> mfList = (m == null ? null : m.getFunctionList());

			getPermission(mfList, rpCurrent, buff);

			// 处理子节点
			if (pt[i].hasSon()) {
				fillPermission(pt[i].getSons(), lst, rpCurrent, buff);
			}
		} // end for
	}

	private void getPermission(List<MenuFunction> mfList, RoleMenu rp, Set<String> buff) {
		if (mfList == null || mfList.size() == 0 || rp == null) {
			return;
		}

		for (MenuFunction mf : mfList) {
			// 权限类型
			Integer typeFlag = mf.getTypeFlag();
			// 权限
			Integer permission = rp.getPermissionFlag();

			if (typeFlag == null || permission == null)
				continue;

			if (rp.getCan(0) || rp.getCan(typeFlag.intValue())) {
				buff.add(mf.getFunctionUrl());
			}
		}

		return;
	}

	private void combine(RoleMenu rpCurrent, RoleMenu parent) {
		if (rpCurrent == null || parent == null) {
			return;
		}

		// 当前节点的权限标识
		Integer nC = rpCurrent.getPermissionFlag();
		int n1 = (nC == null) ? 0 : nC.intValue();
		// 父节点的权限标识
		Integer nP = parent.getPermissionFlag();
		int n2 = (nP == null) ? 0 : nP.intValue();

		// 叠加权限
		rpCurrent.setPermissionFlag(new Integer(n1 | n2));
	}

	/**
	 * @param pt  叶子
	 * @param lst 权限设置列表
	 * @return 得到权限设置列表中指定叶子的权限设置，如果没有设置， 则新建一个RoleMenu对象， 并把所有权限设置置为false后返回
	 */
	private RoleMenu getLeafRoleMenu(TreeNode pt, List<RoleMenu> lst) {
		for (int i = 0; lst != null && i < lst.size(); i++) {
			RoleMenu rp = lst.get(i);
			if (rp.getMenuId().equals(pt.getId())) {
				return rp;
			}
		}

		return new RoleMenu();
	}
}