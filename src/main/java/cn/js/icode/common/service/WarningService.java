package cn.js.icode.common.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.js.icode.basis.data.Organization;
import cn.js.icode.basis.service.OrganizationService;
import cn.js.icode.common.data.WarningMessage;
import cn.js.icode.system.data.Menu;
import cn.js.icode.system.data.MenuFunction;
import cn.js.icode.system.data.User;
import cn.js.icode.system.data.WarningConfig;
import cn.js.icode.system.service.MenuService;
import cn.js.icode.system.service.UserService;
import team.bangbang.common.sql.DbUtil;
import team.bangbang.common.sql.SQLHelper;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.common.utility.TreeNode;
import team.bangbang.common.utility.TreeUtil;

/**
 * <Description> <br>
 *
 * @author zhang.xianhao<br>
 * @version 1.0<br>
 * @CreateDate 2019/11/12 13:54 <br>
 * @see team.bangbang.common.service <br>
 * @since R9.0<br>
 */
@Service
public final class WarningService {

    private static Logger logger = LoggerFactory.getLogger(WarningService.class);

    @Autowired
    private Environment environment;


    /* 用户的审批任务数据权限过滤条件 */
    private static Pattern auditFilterPattern = Pattern
            .compile("\\{AUDIT_TASK_FILTER:BizFlag=\\d+\\}");

    /* 用户管理的组织过滤条件 */
    private static Pattern organizationFilterPattern = Pattern
            .compile("\\{IN_MY_ORGANIZATION:FieldName=[^\\}]+\\}");

    /* 用户可视下级条件判断 */
    private static Pattern ifHasRoleIdFilterPattern = Pattern
            .compile("\\<if hasRoleId=\"[^\\\">]+\\\">");

    /**
     * <Description> 从告警配置列表中取出有用户权限的告警配置并进行组装 <br>
     *
     * @param 
     * @return 
     * @author zhang.xianhao<br>
     * @version 1.0<br> 
     * @CreateDate 2019/11/12 14:13 <br>
     */
    public void getMessageList(List<WarningMessage> messageList, List<WarningConfig> warningConfigList, User user) {
        logger.debug("WarningService.getMessageList...start");
        for (int index = 0; (warningConfigList != null && index < warningConfigList.size()); index++) {
            WarningConfig config = warningConfigList.get(index);
            // 用户无菜单权限则不展示
            if (isHasRole(user, config)) {
                getMessageList(messageList, config, user);
            }
        }
    }

