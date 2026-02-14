import java.time.LocalDateTime;

public class Ticket {
    
    private String ticketId;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;


    public Ticket(Vehicle vehicle, ParkingSpot spot) {
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
        this.ticketId = generateId();
    }

    private String generateId() {
        return "T-" + vehicle.getLicenseplate() + "-" + System.currentTimeMillis();
    }



    public String getTicketId() {
       return ticketId;
    }

    public Vehicle getVehicle() {
    return vehicle;
    }

    public ParkingSpot getSpot() {
    return spot;
    }

    public LocalDateTime getEntryTime() {
    return entryTime;
    }

    public LocalDateTime getExitTime() {
    return exitTime;
    }

    
}
