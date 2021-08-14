package team.bangbang.spring.db;

/**
 * database上下文类型
 *
 * @author 帮帮组
 * @version 1.0 2017年8月30日
 */
public class DbContextHolder {
	/* 使用线程变量保存database上下文 */
    private static final ThreadLocal<Boolean> local = new ThreadLocal<Boolean>();

    /**
     * 设置是否是只读。读可能是多个库，写只有一个库
     *
     * @param readOnly 是否制度
     */
    public static void setReadOnly(boolean readOnly) {
        local.set(readOnly);
    }

    /**
     * @return 是否是只读
     */
    public static boolean isReadOnly() {
    	Boolean bl = local.get();

        return (bl == null || bl.booleanValue());
    }
}
