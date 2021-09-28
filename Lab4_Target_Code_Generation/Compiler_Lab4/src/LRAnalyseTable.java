import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;
import sun.nio.ch.ServerSocketAdaptor;

import javax.naming.BinaryRefAddr;
import java.util.BitSet;
import java.util.function.BiConsumer;

public class LRAnalyseTable {
    /*LR(1)文法分析表；
    * 定义终结符、非终结符、分析表。
    * */
    public static final String ES = "r1";
    public static final String STID = "r2";
    public static final String TDC = "r3";
    public static final String DINT = "r4";
    public static final String DFLO = "r5";
    public static final String DBOO = "r6";
    public static final String DCHA = "r7";
    public static final String DSTR = "r8";
    public static final String CNON = "r9";
    public static final String CSQU = "r10";
    public static final String SID = "r11";
    public static final String SSP = "r12";
    public static final String SSM = "r13";
    public static final String SA = "r14";
    public static final String AAB = "r15";
    public static final String AB = "r16";
    public static final String AMB = "r17";
    public static final String BBRA = "r18";
    public static final String BID = "r19";
    public static final String BCI = "r20";
    public static final String BCF = "r21";
    public static final String BCB = "r22";
    public static final String BCC = "r23";
    public static final String BCS = "r24";
    String[] terminal = {"+", "-", "*", "=", "(",  ")", "[", "]", "ID", "CONST_INT", "CONST_FLOAT", "CONST_BOOLEAN", "CONST_CHAR", "CONST_STRING", "INT", "FLOAT", "BOOLEAN", "CHAR", "STRING", "$"};
    int terminalNum = terminal.length;//终结符个数
    String[] nonTerminal = {"S", "A", "B", "C", "D", "S'", "T"};
    int nonTerminalNum = nonTerminal.length;//非终结符个数
    String[][] action = {
            {"", "", "", "", "", "", "", "", "s4", "", "", "", "", "", "s5", "s6", "s7", "s8", "s9", ""},
            {"", "", "", "", "", "", "s11", "", CNON, "", "", "", "", "", "", "", "", "", "", "", },
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "acc"},
            {"", "", "", "", "", "", "", "", "s12","", "", "", "", "", "", "", "", "", "", "",},
            {"", "", "", "s13", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", },
            {"", "", "", "", "", "", DINT, "", DINT, "", "", "", "", "", "", "", "", "", "", "", },
            {"", "", "", "", "", "", DFLO, "", DFLO, "", "", "", "", "", "", "", "", "", "", "", },
            {"", "", "", "", "", "", DBOO, "", DBOO, "", "", "", "", "", "", "", "", "", "", "", },
            {"", "", "", "", "", "", DCHA, "", DCHA, "", "", "", "", "", "", "", "", "", "", "", },
            {"", "", "", "", "", "", DSTR, "", DSTR, "", "", "", "", "", "", "", "", "", "", "", },
            {"", "", "", "", "", "", "", "", TDC, "", "", "", "", "", "", "", "", "", "", "", },
            {"", "", "", "", "", "", "", "", "", "s14", "", "", "", "", "", "", "", "", "", "", },
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", STID},
            {"", "s18", "", "", "s19", "", "", "", "s20", "s21", "s22", "s23", "s24", "s25", "", "", "", "", "", "", },
            {"", "", "", "", "", "", "", "s26", "", "", "", "", "", "", "", "", "", "", "", "", },
            {"s27", "s28","", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",  SID},
            {SA, SA, "s29", "", "", SA, "", "", "", "", "", "", "", "", "", "", "", "", "", SA},
            {AB, AB, AB, "", "", AB, "", "", "", "", "", "", "", "", "", "", "", "", "", AB},
            {"", "", "", "", "s19", "", "", "", "s20", "s21", "s22", "s23", "s24", "s25", "", "", "", "", "", "", },
            {"", "s18", "", "", "s19", "", "", "", "s20", "s21", "s22", "s23", "s24", "s25", "", "", "", "", "", "", },
            {BID, BID, BID, "", "", BID, "", "", "", "", "", "", "", "", "", "", "", "", "", BID},
            {BCI, BCI, BCI, "", "", BCI, "", "", "", "", "", "", "", "", "", "", "", "", "", BCI},
            {BCF, BCF, BCF, "", "", BCF, "", "", "", "", "", "", "", "", "", "", "", "", "", BCF},
            {BCB, BCB, BCB, "", "", BCB, "", "", "", "", "", "", "", "", "", "", "", "", "", BCB},
            {BCC, BCC, BCC, "", "", BCC, "", "", "", "", "", "", "", "", "", "", "", "", "", BCC},
            {BCS, BCS, BCS, "", "", BCS, "", "", "", "", "", "", "", "", "", "", "", "", "", BCS},
            {"", "", "", "", "", "", "s11", "", CNON, "", "", "", "", "", "", "", "", "", "", "", "", },
            {"", "s18", "", "", "s19", "", "", "", "s20", "s21", "s22", "s23", "s24", "s25", "", "", "", "", "", "", },
            {"", "s18", "", "", "s19", "", "", "", "s20", "s21", "s22", "s23", "s24", "s25", "", "", "", "", "", "", },
            {"", "", "", "", "s19", "", "", "", "s20", "s21", "s22", "s23", "s24", "s25", "", "", "", "", "", "", },
            {AMB, AMB, AMB, "", "", AMB, "", "", "", "", "", "", "", "", "", "", "", "", "", AMB},
            {"s27", "s28", "", "", "", "s36", "", "", "", "", "", "", "", "", "", "", "", "", "", "", },
            {"", "", "", "", "", "", "", "", CSQU, "", "", "", "", "", "", "", "", "", "", "", },
            {SSP, SSP, "s29", "", "", SSP, "", "", "", "", "", "", "", "", "", "", "", "", "", SSP},
            {SSM, SSM, "s29", "", "", SSM, "", "", "", "", "", "", "", "", "", "", "", "", "", SSM},
            {AAB, AAB, AAB, "", "", AAB, "", "", "", "", "", "", "", "", "", "", "", "", "", AAB},
            {BBRA, BBRA, BBRA, "", "", BBRA,  "", "", "", "", "", "", "", "", "", "", "", "", "", BBRA},
    };
    int[][] goTo={
            {-1,-1,-1,-1,1,2,3},
            {-1,-1,-1,10,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {15,16,17,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,30,-1,-1,-1,-1},
            {31,16,17,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,32,-1,-1,-1},
            {-1,33,17,-1,-1,-1,-1},
            {-1,34,17,-1,-1,-1,-1},
            {-1,-1,35,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
    };
    public int getTerminalIndex(String var){
        /*获取终结符var在终结符表中的索引*/
        for(int i=0;i<terminalNum;i++)
        {
            if(var.equals(terminal[i])){
                return i;
            }
        }
        return -1;
    }
    public int getNonTerminalIndex(String var){
        /*获取非终结符var在非终结符表中的索引*/
        for(int i=0;i<nonTerminalNum;i++)
        {
            if(var.equals(nonTerminal[i])){
                return i;
            }
        }
        return -1;
    }
}
