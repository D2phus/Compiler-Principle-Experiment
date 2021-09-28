import com.sun.org.apache.bcel.internal.generic.ALOAD;
import jdk.nashorn.internal.parser.Lexer;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class GrammarAnalysis {
    ArrayList<Integer> status = new ArrayList<>();//状态栈
    ArrayList<String> sign = new ArrayList<>();//符号栈
    ArrayList<String> inputStr = new ArrayList<>();//词法分析给出的字符串
    ArrayList<String> resultFormulate = new ArrayList<>();//产生式列表
    HashMap<Integer, String> tokenModifyMap = new HashMap<>();//将token中的数字转化为对应的字符串

    public void readTokenFile() throws IOException {
        /*
        * 初始化Map；
        * 读Token文件并将结果存放到inputStr中
        * 根据分号分行。
        * */
        tokenModifyMap.put(11, "+");tokenModifyMap.put(12, "-");tokenModifyMap.put(13, "*");
        tokenModifyMap.put(25, "=");tokenModifyMap.put(36, "(");tokenModifyMap.put(37, ")");
        tokenModifyMap.put(1, "ID");tokenModifyMap.put(42, "CONST_INT");tokenModifyMap.put(2, "INT");
        String path = "D:\\IdeaProjects\\2020Compiler_Principle\\Compiler_Lab2\\src\\result\\token_res.txt";
        FileInputStream fileInputStream = new FileInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String line;
        String item = "";
        while((line = bufferedReader.readLine()) != null){
            char ch = line.charAt(1);
            int i = 1;
            while(ch!=','){
                item += ch;
                ch = line.charAt(++i);
            }
            if(!item.equals("35")) {
                item = tokenModifyMap.get(Integer.parseInt(item));
                inputStr.add(item);
                item = "";
            }else{
                //如果读到；表示本句结束，添加结束符。
                //一个完整句子：字符串$
                inputStr.add("$");
                item = "";
            }
        }
        for(String str:inputStr){
            System.out.print(str + '\t');
        }
        System.out.println();
//        inputStr.add("$");
        fileInputStream.close();
    }
    public void writeFormulateFile() throws IOException {
        /*将产生式写入文件*/
        String Path = "D:\\IdeaProjects\\2020Compiler_Principle\\Compiler_Lab2\\src\\result\\grammar_res.txt";
        File file = new File(Path);
        FileOutputStream outputStream = new FileOutputStream(file);
        for(String str:resultFormulate){
            System.out.println(str);
            outputStream.write(str.getBytes());
            outputStream.write('\n');
        }
        outputStream.close();
    }
    public void LRAnalyse() throws IOException {
        //分析表和产生式对象初始化
        LRAnalyseTable lrAnalyseTable = new LRAnalyseTable();
        String[][] action = lrAnalyseTable.action;
        int[][] goTo = lrAnalyseTable.goTo;
        Formulate formula = new Formulate();
        String[] formulate = formula.formulate;
        //inputStr读取
        readTokenFile();
        int index = 1;//记录当前为第几个句子
        while(inputStr.size()!=0) {
            int step = 1;//步骤
            resultFormulate.add("sentence " + index + ":");
            //初始化
            status.clear();
            sign.clear();
            status.add(0);
            sign.add("$");
            int currentState = 0;
            String ch = inputStr.get(0);//当前inputStr从左到右第一个字符串
            String str;//分析表查询结果
            while ((str = action[currentState][lrAnalyseTable.getTerminalIndex(ch)]) != "acc") {
                //一句的语法分析
                if (str.length() == 0) {
                    System.out.println("出错！");//打印出错
                    //输出出错
                    resultFormulate.add("第"+ step + "步时出错；此时输入串为：" + inputStr.toString());
                    writeFormulateFile();
                    return;
                }
                //以s11为例，op=s，number = 11
                char op = str.charAt(0);
                String temp = str.substring(1);
                int number = Integer.parseInt(temp);
                System.out.println("AnalyseTable result:" + str);
                //移进
                if (op == 's') {
                    sign.add(ch);
                    inputStr.remove(0);//移除
                    status.add(number);
                    System.out.println("step " + step + ": shift, modify status to: " + status);
                    System.out.println("step " + step + ": shift, modify sign to: " + sign);
                    System.out.println("step " + step + ": shift, modify inputStr to: " + inputStr);
                }
                //归约
                else if (op == 'r') {
                    String formu = formulate[number];
                    resultFormulate.add(formu);
                    ArrayList<String> currentFormulate = new ArrayList<>();
                    String item = "";
                    for (int i = 0; i < formu.length(); i++) {
                        char forTemp = formu.charAt(i);
                        if (forTemp != ' ') {
                            item += forTemp;
                        } else {
                            currentFormulate.add(item);
                            item = "";
                        }
                    }
                    currentFormulate.add(item);
                    System.out.println("step " + step + ": currentFormulate: ");
                    for (String i : currentFormulate) {
                        System.out.print(i + '\t');
                    }
                    System.out.print('\n');
                    String head = currentFormulate.get(0);//产生式首字符
                    int popCount = currentFormulate.size() - 2;//出栈次数，去掉首字符和->
                    for (int i = 1; i <= popCount; i++) {
                        //符号栈、状态栈一起出栈
                        //注意：每次remove当前栈中最后一个元素！
                        status.remove(status.size() - 1);
                        sign.remove(sign.size() - 1);
                        System.out.println(status);
                        System.out.println(sign);

                    }
                    //符号栈入栈产生式首字符，状态栈入栈goTo
                    sign.add(head);
                    status.add(goTo[status.get(status.size() - 1)][lrAnalyseTable.getNonTerminalIndex(head)]);
                    System.out.println("step " + step + ": reduce, modify status to: " + status);
                    System.out.println("step " + step + ": reduce, modify sign to: " + sign);
                    System.out.println("step " + step + ": reduce, modify inputStr to: " + inputStr);
                }
                step++;
                //更新当前状态和首符号
                currentState = status.get(status.size() - 1);
                ch = inputStr.get(0);
            }
            inputStr.remove(0);//移除上一个句子的结束符
            index++;
        }
        writeFormulateFile();
        //将产生式结果写入文件
    }
}
