import java.util.*;

public class DNSCacheApp {
    static class DNSEntry {
        String ipAddress;
        long timestamp;
        long expiryTime;
        public DNSEntry(String ipAddress, long ttl) {
            this.ipAddress = ipAddress;
            this.timestamp = System.currentTimeMillis();
            this.expiryTime = this.timestamp + (ttl * 1000);
        }
        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
    
    private Map<String, DNSEntry> cache = new HashMap<>();
    private int hits = 0;
    private int misses = 0;
    private double totalTimeMs = 0;

    public String resolve(String domain) {
        long start = System.nanoTime();
        
        DNSEntry entry = cache.get(domain);
        if (entry != null && !entry.isExpired()) {
            hits++;
            long end = System.nanoTime();
            double duration = (end - start) / 1000000.0;
            totalTimeMs += duration;
            return "Cache HIT -> " + entry.ipAddress + " (retrieved in " + String.format("%.1f", duration) + "ms)";
        }
        
        misses++;
        if (entry != null) {
            cache.remove(domain);
            return "Cache EXPIRED -> Query upstream -> " + queryUpstream(domain);
        }
        return "Cache MISS -> Query upstream -> " + queryUpstream(domain) + " (TTL: 300s)";
    }
    
    private String queryUpstream(String domain) {
        String ip = "172.217.14.20" + (new Random().nextInt(10));
        cache.put(domain, new DNSEntry(ip, 300));
        return ip;
    }
    
    public String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        double avgTime = total == 0 ? 0 : (totalTimeMs / total);
        return String.format("Hit Rate: %.1f%%, Avg Lookup Time: %.1fms", hitRate, avgTime);
    }
    
    public static void main(String[] args) {
        DNSCacheApp app = new DNSCacheApp();
        System.out.println("resolve(\"google.com\") -> " + app.resolve("google.com"));
        System.out.println("resolve(\"google.com\") -> " + app.resolve("google.com"));
        
        // Simulating expiry manually
        app.cache.get("google.com").expiryTime = System.currentTimeMillis() - 1000;
        
        System.out.println("... after 301 seconds ...");
        System.out.println("resolve(\"google.com\") -> " + app.resolve("google.com"));
        System.out.println("getCacheStats() -> " + app.getCacheStats());
    }
}
