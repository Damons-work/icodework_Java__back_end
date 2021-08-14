package cn.js.icode.website.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import team.bangbang.common.CommonMPI;
import team.bangbang.common.data.KeyValue;
import team.bangbang.common.data.Pagination;
import cn.js.icode.website.data.Article;
import cn.js.icode.website.mapper.ArticleMapper;

/**
 * 信息发布 - Service
 *
 * @author ICode Studio
 * @version 1.0  2019-06-15
 */
@Service
public final class ArticleService {
	/* 信息发布（Article）Mapper */
	@Resource
	private ArticleMapper _articleMapper = null;
	/* 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要 */
	private static ArticleMapper articleMapper = null;

	@PostConstruct
	public void init() {
		// 设置static的Mapper对象，主要是为了兼顾Service层的static方法需要
		articleMapper = _articleMapper;
	}

	/**
	 * 得到指定的信息发布
	 *
	 * @param articleId
	 *			指定的信息编号
	 * @return 信息发布
	 */
	public static Article getObject(Long articleId) {
		if(articleMapper == null) {
			return null;
		}

		// 参数校验

		if(articleId == null || articleId == 0L) {
			return null;
		}

		// 查询条件
		Article form = new Article();
		form.setId(articleId);

		return getObject(form, null);
	}

	/**
	 * 插入一条信息发布
	 *
	 * @param data
	 *			插入的数据，不能为null
	 * @return 1：成功 其它：失败
	 */
	public static int insert(Article data) {
		if(articleMapper == null) {
			return 0;
		}

		if (data.getId() == null) {

			// 返回创建的关键字的值，如果是系统自动生成的，则此处不返回
			long id = CommonMPI.generateSequenceId();
			data.setId(id);

		}

		return articleMapper.insert(data);
	}

	/**
	 * 删除信息发布
	 *
	 * @param where
	 *			删除条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 成功删除的记录数量
	 */
	public static int delete(Article where, String appendix) {
		if(articleMapper == null) {
			return 0;
		}

		return articleMapper.delete(where, appendix);
	}

	/**
	 * 查询一条信息发布，并转化为相应的POJO对象
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回结果记录，并转化为相应的POJO对象
	 */
	public static Article getObject(Article where, String appendix) {
		if(articleMapper == null) {
			return null;
		}

		return articleMapper.getObject(where, appendix);
	}

	/**
	 * 修改信息发布
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param data
	 *			更新数据，不能为null
	 * @return 成功修改的记录数量
	 */
	public static int update(Article where, String appendix, Article data) {
		if(articleMapper == null) {
			return 0;
		}

		return articleMapper.update(where, appendix, data);
	}

	/**
	 * 查询多条信息发布，并转化为相应的POJO对象列表
	 *
	 * @param where
	 *			更新条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @param pagination
	 *			分页参数，如果分页参数为空，表示不分页
	 * @return 返回结果记录，并转化为相应的POJO对象列表
	 */
	public static List<Article> list(Article where, String appendix, Pagination pagination) {
		if(articleMapper == null) {
			return Collections.emptyList();
		}

		return articleMapper.list(where, appendix, pagination);
	}

	/**
	 * 获得符合条件的信息发布数量
	 *
	 * @param where
	 *			查询条件，不能为null
	 * @param appendix
	 *			附加限定条件
	 * @return 返回记录数量
	 */
	public static int count(Article where, String appendix) {
		if(articleMapper == null) {
			return 0;
		}

		return articleMapper.count(where, appendix);
	}

	/**
	 * 获得状态{1：待上架2：已上架}列表
	 *
	 * @return 状态{1：待上架2：已上架}列表
	 */
	public static List<KeyValue> getStatusList() {
		return CommonMPI.getDictionaryList(Article.statusFlags);
	}

}
