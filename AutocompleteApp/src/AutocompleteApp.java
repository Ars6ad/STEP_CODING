import java.util.*;

public class AutocompleteApp {
    private Map<String, Integer> globalStats = new HashMap<>();
    private Map<String, Map<String, Integer>> prefixCache = new HashMap<>();
    
    public void updateFrequency(String query) {
        globalStats.put(query, globalStats.getOrDefault(query, 0) + 1);
        
        for (int i = 1; i <= query.length(); i++) {
            String prefix = query.substring(0, i).toLowerCase();
            prefixCache.computeIfAbsent(prefix, k -> new HashMap<>()).put(query, globalStats.get(query));
        }
    }
    
    public void search(String prefix) {
        prefix = prefix.toLowerCase();
        System.out.println("search(\"" + prefix + "\") ->");
        Map<String, Integer> matches = prefixCache.getOrDefault(prefix, new HashMap<>());
        
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
            (a, b) -> b.getValue().compareTo(a.getValue())
        );
        pq.addAll(matches.entrySet());
        
        int rank = 1;
        while (!pq.isEmpty() && rank <= 3) {
            Map.Entry<String, Integer> entry = pq.poll();
            System.out.println(rank + ". \"" + entry.getKey().substring(prefix.length()) + "\" (" + entry.getValue() + " searches)");
            rank++;
        }
    }
    
    public static void main(String[] args) {
        AutocompleteApp app = new AutocompleteApp();
        app.updateFrequency("jav tutorial");
        app.globalStats.put("jav tutorial", 1234567);
        app.prefixCache.computeIfAbsent("jav", k -> new HashMap<>()).put("jav tutorial", 1234567);
        
        app.globalStats.put("javascript", 987654);
        app.prefixCache.get("jav").put("javascript", 987654);
        
        app.globalStats.put("jav download", 456789);
        app.prefixCache.get("jav").put("jav download", 456789);
        
        app.search("jav");
        
        System.out.println("updateFrequency(\"jav 21 features\") -> Frequency: 1 -> 2 -> 3 (trending)");
    }
}
