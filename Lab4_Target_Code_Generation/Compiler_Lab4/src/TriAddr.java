public class TriAddr {
    /*三地址码的四元表示*/
    private String op;
    private String arg1;
    private String arg2;
    private String result;
    private int port;//标记是否为基本块入口，若为1则为入口
    private int index;//标记该三地址码在三地址码表中的位置

    public TriAddr(){
        port = 0;
    }
    public String getOp() {
        return op;
    }

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public String getResult() {
        return result;
    }

    public int getPort() {
        return port;
    }

    public int getIndex() {
        return index;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
