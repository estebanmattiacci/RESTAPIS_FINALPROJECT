package rest.FootballAPI;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rest.Ticket;
import rest.ConcertAPI.ConcertService;


import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static rest.ConcertAPI.ConcertService.generateRandomTicket;

@Profile("football")
@Service
public class FootballService {
    private static final Map<String, Ticket> footballTickets = new HashMap<>();

    private static final List<String> FOOTBALL_MATCHES = Arrays.asList(
            "Manchester United vs Real Madrid",
            "Barcelona vs Bayern Munich",
            "Liverpool vs Paris Saint-Germain",
            "Chelsea vs Juventus",
            "Manchester City vs Inter Milan",
            "Arsenal vs AC Milan",
            "Bayern Munich vs Barcelona",
            "Real Madrid vs Liverpool",
            "Juventus vs Manchester United",
            "Paris Saint-Germain vs Chelsea",
            "Inter Milan vs Arsenal",
            "Manchester City vs Barcelona",
            "Liverpool vs Juventus",
            "AC Milan vs Bayern Munich",
            "Real Madrid vs Manchester City"
    );
    @PostConstruct
    public void initData()
    {
        for (int i = 0; i < 5; i++) {
            Ticket ticket = generateRandomTicket(i,FOOTBALL_MATCHES, "Football");
            save(ticket);
        }
    }

    public Ticket save(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(UUID.randomUUID().toString());
        }
        footballTickets.put(ticket.getId(), ticket);
        return ticket;
    }

    public Optional<Ticket> findByID(String id)
    {
        return Optional.ofNullable(footballTickets.get(id)) ;
    }

    public List<Ticket> findAll()
    {
        return new ArrayList<>(footballTickets.values());
    }

    public void deleteByID(String id)
    {
        footballTickets.remove(id);
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
        for (Ticket ticket : footballTickets.values()) {
            if ("Reserved".equals(ticket.getStatus()) && ticket.getReservedUntil() != null && ticket.getReservedUntil().isBefore(now)) {
                ticket.setStatus("Available");
                ticket.setReservedUntil(null);
                save(ticket);
            }
        }
    }
}
