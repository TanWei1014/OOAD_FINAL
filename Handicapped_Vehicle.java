public class Handicapped_Vehicle extends Vehicle {
    
    public Handicapped_Vehicle(String licensePlate, boolean hasCard) {
        super(licensePlate);
        this.setHandicappedCard(hasCard);
    }

    @Override
    public boolean canParkIn(ParkingSpot spot) {
        return spot.isAvailable(); // Can park anywhere if available
    }


   

}
