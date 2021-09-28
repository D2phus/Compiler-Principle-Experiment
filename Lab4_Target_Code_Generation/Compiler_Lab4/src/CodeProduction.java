import org.omg.CORBA.INTERNAL;

import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.HashMap;

public class CodeProduction {
    ArrayList<BasicBlock> blocks = new ArrayList<>();//基本块
    int nameNum = 0;//每次声明新基本块，++

    RegFunc regFunc = new RegFunc();
    ArrayList<Registers> regs = regFunc.initRegs();

    ArrayList<TriAddr> triAddrs = new ArrayList<>();
    public void readTriAddrs(ArrayList<TriAddr> triAddrs){
        this.triAddrs = triAddrs;
    }
    private void printCodes(){
        for(int i=0;i<blocks.size();i++){
            System.out.println("【基本块"+i+"代码】");
            for(int j=0;j<blocks.get(i).getCode().size();j++){
                System.out.println(blocks.get(i).getCode().get(j));
            }
        }
    }
    private void writeCodes() throws IOException {
        String path="D:\\IdeaProjects\\2020Compiler_Principle\\Compiler_Lab3\\src\\result\\producedCode_res.txt";
        File file = new File(path);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        String line;
        for (BasicBlock block : blocks) {
            for (int j = 0; j < block.getCode().size(); j++) {
                line = block.getCode().get(j);
                fileOutputStream.write(line.getBytes());
                fileOutputStream.write('\n');
            }
        }
    }
    public void produceCode() throws IOException {
        System.out.println("====================================代码生成=======================================");
        initBlock();
        initBlockCodes();
        writeCodes();
    }
    private void initBlock(){
        /*
        * 初始化基本块，每个块之间的三地址码不会有重叠；
        * 重要的是判断每个块的入口三地址码，有两种：
        * 程序的第一个三地址码 or 跳转语句跳向的三地址码
        * */
        System.out.println("【initBlock】");
        for(int i=0;i<triAddrs.size();i++){
            if(i==0)
                triAddrs.get(i).setPort(1);//标记其为入口
            if(triAddrs.get(i).getOp().equals("j")){
                triAddrs.get(Integer.parseInt(triAddrs.get(i).getResult())).setPort(1);//跳转到的语句为入口
                triAddrs.get(i+1).setPort(1);//跳转语句的下一行也是入口
            }
        }
        for(int i=0;i<triAddrs.size();){
            BasicBlock b = new BasicBlock(nameNum);
            nameNum++;
            b.add(triAddrs.get(i));
            i++;
            for(;i<triAddrs.size();i++){
                if(triAddrs.get(i).getPort() == 1){
                    //是入口，就把当前的基本块结束，加入基本块列表
                    blocks.add(b);
                    break;
                }else b.add(triAddrs.get(i));//否则就把三地址码加进当前基本块中
            }
            if(i==triAddrs.size())
                blocks.add(b);
        }

        System.out.println("基本块个数："+blocks.size());
        for(BasicBlock ba:blocks){
            System.out.println(ba.getName());
            ArrayList<TriAddr> tmp = ba.getBlockTriAddrs();
            for(TriAddr t:tmp){
                String sysOut = t.getOp() + "\t" + t.getArg1() + "\t" + t.getArg2() + "\t" +
                        t.getResult() + "\t" + t.getPort() + "\t" + t.getIndex();
                System.out.println(sysOut);
            }
        }
    }
    private void initBlockCodes(){
        /*根据基本块的三地址码生成汇编代码*/
        for(int i=0;i<blocks.size();i++){
            ArrayList<String> tempCode = new ArrayList<>();
            for(int j=0;j<blocks.get(i).getBlockTriAddrs().size();j++){
                tempCode.addAll(triToCode(blocks.get(i).getBlockTriAddrs().get(j)));
            }
            blocks.get(i).setCode(tempCode);
        }
    }
    private ArrayList<String> triToCode(TriAddr tri){
        /*
        * 将三地址码转化为对应的汇编语句；
        * 采用类x86指令格式；
        * ADD arg1, arg2: arg1=arg1+arg2
        * SUB arg1, arg2: arg1=arg1-arg2
        * MUL arg1, arg2: arg1=arg1*arg2
        * 上述算术运算指令中，arg1不可为存储器操作数。
        * MOV arg1, arg2: arg1<-arg2
        * arg1和arg2不可同时为存储器操作数。
        * */
        ArrayList<String> codes = new ArrayList<>();
        String op = tri.getOp();
        String arg1 = tri.getArg1();
        String arg2 = tri.getArg2();
        String res = tri.getResult();
        clearUseless(tri);//清除寄存器中不会再用到的值
        Registers reg1;
        switch(op){
            case "+":
                reg1 = regs.get(regFunc.allocateReg(regs, arg1));
                if(regFunc.isExistValue(regs, arg1) && regFunc.isExistValue(regs, arg2)) {
                    Registers reg2 = regs.get(regFunc.allocateReg(regs, arg2));
                    codes.add("ADD " + reg1.getName() + "," + reg2.getName());
                    reg1.clearPush(res);
                }
                else if(regFunc.isExistValue(regs, arg1) && !regFunc.isExistValue(regs, arg2)) {
                    codes.add("ADD " + reg1.getName() + "," + arg2);
                    reg1.clearPush(res);
                }
                else if(!regFunc.isExistValue(regs, arg1) && regFunc.isExistValue(regs, arg2)) {
                    codes.add("MOV " + reg1.getName() + "," + arg1);
                    reg1.clearPush(arg1);
                    Registers reg2 = regs.get(regFunc.allocateReg(regs, arg2));
                    codes.add("ADD " + reg1.getName() + "," + reg2.getName());
                    reg1.clearPush(res);
                }
                else {
                    codes.add("MOV " + reg1.getName() + "," + arg1);
                    reg1.clearPush(arg1);
                    codes.add("ADD " + reg1.getName() + "," + arg2);
                    reg1.clearPush(res);

                }
                break;
            case "-":
                reg1 = regs.get(regFunc.allocateReg(regs, arg1));
                if(regFunc.isExistValue(regs, arg1) && regFunc.isExistValue(regs, arg2)) {
                    Registers reg2 = regs.get(regFunc.allocateReg(regs, arg2));
                    codes.add("SUB " + reg1.getName() + "," + reg2.getName());
                    reg1.clearPush(res);
                }
                else if(regFunc.isExistValue(regs, arg1) && !regFunc.isExistValue(regs, arg2)) {
                    codes.add("SUB " + reg1.getName() + "," + arg2);
                    reg1.clearPush(res);
                }
                else if(!regFunc.isExistValue(regs, arg1) && regFunc.isExistValue(regs, arg2)) {
                    codes.add("MOV " + reg1.getName() + "," + arg1);
                    reg1.clearPush(arg1);
                    Registers reg2 = regs.get(regFunc.allocateReg(regs, arg2));
                    codes.add("SUB " + reg1.getName() + "," + reg2.getName());
                    reg1.clearPush(res);
                }
                else {
                    codes.add("MOV " + reg1.getName() + "," + arg1);
                    reg1.clearPush(arg1);
                    codes.add("SUB " + reg1.getName() + "," + arg2);
                    reg1.clearPush(res);

                }
                break;
            case "*":
                reg1 = regs.get(regFunc.allocateReg(regs, arg1));
                if(regFunc.isExistValue(regs, arg1) && regFunc.isExistValue(regs, arg2)) {
                    Registers reg2 = regs.get(regFunc.allocateReg(regs, arg2));
                    codes.add("MUL " + reg1.getName() + "," + reg2.getName());
                    reg1.clearPush(res);
                }
                else if(regFunc.isExistValue(regs, arg1) && !regFunc.isExistValue(regs, arg2)) {
                    codes.add("MUL " + reg1.getName() + "," + arg2);
                    reg1.clearPush(res);
                }
                else if(!regFunc.isExistValue(regs, arg1) && regFunc.isExistValue(regs, arg2)) {
                    codes.add("MOV " + reg1.getName() + "," + arg1);
                    reg1.clearPush(arg1);
                    Registers reg2 = regs.get(regFunc.allocateReg(regs, arg2));
                    codes.add("MUL " + reg1.getName() + "," + reg2.getName());
                    reg1.clearPush(res);
                }
                else {
                    codes.add("MOV " + reg1.getName() + "," + arg1);
                    reg1.clearPush(arg1);
                    codes.add("MUL " + reg1.getName() + "," + arg2);
                    reg1.clearPush(res);
                }
                break;
            case "assign":
                if(!regFunc.isExistValue(regs, arg1)) {
                    //右边还不在寄存器里
                    reg1 = regs.get(regFunc.allocateReg(regs, arg1));
                    codes.add("MOV" + reg1.getName() + "," + arg1);
                    reg1.clearPush(arg1);
                    codes.add("MOV " + res + "," + reg1.getName());
                }
                else {//右边已经在寄存器里，就不用再取了
                    reg1 = regs.get(regFunc.allocateReg(regs, arg1));
                    codes.add("MOV " + res + "," + reg1.getName());
                }
                break;
        }

        String sysOut = tri.getOp() + "\t" + tri.getArg1() + "\t" + tri.getArg2() + "\t" +
                tri.getResult() + "\t" + tri.getPort() + "\t" + tri.getIndex();
        System.out.println("三地址码为：\t"+sysOut+"时，寄存器调整后状态：");
        for(Registers r:regs){
            System.out.println(r.getName()+"\t"+r.getValue()+"\t");
        }
        for(String str: codes){
            System.out.println(str);
        }

        return codes;
    }
    /*代码生成优化：
    * 删除公共表达式；
    * 删除非待用；
    * */
    private boolean isUseless(String arg, int index){
        for(int i=triAddrs.size()-1;i>=index;i--){
            //要包含index，因为该三地址码还没有执行！
            if(triAddrs.get(i).getArg1()!=null&&triAddrs.get(i).getArg1().equals(arg))
                return true;
            if(triAddrs.get(i).getArg2()!=null&&triAddrs.get(i).getArg2().equals(arg))
                return true;
        }
        return false;
    }
    private void clearUseless(TriAddr tri){
        /*判断含有该三地址码的块中，后续操作是否还用得到寄存器中的值
        * */
        int index = tri.getIndex();
        for (Registers reg : regs) {
            int j = 0;
            while (j < reg.getValue().size()) {
                if (!isUseless(reg.getValue().get(j), index))
                    reg.getValue().remove(j);//remove后的原本的j+1位变成了第j位，无需改变j
                else j++;
            }
        }
    }
}
