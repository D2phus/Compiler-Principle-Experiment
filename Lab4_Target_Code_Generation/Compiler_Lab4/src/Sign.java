public class Sign {
    /*语法分析时符号栈元素*/
    private String id;//符号
    private String attr;//符号值
    private String kind;//变量or数组
    private String type;//类型，int or float
    private long width;//长度

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getAttr() {
        return attr;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public long getWidth() {
        return width;
    }

    public String getKind() {
        return kind;
    }
}
