public class ParkingManager {
    private static ParkingManager instance;
    private Fine activeFineStrategy;
    private double totalRevenue;

    private ParkingManager() {
        activeFineStrategy = new FixedFine(); // Default
        totalRevenue = 0;
    }

    public static synchronized ParkingManager getInstance() {
        if (instance == null) {
            instance = new ParkingManager();
        }
        return instance;
    }

    public void setFineStrategy(Fine strategy) {
        this.activeFineStrategy = strategy;
    }

    public Fine getFineStrategy() {
        return activeFineStrategy;
    }

    public void addRevenue(double amount) {
        totalRevenue += amount;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}

// // Creational Pattern: Singleton
// public class ParkingManager {
//     private static ParkingManager instance;
//     private double totalRevenue = 0;
//     // Chapter 12: The Context holds a reference to the Strategy interface
//     private Fine activeFineStrategy = new FixedFine(); 

//     private ParkingManager() {} 

//     public static synchronized ParkingManager getInstance() {
//         if (instance == null) instance = new ParkingManager();
//         return instance;
//     }
    
//     public void setFineStrategy(Fine strategy) {
//         this.activeFineStrategy = strategy;
//     }

//     public Fine getFineStrategy() {
//         return activeFineStrategy;
//     }

//     public void addRevenue(double amount) { this.totalRevenue += amount; }
//     public double getRevenue() { return totalRevenue; }
// }