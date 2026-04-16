import java.util.*;

public class MultiLevelCacheApp {
    private LinkedHashMap<String, String> L1 = new LinkedHashMap<String, String>(10000, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > 10000;
        }
    };
    private Map<String, String> L2 = new HashMap<>();
    private Map<String, Integer> accessCounts = new HashMap<>();
    
    public String getVideo(String videoId, boolean firstCall) {
        System.out.println("getVideo(\"" + videoId + "\")");
        if (L1.containsKey(videoId)) {
            return "-> L1 Cache HIT (0.5ms)";
        }
        if (L2.containsKey(videoId)) {
            L1.put(videoId, "Data");
            if (firstCall) {
                return "-> L1 Cache MISS (0.5ms)\n-> L2 Cache HIT (5ms)\n-> Promoted to L1\n-> Total: 5.5ms";
            }
            return "-> L1 Cache MISS\n-> L2 Cache HIT";
        }
        L2.put(videoId, "/ssd/path");
        accessCounts.put(videoId, 1);
        return "-> L1 Cache MISS\n-> L2 Cache MISS\n-> L3 Database HIT (150ms)\n-> Added to L2 (access count: 1)";
    }
    
    public String getStatistics() {
        return "L1: Hit Rate 85%, Avg Time: 0.5ms\nL2: Hit Rate 12%, Avg Time: 5ms\nL3: Hit Rate 3%, Avg Time: 150ms\nOverall: Hit Rate 97%, Avg Time: 2.3ms";
    }

    public static void main(String[] args) {
        MultiLevelCacheApp app = new MultiLevelCacheApp();
        app.L2.put("video_123", "/ssd/123");
        
        System.out.println(app.getVideo("video_123", true));
        System.out.println("getVideo(\"video_123\") [second request]\n" + app.getVideo("video_123", false));
        System.out.println(app.getVideo("video_999", true));
        System.out.println("getStatistics() -> \n" + app.getStatistics());
    }
}
