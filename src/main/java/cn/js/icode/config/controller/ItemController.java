package cn.js.icode.config.controller;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.StatusCode;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import cn.js.icode.config.data.Item;
import cn.js.icode.config.service.ItemService;
import team.bangbang.spring.parameter.EntityParam;
/**
 * 选项配置 - Controller
 *
 * @author ICode Studio
 * @version 1.0  2018-10-13
 */
@Controller
@RequestMapping("/config")
public class ItemController {
	/**
	 * 选项配置分类列表
	 *
	 * @return 分类列表页面
	 */
	@RequestMapping("/categorySelect.do")
	public ModelAndView doCategorySelect() {
		ModelAndView view = new ModelAndView("config/categorySelect");
		// 分类列表
		List<String> categoryList = ItemService.getCategoryList();
		
		view.addObject("categoryList", categoryList);
		return view;
	}
	
	/**
	 * 选项配置列表
	 *
	 * @param item 查询条件
	 * @param pagination 分页数据
	 *
	 * @return 列表页面
	 */
	@RequestMapping("/itemList.do")
	public ModelAndView doList(@EntityParam Item item, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("config/itemList");

		// 构造下拉框数据
		configDropdownList(item, view);
		
		// 是否传入了分类
		String category = item.getCategory();
		if(category != null && category.trim().length() != 0) {
			String appendix = null;
			if(item.getParentId() == null) {
				appendix = "ParentId is null";
			}
			
			List<Item> itemList = ItemService.list(item, appendix, pagination);
			
			// 统计符合条件的结果记录数量
			int recordCount = ItemService.count(item, null);
			pagination.setRecordCount(recordCount);
			
			view.addObject("itemList", itemList);
		}
		
		view.addObject("item", item);
		view.addObject("pagination", pagination);
		
		return view;
	}
	/**
	 * 新增页面显示
	 *
	 * @param item 预设定的数据，比如在指定的分类下新增记录
	 *
	 * @return 新增页面
	 */
	@GetMapping("/itemAdd.do")
	public ModelAndView doAdd(@EntityParam Item item) {
		ModelAndView view = new ModelAndView("config/itemAdd");
		
		// 构造下拉框数据
		configDropdownList(item, view);
		// 保存预设定的数据
		view.addObject("item", item);
		
		
		return view;
	}
	/**
	 * 新增页面数据提交
	 *
	 * @param item 选项配置
     * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/itemAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@EntityParam Item item, HttpServletRequest request) {
		// 是否存在重复记录？
		if(exist(item)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		item.setSonCount(0);
		
		int result = ItemService.insert(item);
		if(result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}
		
		// 更新父节点数量
		Long pId = item.getParentId();
		Item pIt = ItemService.getObject(pId);
		if(pId != null && pIt != null) {
			// 子节点数量
			Item where = new Item();
			where.setParentId(pId);
			int count = ItemService.count(where, null);
			
			pIt.setSonCount(count);
			
			ItemService.update(pIt, null, pIt);
		}
		
        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("新增选项配置");
        log.setBizData(item);
        request.setAttribute("log", log);
        
		return ResponseBase.SUCCESS;
	}
	/**
	 * 修改页面显示
	 *
	 * @param itemId 选项编号（关键字）
	 *
	 * @return 修改页面
	 */
	@GetMapping("/itemUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value="id") Long itemId) {
		ModelAndView view = new ModelAndView("config/itemUpdate");
		if(itemId == null || itemId == 0L) {
			return view;
		}
		// 查询条件
		Item where = new Item();
		where.setId(itemId);
		Item item = ItemService.getObject(where, null);
		
		if (item == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + itemId + "的选项配置");
			return common;
		}
		
		view.addObject("item", item);
		
		// 构造下拉框数据
		configDropdownList(item, view);
		return view;
	}
	/**
	 * 修改页面数据提交
	 *
	 * @param item 选项配置
     * @param request HTTP请求
	 *
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/itemUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@EntityParam Item item, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long itemId = item.getId();
		if(itemId == null || itemId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		// 是否存在重复记录？
		if(exist(item)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		// 第1个参数item，取关键字段item.itemId为条件
		// 第3个参数item，取item内关键字段以外其它属性数据
		int result = ItemService.update(item, null, item);
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("修改选项配置");
        log.setBizData(item);
        request.setAttribute("log", log);
		return ResponseBase.SUCCESS;
	}
	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 *
	 * @param itemId 选项编号（关键字）
     * @param request HTTP请求
	 *
	 * @return 删除结果
	 */
	@PostMapping(value = "/itemDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value="id") Long itemId, HttpServletRequest request) {
		if(itemId == null || itemId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}
        // 获取待删除的对象，用于日志记录
        Item item = ItemService.getObject(itemId);
        if(item == null) {
            return ResponseBase.DATA_NOT_FOUND;
        }
        
        // 如果有子节点，则不可删除
        if(item.getSonCount() > 0) {
        	ResponseBase rb = new ResponseBase(StatusCode.DATA_PROTECTED, "存在子项，不可删除！");
        	return rb;
        }
		// 限定条件
		Item where = new Item();
		where.setId(itemId);
		int result = ItemService.delete(where, null);
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}
		
		// 更新父节点数量
		Long pId = item.getParentId();
		Item pIt = ItemService.getObject(pId);
		if(pId != null && pIt != null) {
			// 子节点数量
			where = new Item();
			where.setParentId(pId);
			int count = ItemService.count(where, null);
			
			pIt.setSonCount(count);
			
			ItemService.update(pIt, null, pIt);
		}
		
        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("删除选项配置");
        log.setBizData(item);
        request.setAttribute("log", log);
		return ResponseBase.SUCCESS;
	}
	/**
	 * 展示页面
	 *
	 * @param itemId 选项编号（关键字）
	 *
	 * @return 展示页面
	 */
	@RequestMapping("/itemView.do")
	public ModelAndView doView(@RequestParam(value="id") Long itemId) {
		ModelAndView view = new ModelAndView("config/itemView");
		if(itemId == null || itemId == 0L) {
			return view;
		}
		
		// 查询条件
		Item where = new Item();
		where.setId(itemId);
		Item item = ItemService.getObject(where, null);
		
		if (item == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + itemId + "的选项配置");
			return common;
		}
		
		// 构造下拉框数据
		configDropdownList(item, view);
		
		view.addObject("item", item);
		return view;
	}
	
	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 *
	 * @param item 查询条件
	 * @param view 视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(Item item, ModelAndView view) {
		// 父路径列表
		List<Item> pathList = new ArrayList<Item>();
		while(true) {
			Item it = ItemService.getObject(item.getParentId());
			if(it == null) break;
			
			pathList.add(0, it);
			item = it;
		}
		// 添加分类
		Item catIt = new Item();
		catIt.setItemName(item.getCategory());
		pathList.add(0, catIt);
		
		view.addObject("pathList", pathList);
	}
	
	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 *
	 * @param temp
	 *            将要修改的选项配置POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(Item temp) {
		// 检查修改的选项配置是否有重复记录
		Item form = new Item();
		// 关键字限定条件
		String str = (temp.getId() == null ? null : "ItemId != " + temp.getId());
		// 其它信息限定条件
		// 父编号
		if(temp.getParentId() != null) {
			form.setParentId(temp.getParentId());
		} else {
			if(str == null) {
				str = "ParentId is null";
			} else {
				str += " and ParentId is null";
			}
		}

		// 所属分类
		form.setCategory(temp.getCategory());
		// 选项编码
		form.setItemCode(temp.getItemCode());

		return (ItemService.getObject(form, str) != null);
	}
}