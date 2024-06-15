package rest.MovieAPI;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import rest.Ticket;
import rest.ConcertAPI.ConcertService.*;


import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static rest.ConcertAPI.ConcertService.generateRandomTicket;

@Profile("movie")
@Service
public class MovieService {
    private static final Map<String, Ticket> movieTickets = new HashMap<>();

    private static final List<String> MOVIE_NAMES = Arrays.asList(
            "Oppenheimer at Kinepolis Bruxelles",
            "Avatar 2 at AMC Empire 25",
            "Spider-Man: No Way Home at Regal LA Live",
            "The Batman at Odeon Leicester Square",
            "Dune at Alamo Drafthouse",
            "Top Gun: Maverick at Vue West End",
            "Black Panther: Wakanda Forever at ArcLight Hollywood",
            "No Time to Die at Cineworld Glasgow",
            "Shang-Chi at The Light Leeds",
            "Eternals at Picturehouse Central",
            "The Matrix Resurrections at Showcase Cinema de Lux",
            "Jurassic World Dominion at Everyman Cinema",
            "The Suicide Squad at Cineworld Dublin",
            "Mission: Impossible 7 at IMAX Melbourne",
            "Doctor Strange 2 at Odeon Manchester"
    );
    @PostConstruct
    public void initData()
    {
        for (int i = 0; i < 5; i++) {
            Ticket ticket = generateRandomTicket(i, MOVIE_NAMES, "Movies");
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
        for (Ticket ticket : movieTickets.values()) {
            if ("Reserved".equals(ticket.getStatus()) && ticket.getReservedUntil() != null && ticket.getReservedUntil().isBefore(now)) {
                ticket.setStatus("Available");
                ticket.setReservedUntil(null);
                save(ticket);
            }
        }
    }



}

