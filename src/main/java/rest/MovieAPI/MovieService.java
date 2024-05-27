package rest.MovieAPI;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

@Profile("movie")
@Service
public class MovieService {
    private static final Map<String, MovieTicket> movieTickets = new HashMap<>();

    @PostConstruct
    public void initData()
    {
        for (int i = 0; i < 5; i++) {
            MovieTicket ticket = new MovieTicket();
            ticket.setDescription("Movie " + (i + 1));
            ticket.setStatus("Available");
            ticket.setDate(LocalDate.now().plusDays(i));
            ticket.setSpot("Spot " + (i + 1));
            save(ticket);
        }
    }

    public MovieTicket save(MovieTicket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(UUID.randomUUID().toString());
        }
        movieTickets.put(ticket.getId(), ticket);
        return ticket;
    }

    public Optional<MovieTicket> findByID(String id)
    {
        return Optional.ofNullable(movieTickets.get(id)) ;
    }

    public List<MovieTicket> findAll()
    {
        return new ArrayList<>(movieTickets.values());
    }

    public void deleteByID(String id)
    {
        movieTickets.remove(id);
    }


}

