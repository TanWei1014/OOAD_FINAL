public abstract class Vehicle {
    protected String licenseplatenumber;
    protected long entryTime;
    protected long exitTime;
    protected boolean HandicappedCard;
    private boolean reservedViolation;
    


    public Vehicle(String licenseplate){
        this.licenseplatenumber = licenseplate;
        this.entryTime = System.currentTimeMillis();
        this.exitTime = 0;
        this.HandicappedCard = false;
    }

    public String getLicenseplate(){

        return licenseplatenumber;
    }

    public long getEntryTime(){
        return entryTime;
    }

    public long getExitTime(){
        return exitTime;
    }

    public boolean getHandiCappedCard(){
        return HandicappedCard;
    }

    public void setExitTime(long exitTime){
        this.exitTime = exitTime;
    }

    public void setHandicappedCard(boolean handicappedCard) {
        this.HandicappedCard = handicappedCard;
    }

    public long getParkingDurationHours() {
    
    long endTime;

    if (exitTime == 0) {
        endTime = System.currentTimeMillis();
    } else {
        endTime = exitTime;
    }

    long durationMillis = endTime - entryTime;

    double hours = durationMillis / (1000.0 * 60 * 60);

    long roundedHours = (long) Math.ceil(hours);

    return roundedHours;
    }


     public void setReservedViolation(boolean violation) {
        this.reservedViolation = violation;
    }

    
    public boolean reservedViolation(){
        return reservedViolation;
    }

    public abstract boolean canParkIn(ParkingSpot spot);

}
