import java.util.ArrayList;

public class RegFunc {
    /*寄存器相关操作*/
    public ArrayList<Registers> initRegs() {
        /*初始化寄存器组*/
        ArrayList<Registers> regs = new ArrayList<>();
        Registers reg1 = new Registers("AX");
        regs.add(reg1);
        Registers reg2 = new Registers("BX");
        regs.add(reg2);
        Registers reg3 = new Registers("CX");
        regs.add(reg3);
        Registers reg4 = new Registers("DX");
        regs.add(reg4);
        return regs;
    }
    public int allocateReg(ArrayList<Registers> regs, String value){
        /*
        * 为value分配寄存器；
        * 寻找value是否已经在某个寄存器中；如果在返回该寄存器，不在返回空寄存器。
        * 符合赋值：a=b，如果b已经在寄存器R中，则不必生成代码，直接从R中取a即可
         * */
        for(int i=0;i<regs.size();i++){
            if(regs.get(i).isInReg(value))
                return i;
        }
        for(int i=0;i<regs.size();i++){
            if(regs.get(i).getValue().isEmpty())
                return i;
        }
        System.out.println("寄存器组满，无法继续分配");
        return -1;
    }
    public boolean isExistValue(ArrayList<Registers> regs, String value){
        /*value是否存在在寄存器组中
        * */
        for(Registers reg:regs){
            if(reg.isInReg(value))
                return true;
        }
        return false;
    }

}
