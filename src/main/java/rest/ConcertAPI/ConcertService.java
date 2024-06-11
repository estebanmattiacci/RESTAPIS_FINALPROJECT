package rest.ConcertAPI;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import rest.Ticket;


import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Profile("concert")
@Service
public class ConcertService {
    private static final Map<String, Ticket> concertTickets = new HashMap<>();

    @PostConstruct
    public void initData()
    {
        for (int i = 0; i < 5; i++) {
            Ticket ticket = new Ticket();
            ticket.setEvent("Concert " + (i + 1));
            ticket.setStatus("Available");
            ticket.setDate(LocalDate.now().plusDays(i));
            ticket.setSpot("Spot " + (i + 1));
            save(ticket);
        }
    }

    public Ticket save(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(UUID.randomUUID().toString());
        }
        concertTickets.put(ticket.getId(), ticket);
        return ticket;
    }

    public Optional<Ticket> findByID(String id)
    {
        return Optional.ofNullable(concertTickets.get(id)) ;
    }

    public List<Ticket> findAll()
    {
        return new ArrayList<>(concertTickets.values());
    }

    public void deleteByID(String id)
    {
        concertTickets.remove(id);
    }

    public Optional<Ticket> reserveTicket(String id) {
        Optional<Ticket> optionalTicket = findByID(id);
        if (optionalTicket.isEmpty()) {
            return Optional.empty();
        }
        Ticket ticket = optionalTicket.get();
        if ("Available".equals(ticket.getStatus())) {
            ticket.setStatus("Reserved");
            ticket.setReservedUntil(LocalDateTime.now().plusMinutes(5));
            save(ticket);
            return Optional.of(ticket);
        } else {
            return Optional.empty();
        }
    }

    @Scheduled(fixedRate = 60000)
    public void checkReservations() {
        LocalDateTime now = LocalDateTime.now();
        for (Ticket ticket : concertTickets.values()) {
            if ("Reserved".equals(ticket.getStatus()) && ticket.getReservedUntil() != null && ticket.getReservedUntil().isBefore(now)) {
                ticket.setStatus("Available");
                ticket.setReservedUntil(null);
                save(ticket);
            }
        }
    }

}
