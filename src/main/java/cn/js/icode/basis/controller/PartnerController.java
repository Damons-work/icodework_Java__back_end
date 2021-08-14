package cn.js.icode.basis.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.js.icode.basis.data.Partner;
import cn.js.icode.basis.service.PartnerService;
import team.bangbang.spring.parameter.EntityParam;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import team.bangbang.common.sql.DbUtil;

/**
 * 商户信息 - Controller
 * 
 * @author ICode Studio
 * @version 1.0  2018-12-02
 */
@Controller
@RequestMapping("/basis")
public class PartnerController {
	/**
	 * 商户信息列表
	 * 
	 * @param partner 查询条件
	 * @param pagination 分页数据
	 * 
	 * @return 列表页面
	 */
	@RequestMapping("/partnerList.do")
	public ModelAndView doList(@EntityParam Partner partner, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("basis/partnerList");

		// 设置缺省值：是否有效
		// 默认查询有效记录
		if(partner.getActiveFlag() == null) {
			partner.setActiveFlag(true);
		}
	
		// 构造下拉框数据
		configDropdownList(partner, view);  
		
		List<Partner> partnerList = PartnerService.list(partner, null, pagination);		
		
		// 统计符合条件的结果记录数量
		int recordCount = PartnerService.count(partner, null);		
		pagination.setRecordCount(recordCount);		
		
		view.addObject("partner", partner);
		view.addObject("partnerList", partnerList);
		view.addObject("pagination", pagination);
				
		return view;
	}

	/**
	 * 商户信息选择
	 * 
	 * @param partner 查询条件
	 * @param pagination 分页数据
	 * 
	 * @return 选择页面
	 */
	@RequestMapping("/partnerSelect.do")
	public ModelAndView doSelect(@EntityParam Partner partner, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("basis/partnerSelect");

		// 查询有效记录
		partner.setActiveFlag(true);
	
		// 构造下拉框数据
		configDropdownList(partner, view);  
		
		List<Partner> partnerList = PartnerService.list(partner, null, pagination);		
		
		// 统计符合条件的结果记录数量
		int recordCount = PartnerService.count(partner, null);		
		pagination.setRecordCount(recordCount);		
		
		view.addObject("partner", partner);
		view.addObject("partnerList", partnerList);
		view.addObject("pagination", pagination);
				
		return view;
	}
	
	/**
	 * 新增页面显示
	 * 
	 * @param partner 预设定的数据，比如在指定的分类下新增记录
	 * 
	 * @return 新增页面
	 */
	@GetMapping("/partnerAdd.do")
	public ModelAndView doAdd(@EntityParam Partner partner) {
		ModelAndView view = new ModelAndView("basis/partnerAdd");
		
		// 默认新建有效的商户
		if(partner.getActiveFlag() == null) {
			partner.setActiveFlag(true);
		}

		// 构造下拉框数据
		configDropdownList(partner, view);
		
		// 保存预设定的数据
		view.addObject("partner", partner);

		return view;  
	}
	
	/**
	 * 新增页面数据提交
	 * 
	 * @param partner 商户信息
     * @param request HTTP请求
	 * 
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/partnerAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam Partner partner, HttpServletRequest request) {
		// 是否存在重复记录？
		if(exist(partner)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		
		// 创建时间
		partner.setCreateTime(new Date());

		int result = PartnerService.insert(partner);
		
		if(result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("新增商户信息");
        log.setBizData(partner);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 修改页面显示
	 * 
	 * @param partnerId 商户编号（关键字）
	 * 
	 * @return 修改页面
	 */
	@GetMapping("/partnerUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value="id") String partnerId) {
		ModelAndView view = new ModelAndView("basis/partnerUpdate");

		if(partnerId == null || partnerId.trim().length() == 0) {
			return view;
		}

		// 查询条件
		Partner where = new Partner();
		where.setId(partnerId);
		
		Partner partner = PartnerService.getObject(where, null);

		if(partner == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + partnerId + "的商户信息");
			return common;
		}
			
		view.addObject("partner", partner);
		
		// 构造下拉框数据
		configDropdownList(partner, view);
		
		return view;
	}
	
	/**
	 * 修改页面数据提交
	 * 
	 * @param partner 商户信息
     * @param request HTTP请求
	 * 
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/partnerUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam Partner partner, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		String partnerId = partner.getId();

		if(partnerId == null || partnerId.trim().length() == 0) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 是否存在重复记录？
		if(exist(partner)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		// 第1个参数partner，取关键字段partner.partnerId为条件
		// 第3个参数partner，取partner内关键字段以外其它属性数据
		int result = PartnerService.update(partner, null, partner);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("修改商户信息");
        log.setBizData(partner);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 * 
	 * @param partnerId 商户编号（关键字）
     * @param request HTTP请求
	 * 
	 * @return 删除结果
	 */
	@PostMapping(value = "/partnerDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value="id") String partnerId, HttpServletRequest request) {

		if(partnerId == null || partnerId.trim().length() == 0) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 获取待删除的对象，用于日志记录
        Partner partner = PartnerService.getObject(partnerId);
        if(partner == null) {
            return ResponseBase.DATA_NOT_FOUND;
        }

		// 限定条件
		Partner where = new Partner();
		where.setId(partnerId);
		
		Partner data = new Partner();
		// 是否有效取反
		data.setActiveFlag(!partner.getActiveFlag());
		
		int result = PartnerService.update(where, null, data);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType(partner.getActiveFlag() ? "禁用商户信息" : "恢复");
        log.setBizData(partner);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 展示页面
	 * 
	 * @param partnerId 商户编号（关键字）
	 * 
	 * @return 展示页面
	 */
	@RequestMapping("/partnerView.do")
	public ModelAndView doView(@RequestParam(value="id") String partnerId) {
		ModelAndView view = new ModelAndView("basis/partnerView");

		if(partnerId == null || partnerId.trim().length() == 0) {
			return view;
		}

		// 查询条件		
		Partner partner = PartnerService.getObject(partnerId);
		
		if(partner == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + partnerId + "的商户信息");
			return common;
		}

		view.addObject("partner", partner);
		
		return view;
	}

	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 * 
	 * @param partner 查询条件
	 * @param view 视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(Partner partner, ModelAndView view) {

	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 * 
	 * @param temp
	 *            将要修改的商户信息POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(Partner temp) {
		// 检查修改的商户信息是否有重复记录
		Partner form = new Partner();
		
		// 关键字限定条件

		String str = (temp.getId() == null ? null : "PartnerId != '"
				+ DbUtil.getDataString(temp.getId()) + "'");

		// 其它信息限定条件

		// 商户名称
		form.setPartnerName(temp.getPartnerName());

		return (PartnerService.getObject(form, str) != null);
	}
}
