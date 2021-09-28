import java.util.*;

public class MapFunc {
    public HashMap<String, SymbolAttr> sortMap(HashMap<String, SymbolAttr> map){
        /*对符号表按照地址升序排序*/
        if(map == null || map.isEmpty()){
            return null;
        }
        //LinkedHashMap：在HashMap的基础上，增加了一个双向链表，即重定义了Entry，使其保存对前后元素的引用
        //为了保证Map中元素和排序后的List元素顺序一致。
        HashMap<String, SymbolAttr> sortedMap = new LinkedHashMap<String, SymbolAttr>();
        List<Map.Entry<String, SymbolAttr>> entryList = new ArrayList<Map.Entry<String, SymbolAttr>>(map.entrySet());
        entryList.sort(new MapValueComparator());
        Iterator<Map.Entry<String, SymbolAttr>> iterator = entryList.iterator();
        Map.Entry<String, SymbolAttr> tmpEntry = null;
        while(iterator.hasNext()){
            tmpEntry = iterator.next();
            sortedMap.put(tmpEntry.getKey(),tmpEntry.getValue());
        }
        return sortedMap;
    }
}
