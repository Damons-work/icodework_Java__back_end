package cn.js.icode.system.data;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author zhang.xianhao<br>
 * @version 1.0<br>
 * @CreateDate 2019/11/10 0:18 <br>
 * @see cn.js.icode.system.data <br>
 * @since R9.0<br>
 */
public class TreeSelectData {

    private String name;

    private String id;

    private List<TreeSelectData> children;

    public TreeSelectData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TreeSelectData> getChildren() {
        return children;
    }

    public void setChildren(List<TreeSelectData> children) {
        this.children = children;
    }
}
