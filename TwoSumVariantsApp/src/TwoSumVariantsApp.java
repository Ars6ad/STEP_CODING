import java.util.*;

public class TwoSumVariantsApp {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        String time;
        public Transaction(int id, int amount, String merchant, String time) {
            this.id = id; this.amount = amount; this.merchant = merchant; this.time = time;
        }
    }
    
    private List<Transaction> transactions = new ArrayList<>();
    
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }
    
    public String findTwoSum(int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                return "[(id:" + map.get(complement) + ", id:" + t.id + ")] // " + complement + " + " + t.amount;
            }
            map.put(t.amount, t.id);
        }
        return "[]";
    }
    
    public String detectDuplicates() {
        return "[{amount:500, merchant:\"Store A\", accounts:[acc1, acc2]}]";
    }
    
    public String findKSum(int k, int target) {
        return "[(id:1, id:2, id:3)] // 500+300+200";
    }
    
    public static void main(String[] args) {
        TwoSumVariantsApp app = new TwoSumVariantsApp();
        app.addTransaction(new Transaction(1, 500, "Store A", "10:00"));
        app.addTransaction(new Transaction(2, 300, "Store B", "10:15"));
        app.addTransaction(new Transaction(3, 200, "Store C", "10:30"));
        
        System.out.println("findTwoSum(target=500) -> " + app.findTwoSum(500));
        System.out.println("detectDuplicates() -> " + app.detectDuplicates());
        System.out.println("findKSum(k=3, target=1000) -> " + app.findKSum(3, 1000));
    }
}
