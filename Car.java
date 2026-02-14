public class Car extends Vehicle{

    public Car(String licensePlate) {
        super(licensePlate);
    }

    @Override
    public boolean canParkIn(ParkingSpot spot) {
        String type = spot.getType();
        return (type.equals("Compact") || type.equals("Regular")) && spot.isAvailable();
    }
    
}
