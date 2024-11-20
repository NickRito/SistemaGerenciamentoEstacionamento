import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private int capacity;
    private double firstThreeHoursRate;
    private double subsequentHoursRate;
    private double totalRevenue;
    private List<Ticket> tickets;

    public ParkingLot(double firstThreeHoursRate) {
        this.capacity = 10; // Capacidade inicial padrão
        this.firstThreeHoursRate = firstThreeHoursRate;
        this.subsequentHoursRate = 5.0; // Valor padrão para horas subsequentes
        this.totalRevenue = 0.0;
        this.tickets = new ArrayList<>();
    }

    public void setCapacity(int newCapacity) {
        if (newCapacity < tickets.size() - getAvailableSpots()) {
            throw new IllegalArgumentException("Nova capacidade não pode ser menor que o número de veículos atualmente estacionados.");
        }
        this.capacity = newCapacity;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setRates(double firstThreeHoursRate, double subsequentHoursRate) {
        this.firstThreeHoursRate = firstThreeHoursRate;
        this.subsequentHoursRate = subsequentHoursRate;
    }

    public int getAvailableSpots() {
        return capacity - tickets.size();
    }

    public Ticket generateTicket(String vehicleNumber, String vehicleDescription) {
        if (getAvailableSpots() > 0) {
            Ticket ticket = new Ticket(vehicleNumber, vehicleDescription);
            tickets.add(ticket);
            return ticket;
        }
        return null; // Estacionamento cheio
    }

    public boolean payTicket(String ticketId) {
        for (Ticket ticket : tickets) {
            if (ticket.getTicketId().equals(ticketId) && !ticket.isPaid()) {
                LocalDateTime now = LocalDateTime.now();
                double fee = calculateFee(ticket.getEntryTime(), now);
                ticket.payTicket();
                totalRevenue += fee;
                return true;
            }
        }
        return false;
    }

    public double calculateFee(LocalDateTime entryTime, LocalDateTime exitTime) {
        long totalHours = java.time.Duration.between(entryTime, exitTime).toHours();
        if (totalHours <= 3) {
            return totalHours * firstThreeHoursRate;
        } else {
            return (3 * firstThreeHoursRate) + ((totalHours - 3) * subsequentHoursRate);
        }
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
    public double getFirstThreeHoursRate() {
        return firstThreeHoursRate;
    }
    
    public double getSubsequentHoursRate() {
        return subsequentHoursRate;
    }
    
}
