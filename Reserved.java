public class Reserved extends ParkingSpot {
    
     public Reserved(String spotId) {
        super(spotId, 10); // RM 10/hour
    }

    @Override
    public String getType() {
        return "Reserved";
    }
}
