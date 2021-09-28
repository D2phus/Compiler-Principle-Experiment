import java.util.ArrayList;

public class Registers {
    /*一个寄存器*/
    private String name;
    private ArrayList<String> value = new ArrayList<>();
    public Registers(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(ArrayList<String> value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getValue() {
        return value;
    }

    public void clearPush(String appendValue){
        /*将寄存器清空，加入新值*/
        value.clear();
        value.add(appendValue);
    }
    public boolean isInReg(String valueName) {
        /*判断valueName是否在寄存器中*/
        for (String v : value) {
            if (v.equals(valueName))
                return true;
        }
        return false;
    }

}
