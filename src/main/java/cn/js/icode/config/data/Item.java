package cn.js.icode.config.data;

/**
 * 选项配置 - POJO 对应数据库表：config_item_base
 *
 * @author ICode Studio
 * @version 1.0 2018-10-13
 */
public class Item {
	/* 选项编号（关键字） */
	private Long itemId = null;
	/* 父编号 */
	private Long parentId = null;
	/* 父 */
	private Item parent = null;
	/* 选项编码 */
	private String itemCode = null;
	/* 选项名称 */
	private String itemName = null;
	/* 所属分类 */
	private String category = null;
	/* 备注信息 */
	private String remark = null;
	/* 子项数量 */
	private Integer sonCount = null;

	/**
	 * @return 选项编号
	 */
	public Long getId() {
		return itemId;
	}

	/**
	 * @param itemId 选项编号
	 */
	public void setId(Long itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return 父编号
	 */
	public Long getParentId() {
		return parentId;
	}

	/**
	 * @param parentId 父编号
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return 父
	 */
	public Item getParent() {
		return parent;
	}

	/**
	 * @param parent 父
	 */
	public void setParent(Item parent) {
		this.parent = parent;
	}

	/**
	 * @return 选项编码
	 */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * @param itemCode 选项编码
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	/**
	 * @return 选项名称
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName 选项名称
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return 所属分类
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category 所属分类
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return 备注信息
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark 备注信息
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return 子项数量
	 */
	public Integer getSonCount() {
		return sonCount;
	}

	/**
	 * @param sonCount 子项数量
	 */
	public void setSonCount(Integer sonCount) {
		this.sonCount = sonCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (getId() == null) ? 0 : getId().toString().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (getId() == null || obj == null || !(obj instanceof Item)) {
			return false;
		}
		return getId().equals(((Item) obj).getId());
	}
}
