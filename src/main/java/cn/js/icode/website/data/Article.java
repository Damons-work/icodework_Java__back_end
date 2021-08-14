package cn.js.icode.website.data;

import cn.js.icode.config.data.Item;
import java.util.Date;

import team.bangbang.common.CommonMPI;

/**
 * 信息发布 - POJO
 * 对应数据库表：website_article_base
 *
 * @author ICode Studio
 * @version 1.0  2019-06-15
 */
public class Article {
	/* 信息编号（关键字） */
	private Long articleId = null;

	/* 分类编码，关联config_item_base.ItemCode[Category=信息分类] */
	private String categoryCode = null;
	/* 分类编码，关联config_item_base.ItemCode[Category=信息分类] */
	private Item category = null;

	/* 头像图片地址 */
	private String imageUrl = null;

	/* 标题 */
	private String title = null;

	/* 副标题或者摘要 */
	private String subTitle = null;

	/* 信息内容 */
	private String content = null;

	/* 状态标识{1：待上架2：已上架} */
	private Integer statusFlag = null;

	/* 发布人 */
	private String posterName = null;

	/* 更新时间 */
	private Date updateTime = null;
	/* 更新时间 （查询上线） */
	private Date updateTimeTop = null;
	/* 更新时间 （查询下线） */
	private Date updateTimeBottom = null;

	/*
	 * 固定数据字典 状态{1：待上架2：已上架}
	 */
	public static final String[] statusFlags = { "待上架","已上架" };

	/**
	 * @return 信息编号
	 */
	public Long getId() {
		return articleId;
	}

	/**
	 * @param articleId 信息编号
	 */
	public void setId(Long articleId) {
		this.articleId = articleId;
	}

	/**
	 * @return 分类编码，关联config_item_base.ItemCode[Category=信息分类]
	 */
	public String getCategoryCode() {
		return categoryCode;
	}

	/**
	 * @param categoryCode 分类编码，关联config_item_base.ItemCode[Category=信息分类]
	 */
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	/**
	 * @return 分类编码，关联config_item_base.ItemCode[Category=信息分类]
	 */
	public Item getCategory() {
		return category;
	}

	/**
	 * @param category 分类编码，关联config_item_base.ItemCode[Category=信息分类]
	 */
	public void setCategory(Item category) {
		this.category = category;
	}

	/**
	 * @return 头像图片地址
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * @param imageUrl 头像图片地址
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * @return 标题
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title 标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return 副标题或者摘要
	 */
	public String getSubTitle() {
		return subTitle;
	}

	/**
	 * @param subTitle 副标题或者摘要
	 */
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	/**
	 * @return 信息内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content 信息内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return 状态标识{1：待上架2：已上架}
	 */
	public Integer getStatusFlag() {
		return statusFlag;
	}

	/**
	 * @param statusFlag 状态标识{1：待上架2：已上架}
	 */
	public void setStatusFlag(Integer statusFlag) {
		this.statusFlag=statusFlag;
	}

	/**
	 * @return 状态{1：待上架2：已上架}名称
	 */
	public String getStatusName() {
		Integer nFlag = getStatusFlag();
		return CommonMPI.getDictionaryName(statusFlags, nFlag);
	}

	/**
	 * @return 发布人
	 */
	public String getPosterName() {
		return posterName;
	}

	/**
	 * @param posterName 发布人
	 */
	public void setPosterName(String posterName) {
		this.posterName = posterName;
	}

	/**
	 * @return 更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime 更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * @return 更新时间（查询上线）
	 */
	public Date getUpdateTimeTop() {
		return updateTimeTop;
	}

	/**
	 * @param updateTimeTop 更新时间（查询上线）
	 */
	public void setUpdateTimeTop(Date updateTimeTop) {
		this.updateTimeTop = updateTimeTop;
	}

	/**
	 * @return 更新时间（查询下线）
	 */
	public Date getUpdateTimeBottom() {
		return updateTimeBottom;
	}

	/**
	 * @param updateTimeBottom 更新时间（查询下线）
	 */
	public void setUpdateTimeBottom(Date updateTimeBottom) {
		this.updateTimeBottom = updateTimeBottom;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (getId() == null)?0:getId().toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(getId() == null || obj == null || !(obj instanceof Article)) {
			return false;
		}

		return getId().equals(((Article)obj).getId());
	}
}
