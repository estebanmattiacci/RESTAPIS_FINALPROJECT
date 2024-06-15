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


    private static final List<String> CONCERT_NAMES = Arrays.asList(
            "Coldplay Concert at Accord Arena",
            "Ed Sheeran Live at Wembley",
            "Adele at Madison Square Garden",
            "Taylor Swift at Staples Center",
            "Beyonce at Coachella",
            "Bruno Mars at O2 Arena",
            "The Weeknd at United Center",
            "Billie Eilish at The Forum",
            "Drake at Barclays Center",
            "Post Malone at TD Garden",
            "Ariana Grande at Honda Center",
            "Justin Bieber at AT&T Stadium",
            "Lady Gaga at MetLife Stadium",
            "Katy Perry at MGM Grand Garden Arena",
            "Harry Styles at Little Caesars Arena"
    );

    private static final Random RANDOM = new Random();

    public static Ticket generateRandomTicket(int index, List<String> EVENT_NAMES, String Type) {
        Ticket ticket = new Ticket();
        ticket.setEvent(EVENT_NAMES.get(index % EVENT_NAMES.size()));  // Get a random concert name
        ticket.setStatus("Available");
        ticket.setType(Type);
        ticket.setDate(generateRandomDate());
        ticket.setSpot(generateRandomSpot(index));
        return ticket;
    }

    private static LocalDate generateRandomDate() {
        int randomDaysToAdd = RANDOM.nextInt(30) + 1;  // Random date within the next 30 days
        return LocalDate.now().plusDays(randomDaysToAdd);
    }

    private static String generateRandomSpot(int index) {
        int randomSpotNumber = RANDOM.nextInt(100) + 1;  // Random spot number between 1 and 100
        return "Spot " + randomSpotNumber;
    }

    @PostConstruct
    public void initData()
    {
        for (int i = 0; i < 15; i++) {
            Ticket ticket = generateRandomTicket(i, CONCERT_NAMES, "Concert");
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
