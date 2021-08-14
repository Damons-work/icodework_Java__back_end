package cn.js.icode.system.data;

/**
 * 告警配置 - POJO
 * 对应数据库表：system_warning_config
 * 
 * @author ICode Studio
 * @version 1.0  2019-11-08
 */
public class WarningConfig {
	/* 编号（关键字） */
	private Long id = null;

	/* 数据库配置序号 */
	private String dbAlias = null;
	
	/* 提醒信息格式（如：您有{COUNT}个XXX需要处理） */
	private String tip = null;
	
	/* 菜单项编号，关联system_menu_base.MenuId */
	private Long menuId = null;

	/* 统计查询的SQL语句 */
	private String querySql = null;

	/* 菜单 */
	private Menu menu = null;
	
	/**
	 * @return 编号
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id 编号
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return 数据库配置序号
	 */
	public String getDbAlias() {
		return dbAlias;
	}

	/**
	 * @param dbAlias 数据库配置序号
	 */
	public void setDbAlias(String dbAlias) {
		this.dbAlias = dbAlias;
	}
	
	/**
	 * @return 提醒信息格式（如：您有{COUNT}个XXX需要处理）
	 */
	public String getTip() {
		return tip;
	}

	/**
	 * @param tip 提醒信息格式（如：您有{COUNT}个XXX需要处理）
	 */
	public void setTip(String tip) {
		this.tip = tip;
	}
	
	/**
	 * @return 菜单项编号，关联system_menu_base.MenuId
	 */
	public Long getMenuId() {
		return menuId;
	}

	/**
	 * @param menuId 菜单项编号，关联system_menu_base.MenuId
	 */
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	
	/**
	 * @return 统计查询的SQL语句
	 */
	public String getQuerySql() {
		return querySql;
	}

	/**
	 * @param querySql 统计查询的SQL语句
	 */
	public void setQuerySql(String querySql) {
		this.querySql = querySql;
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
		if(getId() == null || obj == null || !(obj instanceof WarningConfig)) {
			return false;
		}

		return getId().equals(((WarningConfig)obj).getId());
	}

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}
