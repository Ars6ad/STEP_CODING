import java.util.*;

public class FlashSaleManagerApp {
    private Map<String, Integer> stockMap = new HashMap<>();
    private Map<String, LinkedList<Integer>> waitingLists = new HashMap<>();
    
    public FlashSaleManagerApp() {
        stockMap.put("IPHONE15_256GB", 100);
        waitingLists.put("IPHONE15_256GB", new LinkedList<>());
    }
    
    public String checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0) + " units available";
    }
    
    public synchronized String purchaseItem(String productId, int userId) {
        int stock = stockMap.getOrDefault(productId, 0);
        if (stock > 0) {
            stockMap.put(productId, stock - 1);
            return "Success, " + (stock - 1) + " units remaining";
        } else {
            waitingLists.computeIfAbsent(productId, k -> new LinkedList<>()).add(userId);
            return "Added to waiting list, position #" + waitingLists.get(productId).size();
        }
    }

    public static void main(String[] args) {
        FlashSaleManagerApp app = new FlashSaleManagerApp();
        System.out.println("checkStock(\"IPHONE15_256GB\") -> " + app.checkStock("IPHONE15_256GB"));
        System.out.println("purchaseItem(\"IPHONE15_256GB\", 12345) -> " + app.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println("purchaseItem(\"IPHONE15_256GB\", 67890) -> " + app.purchaseItem("IPHONE15_256GB", 67890));
        app.stockMap.put("IPHONE15_256GB", 0); // simulate sold out
        System.out.println("... (after 100 purchases)");
        System.out.println("purchaseItem(\"IPHONE15_256GB\", 99999) -> " + app.purchaseItem("IPHONE15_256GB", 99999));
    }
}
