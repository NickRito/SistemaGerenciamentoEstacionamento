import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket {
    private String ticketId;
    private String vehicleNumber;
    private String vehicleDescription;
    private LocalDateTime entryTime;
    private boolean isPaid;

    public Ticket(String vehicleNumber, String vehicleDescription) {
        this.ticketId = UUID.randomUUID().toString();
        this.vehicleNumber = vehicleNumber;
        this.vehicleDescription = vehicleDescription;
        this.entryTime = LocalDateTime.now();
        this.isPaid = false;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void payTicket() {
        this.isPaid = true;
    }
}
