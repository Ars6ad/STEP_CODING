import java.util.*;

public class RateLimiterApp {
    static class TokenBucket {
        int tokens;
        long lastRefillTime;
        int maxTokens;
        long resetTime;
        public TokenBucket(int maxTokens) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
            this.resetTime = this.lastRefillTime + 3600 * 1000L;
        }
        public synchronized boolean tryConsume() {
            if (System.currentTimeMillis() > resetTime) {
                tokens = maxTokens;
                resetTime = System.currentTimeMillis() + 3600 * 1000L;
            }
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }
    }
    
    private Map<String, TokenBucket> buckets = new HashMap<>();
    
    public String checkRateLimit(String clientId) {
        buckets.putIfAbsent(clientId, new TokenBucket(1000));
        TokenBucket bucket = buckets.get(clientId);
        if (bucket.tryConsume()) {
            return "Allowed (" + bucket.tokens + " requests remaining)";
        } else {
            long retryAfter = (bucket.resetTime - System.currentTimeMillis()) / 1000;
            return "Denied (0 requests remaining, retry after " + retryAfter + "s)";
        }
    }
    
    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = buckets.get(clientId);
        if (bucket == null) return "No data";
        int used = bucket.maxTokens - bucket.tokens;
        return "{used: " + used + ", limit: " + bucket.maxTokens + ", reset: " + (bucket.resetTime / 1000) + "}";
    }

    public static void main(String[] args) {
        RateLimiterApp app = new RateLimiterApp();
        app.buckets.put("abc123", new TokenBucket(1000));
        System.out.println("checkRateLimit(clientId=\"abc123\") -> " + app.checkRateLimit("abc123"));
        System.out.println("checkRateLimit(clientId=\"abc123\") -> " + app.checkRateLimit("abc123"));
        
        app.buckets.get("abc123").tokens = 1;
        System.out.println("checkRateLimit(clientId=\"abc123\") -> " + app.checkRateLimit("abc123"));
        System.out.println("checkRateLimit(clientId=\"abc123\") -> " + app.checkRateLimit("abc123"));
        System.out.println("getRateLimitStatus(\"abc123\") -> " + app.getRateLimitStatus("abc123"));
    }
}
