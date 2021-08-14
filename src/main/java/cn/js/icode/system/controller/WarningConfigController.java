package cn.js.icode.system.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.js.icode.system.data.WarningConfig;
import cn.js.icode.system.service.WarningConfigService;
import team.bangbang.common.config.Config;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import team.bangbang.spring.parameter.EntityParam;

/**
 * 告警配置 - Controller
 * 
 * @author ICode Studio
 * @version 1.0  2019-11-08
 */
@Controller
@RequestMapping("/system")
public class WarningConfigController {

    /* 菜单层级：最多两层菜单 */
    private static final int menuLevel = 2;

    private static final String DRUID_URL = ".druid.url";

//    @Value("#{'${dbAlias}'.split(',')}")
//    private List<String> list;

	/**
	 * 告警配置列表
	 * 
	 * @param warningConfig 查询条件
	 * @param pagination 分页数据
	 * 
	 * @return 列表页面
	 */
	@RequestMapping("/warningConfigList.do")
	public ModelAndView doList(@EntityParam WarningConfig warningConfig, @EntityParam Pagination pagination) {

	    ModelAndView view = new ModelAndView("system/warningConfigList");

		// 构造下拉框数据
		configDropdownList(warningConfig, view);
		
		List<WarningConfig> warningConfigList = WarningConfigService.list(warningConfig, null, pagination);
		
		// 统计符合条件的结果记录数量
		int recordCount = WarningConfigService.count(warningConfig, null);		
		pagination.setRecordCount(recordCount);		
		
		view.addObject("warningConfig", warningConfig);
		view.addObject("warningConfigList", warningConfigList);
		view.addObject("pagination", pagination);
		return view;
	}

	/**
	 * 新增页面显示
	 * 
	 * @param warningConfig 预设定的数据，比如在指定的分类下新增记录
	 * 
	 * @return 新增页面
	 */
	@GetMapping("/warningConfigAdd.do")
	public ModelAndView doAdd(@EntityParam WarningConfig warningConfig) {
		ModelAndView view = new ModelAndView("system/warningConfigAdd");

		// 构造下拉框数据
		configDropdownList(warningConfig, view);

		// 保存预设定的数据
		view.addObject("warningConfig", warningConfig);

        return view;
	}
	
	/**
	 * 新增页面数据提交
	 * 
	 * @param warningConfig 告警配置
     * @param request HTTP请求
	 * 
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/warningConfigAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam WarningConfig warningConfig, HttpServletRequest request) {
		// 是否存在重复记录？
		if(exist(warningConfig)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		int result = WarningConfigService.insert(warningConfig);
		
		if(result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("新增告警配置");
        log.setBizData(warningConfig);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 修改页面显示
	 * 
	 * @param id 编号（关键字）
	 * 
	 * @return 修改页面
	 */
	@GetMapping("/warningConfigUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value="id") Long id) {
		ModelAndView view = new ModelAndView("system/warningConfigUpdate");

		if(id == null || id == 0L) {
			return view;
		}

		// 查询条件		
		WarningConfig warningConfig = WarningConfigService.getObject(id);

		if(warningConfig == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + id + "的告警配置");
			return common;
		}
			
		view.addObject("warningConfig", warningConfig);
		
		// 构造下拉框数据
		configDropdownList(warningConfig, view);
		
		return view;
	}
	
	/**
	 * 修改页面数据提交
	 * 
	 * @param warningConfig 告警配置
     * @param request HTTP请求
	 * 
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/warningConfigUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam WarningConfig warningConfig, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long id = warningConfig.getId();

		if(id == null || id == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 是否存在重复记录？
		if(exist(warningConfig)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		// 第1个参数warningConfig，取关键字段warningConfig.id为条件
		// 第3个参数warningConfig，取warningConfig内关键字段以外其它属性数据
		int result = WarningConfigService.update(warningConfig, null, warningConfig);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("修改告警配置");
        log.setBizData(warningConfig);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 * 
	 * @param id 编号（关键字）
     * @param request HTTP请求
	 * 
	 * @return 删除结果
	 */
	@PostMapping(value = "/warningConfigDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value="id") Long id, HttpServletRequest request) {

		if(id == null || id == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 获取待删除的对象，用于日志记录
        WarningConfig warningConfig = WarningConfigService.getObject(id);
        if(warningConfig == null) {
            return ResponseBase.DATA_NOT_FOUND;
        }

		// 限定条件
		WarningConfig where = new WarningConfig();
		where.setId(id);
		
		int result = WarningConfigService.delete(where, null);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("删除告警配置");
        log.setBizData(warningConfig);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 展示页面
	 * 
	 * @param id 编号（关键字）
	 * 
	 * @return 展示页面
	 */
	@RequestMapping("/warningConfigView.do")
	public ModelAndView doView(@RequestParam(value="id") Long id) {
		ModelAndView view = new ModelAndView("system/warningConfigView");

		if(id == null || id == 0L) {
			return view;
		}

		// 查询
		WarningConfig warningConfig = WarningConfigService.getObject(id);
		
		if(warningConfig == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + id + "的告警配置");
			return common;
		}

		view.addObject("warningConfig", warningConfig);
		
		return view;
	}

	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 * 
	 * @param warningConfig 查询条件
	 * @param view 视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(WarningConfig warningConfig, ModelAndView view) {
        List<String> dbAliasList = getDbAliasList();
        view.addObject("dbAliasList", dbAliasList);
    }

    private List<String> getDbAliasList() {
	    List<String> resultList = new ArrayList<>();
	    // 获取所有配置项的key集合
        Enumeration<String> enumeration = Config.keys();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            if (key.endsWith(DRUID_URL)) {
                int index = key.indexOf(DRUID_URL);
                resultList.add(key.substring(0, index));
            }
        }
	    return resultList;
    }

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 * 
	 * @param temp
	 *            将要修改的告警配置POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(WarningConfig temp) {
		// 检查修改的告警配置是否有重复记录
		WarningConfig form = new WarningConfig();
		
		// 关键字限定条件

		String str = (temp.getId() == null ? null : "Id != " + temp.getId());

		// 其它信息限定条件

		// 数据库配置序号
		form.setDbAlias(temp.getDbAlias());

		// 提醒信息格式（如：您有{COUNT}个XXX需要处理）
		form.setTip(temp.getTip());

		// 菜单项编号，关联system_menu_base.MenuId
		form.setMenuId(temp.getMenuId());

		// 统计查询的SQL语句
		form.setQuerySql(temp.getQuerySql());

		return (WarningConfigService.getObject(form, str) != null);
	}
}
