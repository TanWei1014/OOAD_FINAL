// Creational Pattern: Singleton
public class ParkingManager {
    private static ParkingManager instance;
    private double totalRevenue = 0;
    // Chapter 12: The Context holds a reference to the Strategy interface
    private FineStrategy activeFineStrategy = new FixedFine(); 

    private ParkingManager() {} 

    public static synchronized ParkingManager getInstance() {
        if (instance == null) instance = new ParkingManager();
        return instance;
    }
    
    public void setFineStrategy(FineStrategy strategy) {
        this.activeFineStrategy = strategy;
    }

    public FineStrategy getFineStrategy() {
        return activeFineStrategy;
    }

    public void addRevenue(double amount) { this.totalRevenue += amount; }
    public double getRevenue() { return totalRevenue; }
}