    /**
     * <Description> 将告警消息组装到列表 <br>
     *
     * @param
     * @return
     * @author zhang.xianhao<br>
     * @version 1.0<br>
     * @CreateDate 2019/11/12 14:18 <br>
     */
    private void getMessageList(List<WarningMessage> messageList, WarningConfig config, User user) {
        logger.debug("WarningService.getMessageList...start");
        String dbAlias = config.getDbAlias();
        SQLHelper sqlHelper = new SQLHelper(dbAlias);
        // 数据库资源连接
        Connection conn = sqlHelper.connection();
        /*======================================================================*/
        String sql = config.getQuerySql();
        // 替换所有半角space、换行符，为1个半角space
        sql = sql.replaceAll("\\s+", " ");
        // 替换当前操作用户的编号
        sql = sql.replaceAll("\\{CURRENT_USER_ID\\}", "" + user.getId());
        // 状态过滤条件
        sql = loadBizFlag(sql, user);
        // 替换当前操作用户管理的组织过滤条件
        sql = loadFieldName(sql, user);
        // 条件筛选
        sql = ifHasRoleIdSql(sql, user);
        // 当前用户组织编号
        Long orgId = user.getOrganizationId();
        sql = sql.replaceAll("\\{CURRENT_USER_ORG_ID\\}", orgId.toString());
        // 当前用组织的orgCode
        Organization organization = OrganizationService.getObject(orgId);
        String orgCode = organization.getOrganizationCode();
        sql = sql.replaceAll("\\{CURRENT_USER_ORG_CODE\\}", orgCode);
        /* ================================================================= */
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.createStatement();
            logger.debug("warningSql:" + sql);
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                WarningMessage warningMessage = new WarningMessage();
                int num = resultSet.getInt(1);
                // 数量小于1则不展示
                if (num <= 0) {
                    continue;
                }
                warningMessage.setMessageId(config.getId().toString());
                warningMessage.setCount(num);
                warningMessage.setTip(config.getTip());
                Menu menu = config.getMenu();
                if (menu != null) {
                    Menu messageMenu = new Menu();
                    messageMenu.setMenuName(menu.getMenuName());
                    messageMenu.setMenuUrl(menu.getMenuUrl());
                    warningMessage.setMenu(menu);
                }
                messageList.add(warningMessage);
            }
        } catch (SQLException e) {
            logger.error(e + ", the sql:" + sql);
        } finally {
            // 释放
            close(resultSet, statement, conn);
        }
    }

    /**
     * <Description> 释放连接资源 <br>
     *
     * @param
     * @return
     * @author zhang.xianhao<br>
     * @version 1.0<br>
     * @CreateDate 2019/12/2 15:21 <br>
     */
    private void close(ResultSet rs, Statement stat, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stat != null) {
                stat.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * <Description> 替换sql中if条件 <br>
     *
     * @param
     * @return
     * @author zhang.xianhao<br>
     * @version 1.0<br>
     * @CreateDate 2019/11/24 22:26 <br>
     */
    private String ifHasRoleIdSql(String sql, User user) {
        Matcher matcher = ifHasRoleIdFilterPattern.matcher(sql);
        // 开始位置0
        int start = 0;
        while (matcher.find(start)) {
            int ifOpenStart = matcher.start();
            int ifOpenEnd = matcher.end();

            String clip = sql.substring(ifOpenStart, ifOpenEnd);
            int conditionStart = clip.indexOf("=\"") + 2;
            int conditionEnd = clip.indexOf("\">");
            String roleIdStr = clip.substring(conditionStart, conditionEnd);
            long roleId = LogicUtility.parseLong(roleIdStr, 0);
            String conditionSql = "";

            int ifCloseStart = sql.indexOf("</if>", ifOpenEnd);
            int ifCloseEnd = ifCloseStart + 5;

            // 是否有else
            String afterIfStr = sql.substring(ifCloseEnd).trim();
            int end = ifCloseEnd;
            boolean hasElse = afterIfStr.startsWith("<else>");
            if(hasElse) {
                int elseCloseStart = afterIfStr.indexOf("</else>");
                conditionSql = afterIfStr.substring(5, elseCloseStart + 1);
                end = sql.indexOf("</else>", end) + 6;
            }

            // 有角色权限
            if (user.containsRole(roleId)) {
                conditionSql = sql.substring(ifOpenEnd, ifCloseStart);
            }
            else if (hasElse){
                int elseCloseStart = afterIfStr.indexOf("</else>");
                conditionSql = afterIfStr.substring(6, elseCloseStart);
            }
            String beforeConditionStr = sql.substring(0, ifOpenStart);
            String afterConditionStr = sql.substring(end + 1);
            sql = beforeConditionStr + " " + conditionSql + " " + afterConditionStr;
            // 遍历条件
            start = end;
        }
        return sql;
    }


    /* 组装状态参数 */
    private String loadBizFlag(String sql, User user) {
        Matcher auditMatcher = auditFilterPattern.matcher(sql);
        // 开始位置0
        int start = 0;
        while (auditMatcher.find(start)) {
            int nStart = auditMatcher.start();
            int nEnd = auditMatcher.end();

            start = nEnd;
            String clip = sql.substring(nStart, nEnd);
            // BizFlag的值
            int bizFlag = LogicUtility.parseInt(
                    clip.replaceAll("\\D+", ""), 0);
            if (bizFlag == 0) {
                continue;
            }

            Collection<Integer> bizFlags = new HashSet<Integer>();
            bizFlags.add(bizFlag);

            /*// 当前用户的审批任务数据权限过滤条件
            String appendix = Audit.getTaskFilter(sqlHelper,
                    user.getId(), bizFlags);
            if (appendix == null || appendix.trim().length() == 0) {
                appendix = " 1 = 1 ";
            }

            sql = sql.replaceAll(
                    "\\{AUDIT_TASK_FILTER:BizFlag=\\d+\\}", appendix);*/
        }
        return sql;
    }


    /* 组装fieldName */
    private String loadFieldName(String sql, User user) {
        Matcher matcher = organizationFilterPattern.matcher(sql);
        // 开始位置0
        int start = 0;
        if (user == null || user.getId() == null) {
            return sql;
        }
        Set<Long> orgIds = UserService.getPermissionOrganizationIds(user.getId());
        // 当前用户管理的组织机构编号
        while (orgIds != null && orgIds.size() > 0 && matcher.find(start)) {
            int nStart = matcher.start();
            int nEnd = matcher.end();

            start = nEnd;
            String clip = sql.substring(nStart, nEnd);
            // 获取[FieldName]
            nStart = clip.indexOf("=") + 1;
            nEnd = clip.indexOf("}", nStart);
            if (nStart < 5 || nEnd < nStart)
                continue;

            String fieldName = clip.substring(nStart, nEnd);

            // 当前用户管理的组织机构过滤条件
            String appendix = "("
                    + DbUtil
                    .getOrSQL(fieldName, orgIds.toArray())
                    + ")";
            if (appendix == null || appendix.trim().length() == 0) {
                appendix = " 1 = 1 ";
            }

            sql = sql.replaceAll(
                    "\\{IN_MY_ORGANIZATION:FieldName=[^\\}]+\\}",
                    appendix);
        }
        return sql;
    }

    /**
     * <Description> 判断用户是否有此菜单告警权限 <br>
     *
     * @param
     * @return
     * @author zhang.xianhao<br>
     * @version 1.0<br>
     * @CreateDate 2019/11/12 14:19 <br>
     */
    private boolean isHasRole(User user, WarningConfig warningConfig) {
        logger.debug("WarningService.isHasRole...start");
        if (warningConfig == null) {
            return false;
        }
        Set<Long> menuIds = UserService.getPermissionMenuIds(user.getRoleIds());
        if (menuIds.size() <= 0) {
            return false;
        }
        // 获得当前账户有权限操作的菜单树
        TreeNode[] menuTree = getMenuTree(menuIds);
        if (menuTree == null || menuTree.length == 0) {
            return false;
        }

        // 递归判断菜单是否有权限
        return isHasRoleRecursion(menuTree, warningConfig);
    }

    /**
     * <Description> 判断用户是否有此菜单告警权限 <br>
     *
     * @param
     * @return
     * @author zhang.xianhao<br>
     * @version 1.0<br>
     * @CreateDate 2019/11/12 14:20 <br>
     */
    private boolean isHasRoleRecursion(TreeNode[] menuTree, WarningConfig warningConfig) {
        logger.debug("WarningService.isHasRoleRecursion...start");
        boolean result = false;
        if (menuTree == null || menuTree.length == 0) {
            return false;
        }
        Long menuId = warningConfig.getMenuId();
        for (TreeNode treeNode : menuTree) {
            // 如果菜单编号相等，则说明有权限，结束递归
            if (menuId != null && menuId.equals(treeNode.getId())) {
                loadUrl(treeNode, warningConfig);
                return true;
            }
            if (treeNode.getSons() == null || treeNode.getSons().length <= 0) {
                continue;
            }
            result = isHasRoleRecursion(treeNode.getSons(), warningConfig);
            if (result) {
                return result;
            }
        }
        return result;
    }

    /* 装配路径 */
    private void loadUrl(TreeNode treeNode, WarningConfig warningConfig) {
        Menu menu =  (Menu) treeNode.getAttribute(MenuService.MENU_DATA);
        if (warningConfig.getMenu() == null) {
            return;
        }
        // 获取对应的路径
        String menuUrl = warningConfig.getMenu().getMenuUrl();
        String url = menuUrl;
        // 如果是相对路径则进行截取
        if (!StringUtils.isEmpty(menuUrl) && !menuUrl.startsWith("http")) {
            url = getUrl(menu, menuUrl);
        }
        warningConfig.getMenu().setMenuUrl(url);
    }

    /* 获取对应的相对路径 */
    private String getUrl(Menu menu, String url) {
        logger.debug("WarningService.getUrl...start");
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        List<MenuFunction> mfList = (menu == null ? null : menu.getFunctionList());
        for (int i = 0; (mfList != null && i < mfList.size()); i++){
            MenuFunction menuFunction = mfList.get(i);
            if (menuFunction == null || !url.endsWith(menuFunction.getFunctionUrl())) {
                continue;
            }
            return menuFunction.getFunctionUrl();
        }
        return "";
    }

    /* UserController中拷贝,获得当前账户有权限操作的菜单树 */
    private static TreeNode[] getMenuTree(Collection<Long> menuIds) {
        logger.debug("WarningService.getMenuTree...start");
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
}
