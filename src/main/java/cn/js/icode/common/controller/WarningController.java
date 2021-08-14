package cn.js.icode.common.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.js.icode.common.data.WarningMessage;
import cn.js.icode.common.service.WarningService;
import cn.js.icode.system.data.User;
import cn.js.icode.system.data.WarningConfig;
import cn.js.icode.system.service.UserService;
import cn.js.icode.system.service.WarningConfigService;
import cn.weixin.component.service.AuthorizerServlet;
import team.bangbang.common.utility.LogicUtility;
import team.bangbang.spring.parameter.SessionUser;
import team.bangbang.sso.data.Account;

/**
 * 告警配置 - Controller
 *
 * @author ICode Studio
 * @version 1.0  2019-11-08
 */
@RestController
@RequestMapping("/common")
public class WarningController {

    private static Logger logger = LoggerFactory.getLogger(AuthorizerServlet.class);

    @Resource
    private WarningService warningService;

    /**
     * 展示页面
     *
     * @param
     * @return 展示页面
     */
    @RequestMapping("/warningView.do")
    public List<WarningMessage> doView(@SessionUser Account account) {
        logger.debug("WarningController.doView...start");
        long userId = LogicUtility.parseLong(account.getId(), 0L);
        if (userId == 0L) return Collections.emptyList();

        List<WarningMessage> resultList = new ArrayList<>();

        // 查询所有的告警配置项
        List<WarningConfig> warningConfigList = WarningConfigService.list(null, null, null);

        // 
        // 判断告警权限,并组装返回结果
        User user = UserService.getObject(userId);
        if (user == null) return Collections.emptyList();
        
        // 放入角色编号集合
        if (account.getRoleCodes() != null) {
        	for(String code : account.getRoleCodes()) {
        		long lng = LogicUtility.parseLong(code, 0L);
        		user.getRoleIds().add(lng);
        	}
        }
        
        warningService.getMessageList(resultList, warningConfigList, user);

        return resultList;
    }

}
