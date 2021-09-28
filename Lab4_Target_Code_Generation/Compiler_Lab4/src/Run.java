import java.io.IOException;
import java.util.*;

public class Run {
    public static void main(String[] args) throws IOException {
        //词法分析，得到tokens
        Latex latex = new Latex();
        latex.Analyse();
        //词法分析模块向语法分析模块传递tokens信息
        GrammarAnalysis grammarAnalysis = new GrammarAnalysis();
        grammarAnalysis.readTokens(latex.getTokens());
        //语法分析、语义分析和中间代码生成
        boolean grammarCorrect = grammarAnalysis.LRAnalyse();
        //如果语法分析不正确，就无继续生成代码
        if(grammarCorrect) {
            CodeProduction codeProduction = new CodeProduction();
            codeProduction.readTriAddrs(grammarAnalysis.getTriAddrList());
            codeProduction.produceCode();
        }
    }
}
