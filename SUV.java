public class SUV extends Vehicle {
    
    public SUV(String licensePlate) {
        super(licensePlate);
    }

    @Override
    public boolean canParkIn(ParkingSpot spot) {
        return spot.getType().equals("Regular") && spot.isAvailable();
    }
}
