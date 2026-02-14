import java.util.ArrayList;
import java.util.List;

public class Floor {

    private String floorId;                  
    private List<ParkingSpot> spots;         

    
    public Floor(String floorId) {
        this.floorId = floorId;
        this.spots = new ArrayList<>();
    }

    // Add a parking spot to this floor
    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    // Get all spots
    public List<ParkingSpot> getSpots() {
        return spots;
    }

    // Get floor ID
    public String getFloorId() {
        return floorId;
    }

    // Return available spots for a given vehicle
    public List<ParkingSpot> getAvailableSpotsForVehicle(Vehicle vehicle) {
        List<ParkingSpot> available = new ArrayList<>();
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && vehicle.canParkIn(spot)) {
                available.add(spot);
            }
        }
        return available;
    }

    // Optional: Get total occupancy rate (0–100%)
    public double getOccupancyRate() {
        if (spots.isEmpty()) return 0;
        long occupiedCount = spots.stream().filter(s -> !s.isAvailable()).count();
        return (occupiedCount * 100.0) / spots.size();
    }

    // Optional: Get spot by ID (useful for EntryPanel button click)
    public ParkingSpot getSpotById(String spotId) {
        for (ParkingSpot spot : spots) {
            if (spot.getSpotId().equals(spotId)) {
                return spot;
            }
        }
        return null;
    }
}

