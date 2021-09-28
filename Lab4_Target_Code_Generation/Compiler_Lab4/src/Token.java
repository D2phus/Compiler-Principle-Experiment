public class Token {
    int type;//种别码
    String attr;//属性值

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getAttr() {
        return attr;
    }
}
