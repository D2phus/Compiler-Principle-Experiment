import java.lang.reflect.Array;
import java.util.ArrayList;

public class BasicBlock {
    /*
    * 程序基本块
    * */

    private String name;//基本块名
    private ArrayList<TriAddr> blockTriAddrs = new ArrayList<>();//该基本块下的三地址码
    private ArrayList<String> code = new ArrayList<>();//基本块对应的汇编代码
    public BasicBlock(int nameNum){
        //每次生成基本块后，nameNum++
        String str = Integer.toString(nameNum);
        name = "L" + str;
    }

    public String getName() {
        return name;
    }

    public ArrayList<TriAddr> getBlockTriAddrs() {
        return blockTriAddrs;
    }

    public ArrayList<String> getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(ArrayList<String> code) {
        this.code = code;
    }
    public void add(TriAddr tri){
        //将三地址码加入block
        blockTriAddrs.add(tri);
    }


}
