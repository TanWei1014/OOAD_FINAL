public class Compact extends ParkingSpot {
    public Compact(String spotId) {
        super(spotId, 2); // RM 2/hour
    }

    @Override
    public String getType() {
        return "Compact";
    }
}
