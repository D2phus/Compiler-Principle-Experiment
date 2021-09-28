import java.io.IOException;

public class Run {
    public static void main(String[] args) throws IOException {
        Latex latex = new Latex();
        latex.Analyse();
        GrammarAnalysis grammarAnalysis = new GrammarAnalysis();
        grammarAnalysis.LRAnalyse();

    }
}
