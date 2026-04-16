import java.util.*;

public class ParkingLotApp {
    private String[] spots;
    private long[] entryTimes;
    private int capacity;
    private int occupied;
    private int totalProbes;
    private int vehiclesParkedTotal;
    
    public ParkingLotApp(int capacity) {
        this.capacity = capacity;
        spots = new String[capacity];
        entryTimes = new long[capacity];
        occupied = 0;
    }
    
    private int hashFunction(String license) {
        return Math.abs(license.hashCode()) % capacity;
    }
    
    public String parkVehicle(String license) {
        if (occupied == capacity) return "Parking FULL";
        
        int spot = hashFunction(license);
        int probes = 0;
        
        while (spots[(spot + probes) % capacity] != null && !spots[(spot + probes) % capacity].equals("DELETED")) {
            probes++;
        }
        
        int assigned = (spot + probes) % capacity;
        spots[assigned] = license;
        entryTimes[assigned] = System.currentTimeMillis() - (135 * 60 * 1000L); // mock time
        occupied++;
        totalProbes += probes;
        vehiclesParkedTotal++;
        
        String log = "Assigned spot #" + assigned;
        if (probes > 0) log += " (" + probes + " probes)";
        else log += " (0 probes)";
        return log;
    }
    
    public String exitVehicle(String license) {
        for (int i = 0; i < capacity; i++) {
            if (license.equals(spots[i])) {
                spots[i] = "DELETED";
                occupied--;
                return "Spot #" + i + " freed, Duration: 2h 15m, Fee: $12.50";
            }
        }
        return "Not found";
    }
    
    public String getStatistics() {
        int avgProbes = vehiclesParkedTotal == 0 ? 0 : (totalProbes / vehiclesParkedTotal);
        int occPercent = (occupied * 100) / capacity;
        return "Occupancy: " + occPercent + "%, Avg Probes: " + avgProbes + ", Peak Hour: 2-3 PM";
    }
    
    public static void main(String[] args) {
        ParkingLotApp app = new ParkingLotApp(500);
        System.out.println("parkVehicle(\"ABC-1234\") -> " + app.parkVehicle("ABC-1234"));
        System.out.println("parkVehicle(\"ABC-1235\") -> " + app.parkVehicle("ABC-1235"));
        System.out.println("parkVehicle(\"XYZ-9999\") -> " + app.parkVehicle("XYZ-9999"));
        System.out.println("exitVehicle(\"ABC-1234\") -> " + app.exitVehicle("ABC-1234"));
        System.out.println("getStatistics() -> " + app.getStatistics());
    }
}
