public class Motorcycle extends Vehicle {
    
    public Motorcycle(String licenseplatenumber){
        super(licenseplatenumber);

    }

     @Override
    public boolean canParkIn(ParkingSpot spot) {
        return spot.getType().equals("Compact") && spot.isAvailable();
    }
}
