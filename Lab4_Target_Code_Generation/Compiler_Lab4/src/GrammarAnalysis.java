import java.io.*;
import java.util.*;

public class GrammarAnalysis {
    ArrayList<Integer> status = new ArrayList<>();//状态栈
    ArrayList<Sign> sign = new ArrayList<>();//符号栈
    ArrayList<Sign> inputSign = new ArrayList<>();//词法分析给出的字符串
    HashMap<Integer, String> tokenModifyMap = new HashMap<>();//将token中的数字转化为对应的字符串

    ArrayList<String> resultFormulate = new ArrayList<>();//产生式列表
    ArrayList<TriAddr> triAddrList = new ArrayList<>();//三地址码表
    int tempNumber = 1;//临时变量计数器
    HashMap<String, SymbolAttr> symbolList = new HashMap<>();//符号表
    int offSet = 0;
    ArrayList<Integer> extend = new ArrayList<>();//扩展指针，因为分析顺序自底向上，最后赋值时需要reverse


    public void readTokens(ArrayList<Token> tokens){
        /*将词法分析的Tokens转化为输入符号串*/
        tokenModifyMap.put(11, "+");tokenModifyMap.put(12, "-");tokenModifyMap.put(13, "*");
        tokenModifyMap.put(25, "=");tokenModifyMap.put(36, "(");tokenModifyMap.put(37, ")");
        tokenModifyMap.put(1, "ID");tokenModifyMap.put(42, "CONST_INT");tokenModifyMap.put(2, "INT");
        tokenModifyMap.put(3, "FLOAT");tokenModifyMap.put(4, "BOOLEAN");tokenModifyMap.put(5, "CHAR");
        tokenModifyMap.put(6, "STRING");tokenModifyMap.put(38, "[");tokenModifyMap.put(39, "]");
        for(Token token:tokens){
            if(token.getType()!=35){
                //不是分号，则属于同一个句子
                //将该token加入inputSign
                Sign sign = new Sign();
                sign.setId(tokenModifyMap.get(token.getType()));
                sign.setAttr(token.getAttr());
                inputSign.add(sign);
            }else{
                //本句结束，加入结束符
                Sign sign = new Sign();
                sign.setId("$");
                sign.setAttr("$");
                inputSign.add(sign);
            }
        }
//        System.out.println("【inputSign】");
//        for(Sign sign:inputSign){
//            System.out.print(sign.getId() + ", " + sign.getAttr() + '\t');
//        }
//        System.out.println();

    }
    public void write(String path, int mode) throws IOException {
        /*
        * 写文件。
        * 0：产生式
        * 1：三地址码
        * 2：符号表
        * */
        File file = new File(path);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        String line = "";
        switch(mode){
            case 0:
                for(String str:resultFormulate){
                    System.out.println(str);
                    fileOutputStream.write(str.getBytes());
                    fileOutputStream.write('\n');
                }
                break;
            case 1:
                for (TriAddr tri : triAddrList) {
                    line = tri.getOp() + "\t" + tri.getArg1() + "\t" + tri.getArg2() + "\t" + tri.getResult();
                    System.out.println(line);
                    fileOutputStream.write(line.getBytes());
                    fileOutputStream.write('\n');
                }
                break;
            case 2:
                Iterator iterator = symbolList.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String key = (String) entry.getKey();
                    SymbolAttr val = (SymbolAttr) entry.getValue();
                    line = key + "\t" + val.getKind() + "\t" + val.getType() + "\t" + val.getAddr()+ "\t" + val.getExtend();
                    System.out.println(line);
                    fileOutputStream.write(line.getBytes());
                    fileOutputStream.write('\n');
                }
                break;
        }
        fileOutputStream.close();
    }
    public void writeFile() throws IOException {
        String proPath = "D:\\IdeaProjects\\2020Compiler_Principle\\Compiler_Lab3\\src\\result\\production_res.txt";
        String triPath = "D:\\IdeaProjects\\2020Compiler_Principle\\Compiler_Lab3\\src\\result\\triCode_res.txt";
        String symbolPath = "D:\\IdeaProjects\\2020Compiler_Principle\\Compiler_Lab3\\src\\result\\symbolTable_res.txt";
        write(proPath, 0);
        write(triPath, 1);
        write(symbolPath, 2);
    }
    public boolean LRAnalyse() throws IOException {
        //分析表和产生式对象初始化
        LRAnalyseTable lrAnalyseTable = new LRAnalyseTable();
        String[][] action = lrAnalyseTable.action;
        int[][] goTo = lrAnalyseTable.goTo;
        Formulate formula = new Formulate();
        String[] formulate = formula.formulate;
        //inputStr读取
//        readTokenFile();
        int index = 1;//记录当前为第几个句子
        while(inputSign.size()!=0) {
            int step = 1;//步骤
            resultFormulate.add("sentence " + index + ":");
            //初始化
            status.clear();
            sign.clear();
            status.add(0);

            Sign signTemp = new Sign();
            signTemp.setId("$");
            signTemp.setAttr("$");
            sign.add(signTemp);
            int currentState = 0;
            Sign ch = inputSign.get(0);//当前inputSign从左到右第一个字符串
            String str;//分析表查询结果
            while ((str = action[currentState][lrAnalyseTable.getTerminalIndex(ch.getId())]) != "acc") {
                //一句的语法分析
                if (str.length() == 0) {
                    System.out.println("LR语法分析状态转移出错");//打印出错
                    //输出出错
                    resultFormulate.add("第"+ step + "步时出错；此时输入串为：");
                    for(Sign s : inputSign){
                        System.out.print(s.getId()+" ");
                    }
                    System.out.println();
                    writeFile();
                    return false;
                }
                //以s11为例，op=s，number = 11
                char op = str.charAt(0);
                String temp = str.substring(1);
                int number = Integer.parseInt(temp);
//                System.out.println("【AnalyseTable result:" + str + "】");
                //移进
                if (op == 's') {
                    sign.add(ch);
                    inputSign.remove(0);//移除
                    status.add(number);
//                    System.out.println("step " + step + ": shift, modify status to: " + status);
//                    System.out.println("step " + step + ": shift, modify sign to: ");
//                    for(Sign s : sign){
//                        System.out.print(s.getId()+" ");
//                    }
//                    System.out.println();
//                    System.out.println("step " + step + ": shift, modify inputSign to: ");
//                    for(Sign s : inputSign){
//                        System.out.print(s.getId()+" ");
//                    }
//                    System.out.println();

                }
                //归约
                else if (op == 'r') {
                    String formu = formulate[number];
                    resultFormulate.add(formu);
                    ArrayList<Sign> currentFormulate = new ArrayList<>();
                    String item = "";
                    for (int i = 0; i < formu.length(); i++) {
                        char forTemp = formu.charAt(i);
                        if (forTemp != ' ') {
                            item += forTemp;
                        } else {
                            Sign itemSign = new Sign();
                            itemSign.setId(item);
                            itemSign.setAttr("");//初始设为""
//                            System.out.println("itemSign" + itemSign.getId() + "," + itemSign.getAttr());
                            currentFormulate.add(itemSign);
                            item = "";
                        }
                    }
                    Sign itemSign = new Sign();
                    itemSign.setId(item);
                    itemSign.setAttr("");//初始设为""
                    currentFormulate.add(itemSign);
//                    System.out.println("itemSign" + itemSign.getId()+","+itemSign.getAttr());
//                    System.out.println("【currentFormulate】");
//                    for (Sign i : currentFormulate) {
//                        System.out.print(i.getId() + ',' + i.getAttr() + '\t');
//                    }
//                    System.out.print('\n');
                    Sign head = currentFormulate.get(0);//产生式首字符
                    int popCount = currentFormulate.size() - 2;//出栈次数，去掉首字符和->
//                    System.out.println("PopCount: "+popCount);
                    //status照旧先出栈再进栈
                    if(popCount!=0) {
                        //若右边为空不必出栈
                        for (int i = 1; i <= popCount; i++) {
                            //符号栈、状态栈一起出栈
                            //注意：每次remove当前栈中最后一个元素！
                            status.remove(status.size() - 1);
//                            System.out.println(status);
                        }
                    }
                    status.add(goTo[status.get(status.size() - 1)][lrAnalyseTable.getNonTerminalIndex(head.getId())]);
                    //sign先进栈再出栈，方便在formuAction中使用产生式左端的符号
                    sign.add(head);
                    Boolean errFlag = formuAction(number);
                    if(errFlag)
                        return false;
                    if(popCount!=0) {
                        for (int i = 1; i <= popCount; i++) {
                            sign.remove(sign.size() - 2);
                        }
                    }
//                    System.out.println("step " + step + ": reduce, modify status to: " + status);
//                    System.out.println("step " + step + ": reduce, modify sign to: ");
//                    for(Sign s : sign){
//                        System.out.print(s.getId()+" ");
//                    }
//                    System.out.println();
//
//                    System.out.println("step " + step + ": reduce, modify inputSign to: ");
//                    for(Sign s : inputSign){
////                        System.out.println(s.getId()+'\t'+s.getAttr()+'\t'+s.getKind()+'\t'+s.getType()+'\t'+s.getWidth());
//                        System.out.print(s.getId()+" ");
//                    }
//                    System.out.println();
//
                }
                step++;
                //更新当前状态和首符号
                currentState = status.get(status.size() - 1);
                ch = inputSign.get(0);
            }
            inputSign.remove(0);//移除上一个句子的结束符
            index++;
        }
        //对符号表按照地址升序排序
        MapFunc mapFunc = new MapFunc();
        symbolList = mapFunc.sortMap(symbolList);
        writeFile();
        //将产生式结果写入文件
        return true;
    }
    private boolean formuAction(int number){
        /*
        *输入产生式索引，执行对应的语义动作
        * */
        Sign head = sign.get(sign.size()-1);//符号栈头
        TriAddr triAddr = new TriAddr();//三地址码
        String newTemp;//新临时变量
        String idName;//变量名称
        long width;
        switch (number){
            case 1:
            //E'->S'
            case 14:
            //S->A
            case 16:
            //A->B
            case 20:
                //B->CONST_INT
            case 21:
                //B->CONST_FLOAT
            case 22:
                //B->CONST_BOOLEAN
            case 23:
                //B->CONST_CHAR
            case 24:
                //B->CONST_STRING
                head.setAttr(sign.get(sign.size()-2).getAttr());
                sign.set(sign.size()-1, head);
                break;
            case 2:
            //S'->T ID
            //声明语句
                idName = sign.get(sign.size()-2).getAttr();
                if(symbolList.containsKey(idName)){
                    System.out.println("重复声明变量"+idName+";");
                    return true;
                }
                SymbolAttr symbolAttr = new SymbolAttr();
                symbolAttr.setKind(sign.get(sign.size()-3).getKind());
                symbolAttr.setType(sign.get(sign.size()-3).getType());
                symbolAttr.setAddr(offSet);
                Collections.reverse(extend);
//                System.out.println("S'->TID: extend:"+extend);
                if(extend.size()!=0) {
                    String temp = "";
                    for(Integer i:extend){
                        temp+=i+"$";
                    }
                    symbolAttr.setExtend(temp);
                }
//                System.out.println("Local Extend: " + extend + " SymbolAttr's Extend: " + symbolAttr.getExtend());
                offSet+=sign.get(sign.size()-3).getWidth();
                symbolList.put(idName, symbolAttr);
                break;
            case 3:
                //T->D C
                head.setKind(sign.get(sign.size()-2).getKind());//C提供kind
                head.setType(sign.get(sign.size()-3).getType());//B提供type
                width = sign.get(sign.size()-3).getWidth();//B type的width
                if(sign.get(sign.size()-2).getWidth()!=0)
                    width = width * sign.get(sign.size()-2).getWidth();//是数组
                head.setWidth(width);
                //若是数组（扩展指针记录不为0），往指针中加入总维数
                int size;
                if((size = extend.size()) != 0)
                    extend.add(size);
//                System.out.println("T->BC: extend:"+extend);
                sign.set(sign.size()-1,head);
                break;
            case 4:
                //D->INT
                head.setType("int");
                head.setWidth(4);
                sign.set(sign.size()-1,head);
                break;
            case 5:
                //D->FLOAT
                head.setType("float");
                head.setWidth(4);
                sign.set(sign.size()-1, head);
                break;
            case 6:
                //D->BOOLEAN
                head.setType("boolean");
                head.setWidth(1);
                sign.set(sign.size()-1, head);
                break;
            case 7:
                //D->CHAR
                head.setType("char");
                head.setWidth(2);
                sign.set(sign.size()-1, head);
                break;
            case 8:
                //D->STRING
                //java中，String类型是用char[]数组存储的，因此String.width=char.width*char.num
                //声明时不分配width
                head.setType("String");
                sign.set(sign.size()-1, head);
                break;
            case 9:
                //C->
                head.setKind("变量");
                head.setWidth(0);
                sign.set(sign.size()-1, head);
                //清空扩展指针存储
                extend.clear();
                break;
            case 10:
                //C->[CONST_INT]C
                width = sign.get(sign.size()-2).getWidth();//C
                int constInt = Integer.parseInt(sign.get(sign.size()-4).getAttr());//CONST_INT
                extend.add(constInt);//将该维数加入扩展指针存储
//                System.out.println("C->[CONST_INT]C: extend:"+extend);
                if(width!=0)
                    width*=constInt;
                else width=constInt;
                head.setWidth(width);
                head.setKind("数组");
                sign.set(sign.size()-1, head);
                break;
            case 11:
            //S'->ID=S
                newTemp = newTemp();
                head.setAttr(newTemp);
                triAddr.setOp("assign");
                triAddr.setArg1(sign.get(sign.size()-2).getAttr());
                idName = sign.get(sign.size()-4).getAttr();
                if(!symbolList.containsKey(idName)){
                    System.out.println("变量"+idName+"未定义;");
                    return true;
                }
                triAddr.setResult(idName);
                triAddr.setIndex(triAddrList.size());
                triAddrList.add(triAddr);
                sign.set(sign.size()-1,head);
                break;
            case 12:
            //S->S+A
                newTemp = newTemp();
                head.setAttr(newTemp);
                triAddr.setOp("+");
                triAddr.setArg1(sign.get(sign.size()-4).getAttr());
                triAddr.setArg2(sign.get(sign.size()-2).getAttr());
                triAddr.setResult(newTemp);
                triAddr.setIndex(triAddrList.size());
                triAddrList.add(triAddr);
                sign.set(sign.size()-1,head);
                break;
            case 13:
            //S->S-A
                newTemp = newTemp();
                head.setAttr(newTemp);
                triAddr.setOp("-");
                triAddr.setArg1(sign.get(sign.size()-4).getAttr());
                triAddr.setArg2(sign.get(sign.size()-2).getAttr());
                triAddr.setResult(newTemp);
                triAddr.setIndex(triAddrList.size());
                triAddrList.add(triAddr);
                sign.set(sign.size()-1,head);
                break;
            case 15:
            //A->A*B
                newTemp = newTemp();
                head.setAttr(newTemp);
                triAddr.setOp("*");
                triAddr.setArg1(sign.get(sign.size()-4).getAttr());
                triAddr.setArg2(sign.get(sign.size()-2).getAttr());
                triAddr.setResult(newTemp);
                triAddr.setIndex(triAddrList.size());
                triAddrList.add(triAddr);
                sign.set(sign.size()-1,head);
                break;
            case 17:
            //A->-B
                newTemp = newTemp();
                head.setAttr(newTemp);
                triAddr.setOp("minus");
                triAddr.setArg1(sign.get(sign.size()-2).getAttr());
                triAddr.setResult(newTemp);
                triAddr.setIndex(triAddrList.size());
                triAddrList.add(triAddr);
                sign.set(sign.size()-1,head);
                break;
            case 18:
            //B->(S)
                head.setAttr(sign.get(sign.size()-3).getAttr());
                sign.set(sign.size()-1,head);
                break;
            case 19:
                //B->ID
                idName = sign.get(sign.size()-2).getAttr();
                if(!symbolList.containsKey(idName)){
                    System.out.println("变量"+idName+"未定义;");
                    return true;
                }
                head.setAttr(idName);
                sign.set(sign.size()-1, head);
                break;
            default:
                System.out.println("错误产生式编码: "+number);
                return true;
    }
    return false;
}
    private String newTemp(){
        //三地址码中的临时变量
        String newTemp = "newTemp" + tempNumber;
        tempNumber++;
        return newTemp;
    }
    public ArrayList<TriAddr> getTriAddrList() {
        return triAddrList;
    }

    public HashMap<String, SymbolAttr> getSymbolList() {
        return symbolList;
    }

    public void triToAssembler(){
        /*
        * 首先遍历所有寄存器，是否有value，是则返回对应寄存器；否则到内存中取。
        * 将三地址码转化为汇编语言*/

    }

}

