import java.util.ArrayList;
import java.util.List;

public class ParkingLot {

    private static ParkingLot instance;

    private double revenue = 0;
    private int occupiedSpots = 0;

    private List<Floor> floors;   // All floors in the building

    private ParkingLot() {
        floors = new ArrayList<>();
    }

    public static synchronized ParkingLot getInstance() {
        if (instance == null) instance = new ParkingLot();
        return instance;
    }

    // Add a floor to the parking lot
    public void addFloor(Floor floor) {
        floors.add(floor);
    }

    // Get all floors
    public List<Floor> getFloors() {
        return floors;
    }

    // Get a specific floor by ID
    public Floor getFloorById(String floorId) {
        for (Floor f : floors) {
            if (f.getFloorId().equals(floorId)) {
                return f;
            }
        }
        return null;
    }

    // Add revenue when a vehicle exits
    public void addRevenue(double amount) {
        this.revenue += amount;
    }

    public double getRevenue() {
        return revenue;
    }

    // Occupancy tracking
    public void incrementOccupiedSpots() { occupiedSpots++; }
    public void decrementOccupiedSpots() { occupiedSpots--; }
    public int getOccupiedSpots() { return occupiedSpots; }

    // Optional: get total occupancy rate for entire lot
    public double getTotalOccupancyRate() {
        int totalSpots = 0;
        int totalOccupied = 0;
        for (Floor f : floors) {
            totalSpots += f.getSpots().size();
            totalOccupied += f.getSpots().stream().filter(s -> !s.isAvailable()).count();
        }
        if (totalSpots == 0) return 0;
        return (totalOccupied * 100.0) / totalSpots;
    }
}
