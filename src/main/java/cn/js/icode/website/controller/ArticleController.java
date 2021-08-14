package cn.js.icode.website.controller;

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

import cn.js.icode.config.data.Item;
import cn.js.icode.config.service.ItemService;
import cn.js.icode.website.data.Article;
import cn.js.icode.website.service.ArticleService;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.log.OperationLog;
import team.bangbang.spring.parameter.EntityParam;
import team.bangbang.spring.parameter.SessionUser;
import team.bangbang.sso.data.Account;

/**
 * 信息发布 - Controller
 * 
 * @author ICode Studio
 * @version 1.0  2019-06-15
 */
@Controller
@RequestMapping("/website")
public class ArticleController {
	/**
	 * 信息发布列表
	 * 
	 * @param article 查询条件
	 * @param pagination 分页数据
	 * 
	 * @return 列表页面
	 */
	@RequestMapping("/articleList.do")
	public ModelAndView doList(@EntityParam Article article, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("website/articleList");

		// 构造下拉框数据
		configDropdownList(article, view);
		
		// 默认查询已上架
		if(article.getStatusFlag() == null) {
			// 状态标识{1：待上架2：已上架}
			article.setStatusFlag(2);
		}

		List<Article> articleList = ArticleService.list(article, null, pagination);
		
		// 统计符合条件的结果记录数量
		int recordCount = ArticleService.count(article, null);		
		pagination.setRecordCount(recordCount);
		// 数据装配

        //通过循环调用另外一张表中的数据,将Code转化成name
		for (int i = 0; articleList != null && i < articleList.size(); i++) {
		    Article a = articleList.get(i);
		    // 信息分类编码
            String code = a.getCategoryCode();
            Item category = ItemService.getItem("信息分类", code);
            a.setCategory(category);
        }
		
		view.addObject("article", article);
		view.addObject("articleList", articleList);
		view.addObject("pagination", pagination);
				
		return view;
	}

	/**
	 * 信息发布选择
	 * 
	 * @param article 查询条件
	 * @param pagination 分页数据
	 * 
	 * @return 选择页面
	 */
	@RequestMapping("/articleSelect.do")
	public ModelAndView doSelect(@EntityParam Article article, @EntityParam Pagination pagination) {
		ModelAndView view = new ModelAndView("website/articleSelect");

		// 构造下拉框数据
		configDropdownList(article, view);  
		
		List<Article> articleList = ArticleService.list(article, null, pagination);		
		
		// 统计符合条件的结果记录数量
		int recordCount = ArticleService.count(article, null);		
		pagination.setRecordCount(recordCount);		
		
		view.addObject("article", article);
		view.addObject("articleList", articleList);
		view.addObject("pagination", pagination);
				
		return view;
	}
	
	/**
	 * 新增页面显示
	 * 
	 * @param article 预设定的数据，比如在指定的分类下新增记录
	 * 
	 * @return 新增页面
	 */
	@GetMapping("/articleAdd.do")
	public ModelAndView doAdd(@EntityParam Article article) {
		ModelAndView view = new ModelAndView("website/articleAdd");

		// 构造下拉框数据
		configDropdownList(article, view);
		
		// 保存预设定的数据
		view.addObject("article", article);

		return view;  
	}
	
	/**
	 * 新增页面数据提交
	 *
	 * @param user 当前操作账户
	 * @param article 信息发布
     * @param request HTTP请求
	 * 
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/articleAdd.do")
	@ResponseBody
	public ResponseBase doAddAction(@SessionUser Account user, @EntityParam Article article, HttpServletRequest request) {
		// 是否存在重复记录？
		if(exist(article)) {
			return ResponseBase.DATA_DUPLICATE;
		}

		article.setUpdateTime(new Date());//实现自动获取当前更新时间
		article.setPosterName(user.getName());
		//自动获取当前的发布人,这里就是通过语雀中第五个方法，获取当前用户对象
		//在doAddation()函数中增加@SessionUser Account user注解获取当前对象，注意还需要在之前的绿字中添加@param注解才行
		article.setStatusFlag(1);//设置当前状态为1


		int result = ArticleService.insert(article);//将article对象中的内容调用ArticleService的insert函数插入，并把结果反馈给result。
		
		if(result != 1) {
			return ResponseBase.EXCEPTION_OCCURED;
		}


        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("新增信息发布");
        log.setBizData(article);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 修改页面显示
	 * 
	 * @param articleId 信息编号（关键字）
	 * 
	 * @return 修改页面
	 */
	@GetMapping("/articleUpdate.do")
	public ModelAndView doUpdate(@RequestParam(value="id") Long articleId) {
		ModelAndView view = new ModelAndView("website/articleUpdate");

		if(articleId == null || articleId == 0L) {
			return view;
		}

		// 查询条件		
		Article article = ArticleService.getObject(articleId);

		if(article == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + articleId + "的信息发布");
			return common;
		}
			
