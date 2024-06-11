package rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket {

    private String id;
    private String event;
    private String status;
    private LocalDate date;
    private String spot;

    private LocalDateTime reservedUntil;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSpot() {
        return spot;
    }

    public void setSpot(String spot) {
        this.spot = spot;
    }

    public LocalDateTime getReservedUntil() {
        return reservedUntil;
    }

    public void setReservedUntil(LocalDateTime reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id) &&
                Objects.equals(event, ticket.event) &&
                Objects.equals(status, ticket.status) &&
                Objects.equals(date, ticket.date) &&
                Objects.equals(spot, ticket.spot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event, status, date, spot);
    }

}
