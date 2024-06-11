package rest.MovieAPI;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import rest.Ticket;


import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

@Profile("movie")
@Service
public class MovieService {
    private static final Map<String, Ticket> movieTickets = new HashMap<>();

    @PostConstruct
    public void initData()
    {
        for (int i = 0; i < 5; i++) {
            Ticket ticket = new Ticket();
            ticket.setEvent("Movie " + (i + 1));
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
        movieTickets.put(ticket.getId(), ticket);
        return ticket;
    }

    public Optional<Ticket> findByID(String id)
    {
        return Optional.ofNullable(movieTickets.get(id)) ;
    }

    public List<Ticket> findAll()
    {
        return new ArrayList<>(movieTickets.values());
    }

    public void deleteByID(String id)
    {
        movieTickets.remove(id);
    }


}

