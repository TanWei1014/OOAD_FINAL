public class Regular extends ParkingSpot {
    public Regular(String spotId) {
        super(spotId, 5); // RM 5/hour
    }

    @Override
    public String getType() {
        return "Regular";
    }

}
