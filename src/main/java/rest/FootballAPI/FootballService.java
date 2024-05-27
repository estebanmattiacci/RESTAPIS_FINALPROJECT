package rest.FootballAPI;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

@Profile("football")
@Service
public class FootballService {
    private static final Map<String, FootballTicket> footballTickets = new HashMap<>();

    @PostConstruct
    public void initData()
    {
        for (int i = 0; i < 5; i++) {
            FootballTicket ticket = new FootballTicket();
            ticket.setDescription("Football Match  " + (i + 1));
            ticket.setStatus("Available");
            ticket.setDate(LocalDate.now().plusDays(i));
            ticket.setSpot("Spot " + (i + 1));
            save(ticket);
        }
    }

    public FootballTicket save(FootballTicket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(UUID.randomUUID().toString());
        }
        footballTickets.put(ticket.getId(), ticket);
        return ticket;
    }

    public Optional<FootballTicket> findByID(String id)
    {
        return Optional.ofNullable(footballTickets.get(id)) ;
    }

    public List<FootballTicket> findAll()
    {
        return new ArrayList<>(footballTickets.values());
    }

    public void deleteByID(String id)
    {
        footballTickets.remove(id);
    }


}
