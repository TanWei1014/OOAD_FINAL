// File: ParkingLotData.java
public class ParkingLotData {
    private static ParkingLotData instance;
    private double revenue = 0;
    private int occupiedSpots = 0;

    private ParkingLotData() {} // Private constructor prevents multiple instances

    public static synchronized ParkingLotData getInstance() {
        if (instance == null) instance = new ParkingLotData();
        return instance;
    }

    public void addRevenue(double amount) { revenue += amount; }
    public double getRevenue() { return revenue; }
}