package rest.ConcertAPI;


import org.springframework.context.annotation.Profile;

import java.util.Objects;
import java.time.LocalDate;

@Profile("concert")
public class ConcertTicket {


    private String id;
    private String description;
    private String status;
    private LocalDate date;
    private String spot;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcertTicket ticket = (ConcertTicket) o;
        return Objects.equals(id, ticket.id) &&
                Objects.equals(description, ticket.description) &&
                Objects.equals(status, ticket.status) &&
                Objects.equals(date, ticket.date) &&
                Objects.equals(spot, ticket.spot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, status, date, spot);
    }
}
