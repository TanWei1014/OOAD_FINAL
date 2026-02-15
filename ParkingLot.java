// import java.util.ArrayList;
// import java.util.List;

// public class ParkingLot {

//     private static ParkingLot instance;

//     private double revenue = 0;
//     private int occupiedSpots = 0;

//     private List<Floor> floors;   // All floors in the building

//     private ParkingLot() {
//         floors = new ArrayList<>();
//     }

//     public static synchronized ParkingLot getInstance() {
//         if (instance == null) instance = new ParkingLot();
//         return instance;
//     }

//     // Add a floor to the parking lot
//     public void addFloor(Floor floor) {
//         floors.add(floor);
//     }

//     // Get all floors
//     public List<Floor> getFloors() {
//         return floors;
//     }

//     // Get a specific floor by ID
//     public Floor getFloorById(String floorId) {
//         for (Floor f : floors) {
//             if (f.getFloorId().equals(floorId)) {
//                 return f;
//             }
//         }
//         return null;
//     }

//     // Add revenue when a vehicle exits
//     public void addRevenue(double amount) {
//         this.revenue += amount;
//     }

//     public double getRevenue() {
//         return revenue;
//     }

//     // Occupancy tracking
//     public void incrementOccupiedSpots() { occupiedSpots++; }
//     public void decrementOccupiedSpots() { occupiedSpots--; }
//     public int getOccupiedSpots() { return occupiedSpots; }

//     // Optional: get total occupancy rate for entire lot
//     public double getTotalOccupancyRate() {
//         int totalSpots = 0;
//         int totalOccupied = 0;
//         for (Floor f : floors) {
//             totalSpots += f.getSpots().size();
//             totalOccupied += f.getSpots().stream().filter(s -> !s.isAvailable()).count();
//         }
//         if (totalSpots == 0) return 0;
//         return (totalOccupied * 100.0) / totalSpots;
//     }






//     // Process vehicle exit: Mark spot as available and save exit record to TXT file


//     public void finalizeExit(String plate, double amountPaid, String method) {
//     // 1. Mark spot as available in memory

//     // This ONLY looks at cars currently in the active Java Object
//     for (Floor f : floors) {
//         for (ParkingSpot s : f.getSpots()) {
//             if (s.getCurrentVehicle() != null && s.getCurrentVehicle().getLicenseplate().equals(plate)) { 
//                 s.removeVehicle();
//                 decrementOccupiedSpots();
//                 break;
//             }
//         }
//     }
//     // for (Floor f : floors) {
//     //     for (ParkingSpot s : f.getSpots()) {
//     //         if (!s.isAvailable() && s.getCurrentVehicle().getLicenseplate().equalsIgnoreCase(plate)) {
//     //             s.removeVehicle();
//     //             decrementOccupiedSpots();
//     //             break;
//     //         }
//     //     }
//     // }
//     // 2. Persistent save (Requirement: Changes must be persistent)
//     try (java.io.FileWriter fw = new java.io.FileWriter("entry_exit.txt", true);
//          java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
//         pw.println("EXIT | " + plate + " | Paid: RM" + amountPaid + " | Method: " + method + " | Time: " + java.time.LocalDateTime.now());
//     } catch (java.io.IOException e) { e.printStackTrace(); }
// }

// public void initializeFloors(java.util.Map<String, Floor> floors) {
//     this.floors.clear();
//     this.floors.addAll(floors.values());
// }

// //     // Inside ParkingLot.java
// //     public void processVehicleExit(String licensePlate, double totalPaid) {
// //     // 1. Mark the spot as available in memory
// //     for (Floor floor : floors) {
// //         for (ParkingSpot spot : floor.getSpots()) {
// //             if (!spot.isAvailable() && spot.getCurrentVehicle().getLicenseplate().equalsIgnoreCase(licensePlate)) {
// //                 spot.removeVehicle();
// //                 decrementOccupiedSpots();
// //                 break;
// //             }
// //         }
// //     }

// //     // 2. Persistent Save to TXT file (Requirement: Changes must be persistent)
// //     try (BufferedWriter writer = new BufferedWriter(new FileWriter("entry_exit.txt", true))) {
// //         String exitRecord = String.format("EXIT | %s | Paid: RM%.2f | Time: %s", 
// //                             licensePlate, totalPaid, java.time.LocalDateTime.now());
// //         writer.write(exitRecord);
// //         writer.newLine();
// //     } catch (IOException e) {
// //         System.err.println("Error saving persistence data: " + e.getMessage());
// //     }
// // }
// }


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
        if (!floors.contains(floor)) {
            floors.add(floor);
        }
    }

    // Clear and set all floors (useful for initialization)
    public void setFloors(List<Floor> newFloors) {
        this.floors.clear();
        this.floors.addAll(newFloors);
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

    // Find a spot by vehicle plate
    public ParkingSpot findSpotByVehiclePlate(String plate) {
        for (Floor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                Vehicle v = spot.getCurrentVehicle();
                if (v != null && v.getLicenseplate().equalsIgnoreCase(plate)) {
                    return spot;
                }
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

    // Process vehicle exit: Mark spot as available and save exit record to TXT file

    public void finalizeExit(String plate, double amountPaid, String method) {
    // 1. Mark spot as available in memory (already done in ExitPanel)
    // But let's also do it here as a backup
    ParkingSpot spot = findSpotByVehiclePlate(plate);
    if (spot != null) {
        spot.removeVehicle();
        decrementOccupiedSpots();
        System.out.println("Spot " + spot.getSpotId() + " is now available");
    }
    
    // 2. Persistent save (Requirement: Changes must be persistent)
    try (java.io.FileWriter fw = new java.io.FileWriter("entry_exit.txt", true);
         java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
        pw.println("EXIT | " + plate + " | Paid: RM" + amountPaid + 
                  " | Method: " + method + 
                  " | Time: " + java.time.LocalDateTime.now());
    } catch (java.io.IOException e) { 
        e.printStackTrace(); 
    }
}
}

//     public void finalizeExit(String plate, double amountPaid, String method) {
//         // 1. Mark spot as available in memory
//         ParkingSpot spot = findSpotByVehiclePlate(plate);
//         if (spot != null) {
//             spot.removeVehicle();
//             decrementOccupiedSpots();
//         }
        
//         // 2. Persistent save (Requirement: Changes must be persistent)
//         try (java.io.FileWriter fw = new java.io.FileWriter("entry_exit.txt", true);
//              java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
//             pw.println("EXIT | " + plate + " | Paid: RM" + amountPaid + " | Method: " + method + " | Time: " + java.time.LocalDateTime.now());
//         } catch (java.io.IOException e) { 
//             e.printStackTrace(); 
//         }
//     }
// }