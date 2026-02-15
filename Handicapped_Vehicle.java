// public class Handicapped_Vehicle extends Vehicle {
    
//     public Handicapped_Vehicle(String licensePlate, boolean hasCard) {
//         super(licensePlate);
//         this.setHandicappedCard(hasCard);
//     }

//     @Override
//     public boolean canParkIn(ParkingSpot spot) {
//         return spot.isAvailable(); // Can park anywhere if available
//     }
// }

public class Handicapped_Vehicle extends Vehicle {
    
    public Handicapped_Vehicle(String licensePlate, boolean hasCard) {
        super(licensePlate);
        this.setHandicappedCard(hasCard);
    }

    @Override
    public boolean canParkIn(ParkingSpot spot) {
        // Handicapped vehicles can park anywhere if spot is available
        return spot.isAvailable();
    }
    
    // This method determines the actual parking fee
    public double calculateParkingFee(ParkingSpot spot, int hours) {
        if (this.getHandiCappedCard()) {
            // Has card → MINUS RM 2 from spot rate
            double rate = spot.getHourlyRate() - 2.0;
            if (rate < 0) rate = 0;
            return hours * rate;
        } else {
            // No card → Pay spot's normal rate
            return hours * spot.getHourlyRate();
        }
    }
}