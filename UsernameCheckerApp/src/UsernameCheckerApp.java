import java.util.*;

public class UsernameCheckerApp {
    private Map<String, Integer> userMap = new HashMap<>();
    private Map<String, Integer> frequencyMap = new HashMap<>();
    
    public boolean checkAvailability(String username) {
        frequencyMap.put(username, frequencyMap.getOrDefault(username, 0) + 1);
        return !userMap.containsKey(username);
    }
    
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int i = 1;
        while (suggestions.size() < 3) {
            String suggestion = username + i;
            if (!userMap.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
            i++;
        }
        return suggestions;
    }
    
    public String getMostAttempted() {
        String mostAttempted = null;
        int max = -1;
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }
        return mostAttempted + " (" + max + " attempts)";
    }

    public static void main(String[] args) {
        UsernameCheckerApp app = new UsernameCheckerApp();
        app.userMap.put("john_doe", 1);
        System.out.println("checkAvailability(\"john_doe\") -> " + app.checkAvailability("john_doe"));
        System.out.println("checkAvailability(\"jane_smith\") -> " + app.checkAvailability("jane_smith"));
        System.out.println("suggestAlternatives(\"john_doe\") -> " + app.suggestAlternatives("john_doe"));
        app.frequencyMap.put("admin", 10543);
        System.out.println("getMostAttempted() -> " + app.getMostAttempted());
    }
}
