public abstract class ParkingSpot {

    protected String spotId;           
    protected boolean isOccupied;      
    protected int hourly_rate;         
    protected Vehicle currentVehicle;  

    public ParkingSpot(String spotId, int hourly_rate) {
        this.spotId = spotId;
        this.hourly_rate = hourly_rate;
        this.isOccupied = false;
        this.currentVehicle = null;
    }


    public boolean isAvailable() {
        return !isOccupied;
    }

    
    public void parkVehicle(Vehicle vehicle) {
        this.currentVehicle = vehicle;
        this.isOccupied = true;
    }

    
    public void removeVehicle() {
        this.currentVehicle = null;
        this.isOccupied = false;
    }

    
    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    
    public String getSpotId() {
        return spotId;
    }

    
    public int getHourlyRate() {
        return hourly_rate;
    }

    
    public abstract String getType();
}
