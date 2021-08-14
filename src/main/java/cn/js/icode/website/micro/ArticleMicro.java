package cn.js.icode.website.micro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import team.bangbang.common.data.Pagination;
import team.bangbang.common.data.StatusCode;
import team.bangbang.common.data.response.DataResponse;
import team.bangbang.common.data.response.ResponseBase;
import team.bangbang.common.utility.LogicUtility;
import cn.js.icode.website.data.Article;
import cn.js.icode.website.service.ArticleService;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 信息发布 - 微服务
 * 
 * 对应Feign：cn.js.icode.website.feign.ArticleFeign
 *
 * @author ICode Studio
 * @version 1.0  2019-06-15
 */
@ApiIgnore
@RestController
@CrossOrigin(allowCredentials="true", allowedHeaders="*", origins="*", maxAge=3600)
@RequestMapping("/microservice/article")
public final class ArticleMicro {
	class ArticleResult {
		/** 信息列表 */
		public List<JSONObject> list = null;
		/** 分页数据 */
		public Pagination pagination = null;
	}
	/**************************************************************************
	 * ！！除非设计、指导人员有特别说明，否则此处不得随意增加、修改、删除！！
	 * －－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
	 * 
	 *************************************************************************/
	/**
	 * 查询一条信息发布，并转化为相应的POJO对象
	 *
	 * @param where 查询条件，不能为null
	 * 
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	@PostMapping("/get")
	public ResponseBase getObject(Article where) {
		if(where == null) {
			where = new Article();
		}
		// 查询"已上架"记录
		// 状态标识{1：待上架2：已上架}
		where.setStatusFlag(2);
		Article unt = ArticleService.getObject(where, null);
		
		DataResponse<JSONObject> rb = new DataResponse<JSONObject>();
		rb.setStatusCode(StatusCode.SUCCESS);
		rb.setMessage("成功");
		rb.setData(toJSONObject(unt));
		
		return rb;
	}

	private JSONObject toJSONObject(Article unt) {
		if(unt == null) return new JSONObject();
		
		String s = JSONObject.toJSONString(unt);
		JSONObject j = JSONObject.parseObject(s);
		
		// 更新时间
		Date dt = unt.getUpdateTime();
		s = LogicUtility.getTimeAsString(dt);
		j.put("updateTime", s);
		
		return j;
	}

	/**
	 * 查询多条信息发布，并转化为相应的POJO对象列表
	 *
	 * @param where 更新条件，不能为null
	 * 
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	@PostMapping("/list")
	public ResponseBase list(Article where, Pagination pagination) {
		if(where == null) {
			where = new Article();
		}
		// 查询"已上架"记录
		// 状态标识{1：待上架2：已上架}
		where.setStatusFlag(2);
		pagination.setRecordCount(Integer.MAX_VALUE);
		List<Article> aList = ArticleService.list(where, null, pagination);
		int count = ArticleService.count(where, null);
		pagination.setRecordCount(count);
		
		ArticleResult ar = new ArticleResult();
		ar.list = toListJSONString(aList);
		ar.pagination = pagination;
		
		DataResponse<ArticleResult> rb = new DataResponse<ArticleResult>();
		rb.setStatusCode(StatusCode.SUCCESS);
		rb.setMessage("成功");
		rb.setData(ar);
		
		return rb;
	}

	private List<JSONObject> toListJSONString(List<Article> aList) {
		List<JSONObject> list = new ArrayList<JSONObject>();
		for(int i = 0; aList != null && i < aList.size(); i++) {
			JSONObject s = toJSONObject(aList.get(i));
			list.add(s);
		}
		
		return list;
	}

	/**
	 * 获得符合条件的信息发布数量
	 *
	 * @param where 查询条件，不能为null
	 * 
	 * @return 返回记录数量
	 */
	@PostMapping("/count")
	public int count(Article where) {
		return ArticleService.count(where, null);
	}
}
