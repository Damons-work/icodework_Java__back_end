package cn.js.icode.common.data;

import cn.js.icode.system.data.Menu;

/**
 * <Description> <br>
 *
 * @author zhang.xianhao<br>
 * @version 1.0<br>
 * @CreateDate 2019/11/11 17:11 <br>
 * @see team.bangbang.common.data <br>
 * @since R9.0<br>
 */
public class WarningMessage {

    /* 编号 */
    private String messageId;

    /* 查询数量结果 */
    private int count;

    /* 提醒信息格式（如：您有{COUNT}个XXX需要处理） */
    private String tip;

    private Menu menu;


    public WarningMessage() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}
