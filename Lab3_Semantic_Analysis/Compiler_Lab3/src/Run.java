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
        grammarAnalysis.LRAnalyse();
    }
}
