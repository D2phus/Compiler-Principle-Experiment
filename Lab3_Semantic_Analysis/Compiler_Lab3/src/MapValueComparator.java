import java.util.Comparator;
import java.util.Map;

public class MapValueComparator implements Comparator<Map.Entry<String, SymbolAttr>> {
    @Override
    public int compare(Map.Entry<String, SymbolAttr> o1, Map.Entry<String, SymbolAttr> o2) {
        //降序
        SymbolAttr symbol1 = o1.getValue();
        SymbolAttr symbol2 = o2.getValue();
        return Long.compare(symbol1.getAddr(), symbol2.getAddr());
    }
}
