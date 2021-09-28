import sun.awt.AWTAccessor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Latex {
    private ArrayList<Token> tokens = new ArrayList<>();//token表
    private HashMap<String, SymbolAttr> symbol = new HashMap<>();//初始符号表，已经没有用了

    public ArrayList<Token> getTokens(){
        return tokens;
    }

    public HashMap<String, SymbolAttr> getSymbol() {
        return symbol;
    }

    public String[] readFile() throws IOException {
        /*
        * 按行读取得到strLine数组
        * */
        String[] text = new String[256];
        String path = "D:\\IdeaProjects\\2020Compiler_Principle\\Compiler_Lab3\\src\\result\\code.txt";
        FileInputStream fileInputStream = new FileInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String line;
        int lineIndex = 0;
        while((line = bufferedReader.readLine()) != null){
            text[lineIndex] = line;
            lineIndex++;
        }
        fileInputStream.close();
        return text;
    }
    public void writeFile() throws IOException {
        //Token结果
        String tokenPath = "D:\\IdeaProjects\\2020Compiler_Principle\\Compiler_Lab3\\src\\result\\token_res.txt";
        File tokenFile = new File(tokenPath);
        FileOutputStream tokenOutputStream = new FileOutputStream(tokenFile);
        for(Token token:tokens){
            String type = Integer.toString(token.getType());
            String attr = token.getAttr();
            String line = "(" + type + ", " + attr + ")";
            tokenOutputStream.write(line.getBytes());
            tokenOutputStream.write('\n');
        }
        tokenOutputStream.close();
        //符号表
        String symbolPath = "D:\\IdeaProjects\\2020Compiler_Principle\\Compiler_Lab2\\src\\result\\symbol_res.txt";
        File symbolFile = new File(symbolPath);
        FileOutputStream symbolOutputStream = new FileOutputStream(symbolFile);
        Iterator iterator = symbol.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            String line = "(" + key + ", " + val + ")";
            symbolOutputStream.write(line.getBytes());
            symbolOutputStream.write('\n');
        }
        symbolOutputStream.close();
    }
    public void Analyse() throws IOException {
        symbol.clear();
        String[] text = readFile();
//        int symbolPos = 0;//符号表表内位置
        int m = 0;
        while(m<text.length && text[m] != null){
            //逐行识别
//            System.out.println(m);
            String str = text[m];
//            System.out.println(str);
            if (str.equals(""))
                continue;
            else {
                char[] strLine = str.toCharArray();
//                System.out.println("【strLine char Array】");
//                for (int i = 0; i < strLine.length; i++) {
//                    System.out.print(strLine[i]+" ");
//                }
//                System.out.println("");
                for (int i = 0; i < strLine.length; i++) {
                    char ch = strLine[i];
                    Token token = new Token();
                    String attr = "";
                    ////判断标识符和关键字, 英文or下划线开头，由英文、数字或下划线构成的字符串
                    if (isAlpha(ch)||ch=='_') {
                        //提取
                        do {
                            attr += ch;
                            i++;
                            if (i > strLine.length) break;
                            ch = strLine[i];
                        } while (ch != '\0' && (isAlpha(ch) || isDigit(ch)) || ch == '_');
                        i--;//指针回退
                        //是关键字，放入token表
                        int type;
                        if ((type = isMatchKeyword(attr)) != -1) {
                            token.setType(type);
                            token.setAttr(attr);
                            tokens.add(token);
                        }
                        //是标识符，放入token表和符号表
                        else {
                            token.setType(ID);
                            token.setAttr(attr);
                            tokens.add(token);
                            //如果符号表为空或符号表中不包含当前标识符，加入符号表
                            if (symbol.isEmpty() || !symbol.containsKey(attr)) {
                                SymbolAttr symbolAttr = new SymbolAttr();
                                symbol.put(attr, symbolAttr);
//                                symbolPos++;
                            }
                        }
                    }
                    ////判断数字常量
                    else if (isDigit(ch)) {
                        /*
                         * 用DFA判断数字常量，共有6个状态；建立二维数组进行状态判断。
                         * */
                        int state = 0;
                        int k;
                        boolean isFloat = false;
                        String mistakeString = "";//记录错误的无符号常数输入
                        while ((ch != '\0') && (isDigit(ch) || ch == '.' || ch == 'e' || ch == 'E' || ch == '-')) {
                            //读取常量，并用DFA判断
                            if (ch == '.' || ch == 'e') {
                                isFloat = true;
                            }
                            for (k = 0; k <= 6; k++) {
                                //k用来遍历每个state下转移到6个state的条件
                                //转移到不能转移为止
                                char[] tmpStr = digitDFA[state].toCharArray();
                                if (ch != '#' && isInDigitDFA(ch, tmpStr[k])) {
                                    //状态转移
                                    attr += ch;
                                    mistakeString += ch;
                                    state = k;
                                    break;
                                }
                            }
                            if (k > 6) break;
                            i++;
                            if (i >= strLine.length) break;
                            ch = strLine[i];
                        }
                        boolean haveMistake = false;
                        if (state == 2 || state == 4 || state == 5) {
                            //非终止状态
                            haveMistake = true;
                        }
                        //可以识别如1.1.1、
                        else {
                            while(ch == 32){
                                mistakeString += ch;
                                i++;
                                if (i >= strLine.length) break;
                                ch = strLine[i];
                            }
                            if (!isOp(ch) || ch == '.') {
                                    haveMistake = true;
                            }
                        }
                        //错误处理
                        if (haveMistake) {
                            //一直到可分割的字符结束
                            while (ch != '\0' && ch != ',' && ch != ';' && ch != ' ') {
                                mistakeString += ch;
                                i++;
                                if (i >= strLine.length) break;
                                ch = strLine[i];
                            }
                            token.setType(-1);
                            token.setAttr(mistakeString+" 请确认无符号常数输入正确");
                            tokens.add(token);
                        }else{
                            if(isFloat){
                                token.setType(CONST_FLOAT);
                                token.setAttr(attr);
                                tokens.add(token);
                            }else{
                                token.setType(CONST_INT);
                                token.setAttr(attr);
                                tokens.add(token);
                            }
                        }
                        i--;//回退
                    }
                    ////判断字符常量
                    else if(ch =='\''){
                        int state = 0;
                        attr+=ch;
                        while(state!=3) {
                            i++;
                            if (i >= strLine.length) break;
                            ch = strLine[i];
                            for (int k = 0; k < 4; k++) {
                                //遍历该state下charDFA四个状态
                                char[] tmpStr = charDFA[state].toCharArray();
                                if (isInCharDFA(ch, tmpStr[k])) {
                                    attr += ch;
                                    state = k;
                                    break;
                                }
                            }
                        }
                        if(state!=3){
                            token.setType(-1);
                            token.setAttr(attr+" 字符常量引号未封闭");
                        }else{
                            token.setType(CONST_CHAR);
                            token.setAttr(attr);
                        }
                        tokens.add(token);
                    }
                    ////判断字符串常量
                    else if(ch == '\"'){
                        String string = "";
                        string+=ch;
                        int state = 0;
                        Boolean hasMistake = false;
                        while(state!=3){
                            i++;
                            if(i>=strLine.length-1){
                                hasMistake = true;
                                break;
                            }
                            ch = strLine[i];
                            if(ch == '\0'){
                                hasMistake = true;
                                break;
                            }
                            for(int k=0;k<4;k++){
                                char[] tmpStr = stringDFA[state].toCharArray();
                                if(isInStringDFA(ch, tmpStr[k])){
                                    string+=ch;
                                    if(k==2 && state == 1){
                                        if(isEsSt(ch))
                                            attr+='\\' + ch;
                                        else attr+=ch;
                                    }else if(k!=3 && k!=1)
                                        attr+=ch;
                                    state = k;
                                    System.out.println(string+" "+attr+" "+ state);
                                    break;
                                }
                            }
                        }
                        if(hasMistake){
                            token.setType(-1);
                            token.setAttr(string+" 字符串常量引号未封闭");
                            tokens.add(token);
                            --i;
                        }else{
                            token.setType(CONST_STRING);
                            token.setAttr(attr);
                            tokens.add(token);
                        }
                    }
                    ////判断运算符和界符
                    else if(isOp(ch)){
                        attr += ch;
                        //判断是否可以再读一个OP
                        if(canOpPlusEqual(ch)){
                            //是否可以跟’=‘?
                            i++;
                            if(i>strLine.length) break;
                            ch = strLine[i];
                            if(ch == '='){
                                attr += ch;
                            }
                            else if(canOpDouble(strLine[i-1])&&ch == strLine[i-1])
                                //是否可以跟和自己相同的op？
                                attr += ch;
                        //否则回退，只读一个OP
                            else i--;
                        }
                        //界符
                        if(attr.length() == 1){
                            char signal = attr.charAt(0);
                            int type;
                            boolean isBound = false;
                            if((type = isMatchBoundary(signal))!=-1){
                                token.setType(type);
                                token.setAttr(attr);
                                tokens.add(token);
                                isBound = true;
                            }
                            if(!isBound){
                                //否则是运算符
                                if((type = isMatchOp(attr))!=-1){
                                    token.setType(type);
                                    token.setAttr(attr);
                                    tokens.add(token);
                                }
                            }
                        }else{
                            //运算符
                            int type;
                            if((type = isMatchOp(attr))!=-1){
                                token.setType(type);
                                token.setAttr(attr);
                                tokens.add(token);
                            }
                        }
                    }
                }
            }
            m++;
        }
//        writeFile();
    }
    public static Boolean isEsSt(char ch) {
        //判断是否为转义字符
        return ch == 'a' || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r'
                || ch == 't' || ch == 'v' || ch == '?' || ch == '0';
    }
    public boolean isAlpha(char ch){
        return ((ch>='a'&&ch<='z')||(ch>='A'&&ch<='Z'));
    }
    public boolean isDigit(char ch){
        return (ch>='0'&&ch<='9');
    }
    public boolean isOp(char ch){
        for(char op: operator){
            if(ch==op){
                return true;
            }
        }
        return false;
    }
    public static final char[] operator = {
            '+', '-', '*', '/', '=', '<', '>', '&', '|', '~',
            '^', '!', '(', ')', '[', ']', '{', '}', '%', ';', ',', '#', '.'
    };
    public int isMatchOp(String str){
        //如果是运算符返回对应宏定义，否则返回-1
        int len = str.length();
        if(len == 1){
            char single = str.charAt(0);
            switch (single){
                case '+':
                    return ADD;
                case '-':
                    return SUB;
                case '*':
                    return MUL;
                case '/':
                    return DIV;
                case '!':
                    return NOT;
                case '^':
                    return XOR;
                case '<':
                    return LESS;
                case '>':
                    return MORE;
                case '=':
                    return ASSIGN;
            }
        }else{
            switch (str){
                case "||":
                    return OR;
                case "&&":
                    return AND;
                case "==":
                    return EQUAL;
                case "!=":
                    return NOT_EQUAL;
                case "<=":
                    return LESS_n_EQUAL;
                case ">=":
                    return MORE_n_EQUAL;
                case "+=":
                    return ADD_ASSIGN;
                case "-=":
                    return SUB_ASSIGN;
                case "*=":
                    return MUL_ASSIGN;
                case "/=":
                    return DIV_ASSIGN;
                case "&=":
                    return AND_ASSIGN;
                case "|=":
                    return OR_ASSIGN;
                case "^=":
                    return XOR_ASSIGN;
            }
        }
        return -1;
    }
    public int isMatchBoundary(char signal){
        //如果是界符返回对应的宏定义，否则返回-1
        for(char bound:boundary){
            if(signal == bound){
                switch (signal){
                    case ',':
                        return COMMA;
                    case ';':
                        return SEMI;
                    case '[':
                        return LEFT_SQUARE_BRACKET;
                    case ']':
                        return RIGHT_SQUARE_BRACKET;
                    case '(':
                        return LEFT_BRACKET;
                    case ')':
                        return RIGHT_BRACKET;
                    case '.':
                        return DOT;
                    case '{':
                        return LEFT_BRACE;
                    case '}':
                        return RIGHT_BRACE;
                }
            }
        }
        return -1;
    }
    public static final char[] boundary = {
            ',', ';', '[', ']', '(', ')', '.', '{', '}'
    };
    public int isMatchKeyword(String attr){
        //如果是关键字返回对应的宏定义，否则返回-1
        for(String keyWord:keyWords){
            if(attr.equals(keyWord)){
                switch (attr){
                    case "int":
                        return INT;
                    case "float":
                        return FLOAT;
                    case "boolean":
                        return BOOLEAN;
                    case "char":
                        return CHAR;
                    case "String":
                        return STRING;
                    case "if":
                        return IF;
                    case "else":
                        return ELSE;
                    case "while":
                        return WHILE;
                    case "return":
                        return MICRO_RETURN;
                }
            }
        }
        return -1;
    }
    public static final String[] keyWords ={
            "int", "float", "boolean", "char", "String", "while",
            "if", "else","return"
    };
    public boolean isInDigitDFA(char ch, char tmpStr){
        if(tmpStr=='d'){
            if(isDigit(ch))
                return true;
            else return false;
        }
        else if(tmpStr == 'e'){
            if(ch == tmpStr || ch == 'E')  return true;
        }
        else if(ch==tmpStr) return true;
        return false;
    }
    public static final String[] digitDFA = {
            "#d#####",
            "#d.#e##",
            "###d###",
            "###de##",
            "#####-d",
            "######d",
            "######d"
    };
    //字符常量和字符串常量中的转义字符仅考虑了\\和\’
    //a代表任意字符，b代表除\和'之外的字符
    public static String[] charDFA = {
            "#\\b#",
            "##a#",
            "###\'",
            "####" };
    public boolean isInCharDFA(char ch, char tmpStr){
        if(tmpStr == 'a')
            return true;
        else if(tmpStr == 'b'){
            if(ch != '\\' && ch != '\'')
                return true;
        }
        else if(tmpStr == '\\' || tmpStr == '\'') return ch == tmpStr;
        return false;
    }
    //a代表任意字符，b代表除\和'之外的字符
    public static String[] stringDFA = {
            "#\\b#",
            "##a#",
            "#\\b\"",
            "####"
    };
    public boolean isInStringDFA(char ch, char tmpStr){
        if(tmpStr == 'a')
            return true;
        if(tmpStr == '\\')
            return ch == tmpStr;
        if(tmpStr == '"')
            return ch == tmpStr;
        if(tmpStr == 'b')
            return ch != '\\' && ch != '"';
        return false;
    }
    public boolean canOpPlusEqual(char ch){
        //运算符后可以加=
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '!' ||
                ch == '=' || ch == '>' || ch == '<' || ch == '&' || ch == '|'
                || ch == '^';

    }
    public boolean canOpDouble(char ch){
        return ch == '+' || ch == '-' || ch == '&' || ch == '|';
    }
    //////宏定义编码表
    public static final int ID = 1;//标识符
    ////关键字
    public static final int INT = 2;
    public static final int FLOAT = 3;
    public static final int BOOLEAN = 4;
    public static final int CHAR = 5;
    public static final int STRING = 6;
    public static final int WHILE = 7;
    public static final int IF = 8;
    public static final int ELSE = 9;
    public static final int MICRO_RETURN = 10;
    ////运算符
    //算术运算符
    public static final int ADD = 11;//+
    public static final int SUB = 12;//-
    public static final int MUL = 13;//*
    public static final int DIV = 14;// /
    //逻辑运算符
    public static final int NOT = 15;//!
    public static final int OR = 16;//||
    public static final int AND = 17;//&&
    public static final int XOR = 18;//^
    //关系运算符
    public static final int EQUAL = 19;//==
    public static final int NOT_EQUAL = 20;//!=
    public static final int LESS = 21;//<
    public static final int MORE = 22;//>
    public static final int LESS_n_EQUAL = 23;//<=
    public static final int MORE_n_EQUAL = 24;//>=
    //赋值运算符
    public static final int ASSIGN =  25;//=
    public static final int ADD_ASSIGN =  26;//+=
    public static final int SUB_ASSIGN =  27;//-=
    public static final int MUL_ASSIGN =  28;//*=
    public static final int DIV_ASSIGN =  29;// /=
    public static final int OR_ASSIGN =  30;//|=
    public static final int AND_ASSIGN =  31;//&=
    public static final int XOR_ASSIGN =  32;//^=
    //分界符
    public static final int COMMA = 33;//,
    public static final int DOT = 34;//.
    public static final int SEMI = 35;//;
    public static final int LEFT_BRACKET = 36;//(
    public static final int RIGHT_BRACKET = 37;//)
    public static final int LEFT_SQUARE_BRACKET = 38;//[
    public static final int RIGHT_SQUARE_BRACKET = 39;//]
    public static final int LEFT_BRACE = 40;//{
    public static final int RIGHT_BRACE = 41;//}
    ////常量
    public static final int CONST_INT = 42;
    public static final int CONST_FLOAT = 43;
    public static final int CONST_CHAR = 44;
    public static final int CONST_STRING = 45;

}
