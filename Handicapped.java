public class Handicapped extends ParkingSpot {
    
    public Handicapped(String spotId) {
        super(spotId, 2); 
    }

    @Override
    public String getType() {
        return "Handicapped";
    }

    
    public double freeParking(Vehicle vehicle, int hours) {
        if (vehicle.getHandiCappedCard()) return 0;
        return hours * this.hourly_rate;
    }

}
