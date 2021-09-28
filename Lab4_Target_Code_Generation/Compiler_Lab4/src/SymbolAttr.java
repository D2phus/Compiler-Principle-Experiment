import netscape.security.UserTarget;

import java.util.ArrayList;

public class SymbolAttr {
    /*符号表基本属性*/
    private String kind;//符号种类
    private String type;//类型
    private long addr;//起始地址
    private String extend;//扩展属性指针，表明数组的维度、维数，用$隔开

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAddr(long addr) {
        this.addr = addr;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getKind() {
        return kind;
    }

    public String getType() {
        return type;
    }

    public long getAddr() {
        return addr;
    }

    public String getExtend() {
        return extend;
    }
}