		view.addObject("article", article);
		
		// 构造下拉框数据
		configDropdownList(article, view);
		
		return view;
	}
	
	/**
	 * 修改页面数据提交
	 *
	 * @param user    当前操作账户
	 * @param article 信息发布
     * @param request HTTP请求
	 * 
	 * @return JSON格式的提交结果
	 */
	@PostMapping(value = "/articleUpdate.do")
	@ResponseBody
	public ResponseBase doUpdateAction(@SessionUser Account user,@EntityParam Article article, HttpServletRequest request) {
		// 为防止更新意外，必须传入id才能更新
		Long articleId = article.getId();

		if(articleId == null || articleId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

		// 是否存在重复记录？
		if(exist(article)) {
			return ResponseBase.DATA_DUPLICATE;
		}
		//更新后自动获取时间
		article.setUpdateTime(new Date());
		//自动获取当前发布人
		article.setPosterName(user.getName());

		// 第1个参数article，取关键字段article.articleId为条件
		// 第3个参数article，取article内关键字段以外其它属性数据
		int result = ArticleService.update(article, null, article);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("修改信息发布");
        log.setBizData(article);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 删除请求，强烈建议根据业务需求将此方法更改为逻辑删除
	 * 
	 * @param articleId 信息编号（关键字）
     * @param request HTTP请求
	 * 
	 * @return 删除结果
	 */
	@PostMapping(value = "/articleDelete.do")
	@ResponseBody
	public ResponseBase doDelete(@RequestParam(value="id") Long articleId, HttpServletRequest request) {

		if(articleId == null || articleId == 0L) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 获取待删除的对象，用于日志记录
        Article article = ArticleService.getObject(articleId);
        if(article == null) {
            return ResponseBase.DATA_NOT_FOUND;
        }

		// 限定条件
		Article where = new Article();
		where.setId(articleId);
		
		int result = ArticleService.delete(where, null);
		
		if(result != 1) {
			return ResponseBase.DATA_NOT_FOUND;
		}

        // 记录日志
        OperationLog log = new OperationLog();
        log.setType("删除信息发布");
        log.setBizData(article);
        request.setAttribute("log", log);
		
		return ResponseBase.SUCCESS;
	}
	
	/**
	 * 展示页面
	 *
	 *  @param articleId 信息编号（关键字）
	 *
	 *
	 * @return 展示页面
	 */
	@RequestMapping("/articleView.do")
	public ModelAndView doView(@RequestParam(value = "id") Long articleId) {
		ModelAndView view = new ModelAndView("website/articleView");

		if(articleId == null || articleId == 0L) {
			return view;
		}

		// 查询
		Article article = ArticleService.getObject(articleId);

		if(article == null) {
			ModelAndView common = new ModelAndView("common/message");
			common.addObject("message", "没有找到编号为" + articleId + "的信息发布");
			return common;
		}

		//获取当前对象的categoryCode，并将值传给code
		String code =article.getCategoryCode();

		//调用另外一张表中的数据,将Code转化成name
		Item category = ItemService.getItem("信息分类", code);
		article.setCategory(category);
		
		view.addObject("article", article);

		
		return view;
	}

	/**
	 * 配置所需的下拉列表数据（请务必保留该方法）
	 * 
	 * @param article 查询条件
	 * @param view 视图对象，将构造的下拉列表数据放入视图对象
	 */
	private void configDropdownList(Article article, ModelAndView view) {

		// 分类编码，关联config_item_base.ItemCode[Category=信息分类]列表
		List<Item> categoryList = ItemService.getItemList("信息分类", null);
		view.addObject("categoryList", categoryList);		
		
		// 状态{1：待上架2：已上架}列表
		List<KeyValue> statusList = ArticleService.getStatusList();
		view.addObject("statusList", statusList);

		
	}

	/**
	 * 用在新增、修改时检查数据库中是否存在重复记录（请务必保留该方法）
	 * 
	 * @param temp
	 *            将要修改的信息发布POJO
	 * @return true：已经存在 false：不存在
	 */
	private boolean exist(Article temp) {
		// 检查修改的信息发布是否有重复记录
		Article form = new Article();
		
		// 关键字限定条件
		String str = (temp.getId() == null ? null : "ArticleId != " + temp.getId());

		// 其它信息限定条件
		// 标题
		form.setTitle(temp.getTitle());

		return (ArticleService.getObject(form, str) != null);
	}
}
