import java.util.*;

public class AnalyticsDashboardApp {
    private Map<String, Integer> pageViews = new HashMap<>();
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();
    private Map<String, Integer> trafficSources = new HashMap<>();
    private int totalViews = 0;
    
    public void processEvent(String url, String userId, String source) {
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);
        uniqueVisitors.computeIfAbsent(url, k -> new HashSet<>()).add(userId);
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
        totalViews++;
    }
    
    public void getDashboard() {
        System.out.println("Top Pages:");
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(pageViews.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        int rank = 1;
        for (Map.Entry<String, Integer> entry : sorted) {
            if (rank > 10) break;
            int uniqueVars = uniqueVisitors.get(entry.getKey()).size();
            System.out.println(rank + ". " + entry.getKey() + " - " + entry.getValue() + " views (" + uniqueVars + " unique)");
            rank++;
        }
        
        System.out.println("\nTraffic Sources:");
        List<String> sourcesStats = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            int percentage = totalViews == 0 ? 0 : (entry.getValue() * 100 / totalViews);
            sourcesStats.add(entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1) + ": " + percentage + "%");
        }
        System.out.println(String.join(", ", sourcesStats));
    }
    
    public static void main(String[] args) {
        AnalyticsDashboardApp app = new AnalyticsDashboardApp();
        app.processEvent("/article/breaking-news", "user_123", "google");
        app.processEvent("/article/breaking-news", "user_456", "facebook");
        
        app.pageViews.put("/article/breaking-news", 15423);
        Set<String> mockSet1 = new HashSet<>();
        for(int i=0; i<8234; i++) mockSet1.add("u" + i);
        app.uniqueVisitors.put("/article/breaking-news", mockSet1);
        
        app.pageViews.put("/sports/championship", 12091);
        Set<String> mockSet2 = new HashSet<>();
        for(int i=0; i<9871; i++) mockSet2.add("us" + i);
        app.uniqueVisitors.put("/sports/championship", mockSet2);
        
        app.trafficSources.put("google", 450);
        app.trafficSources.put("direct", 300);
        app.trafficSources.put("facebook", 150);
        app.trafficSources.put("other", 100);
        app.totalViews = 1000;
        
        System.out.println("getDashboard() -> ");
        app.getDashboard();
    }
}
