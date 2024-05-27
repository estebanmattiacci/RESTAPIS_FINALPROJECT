package rest.MovieAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Profile("movie")
@RestController
@RequestMapping("/api/movie-tickets")
public class MovieController {

    private final MovieService ticketService;

    @Autowired
    public MovieController(MovieService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<EntityModel<MovieTicket>> getAllTickets() {
        List<MovieTicket> tickets = ticketService.findAll();
        return tickets.stream()
                .map(ticket -> EntityModel.of(ticket,
                        linkTo(methodOn(MovieController.class).getTicketById(ticket.getId())).withSelfRel(),
                        linkTo(methodOn(MovieController.class).getAllTickets()).withRel("tickets")))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<MovieTicket>> getTicketById(@PathVariable String id) {
        Optional<MovieTicket> ticket = ticketService.findByID(id);
        return ticket.map(t -> ResponseEntity.ok(
                        EntityModel.of(t,
                                linkTo(methodOn(MovieController.class).getTicketById(t.getId())).withSelfRel(),
                                linkTo(methodOn(MovieController.class).getAllTickets()).withRel("tickets"))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<MovieTicket>> createTicket(@RequestBody MovieTicket ticket) {
        MovieTicket savedTicket = ticketService.save(ticket);
        return ResponseEntity.created(
                        linkTo(methodOn(MovieController.class).getTicketById(savedTicket.getId())).toUri())
                .body(EntityModel.of(savedTicket,
                        linkTo(methodOn(MovieController.class).getTicketById(savedTicket.getId())).withSelfRel(),
                        linkTo(methodOn(MovieController.class).getAllTickets()).withRel("tickets")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<MovieTicket>> updateTicket(@PathVariable String id, @RequestBody MovieTicket ticket) {
        Optional<MovieTicket> optionalTicket = ticketService.findByID(id);
        if (optionalTicket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ticket.setId(id);
        MovieTicket updatedTicket = ticketService.save(ticket);
        return ResponseEntity.ok(
                EntityModel.of(updatedTicket,
                        linkTo(methodOn(MovieController.class).getTicketById(updatedTicket.getId())).withSelfRel(),
                        linkTo(methodOn(MovieController.class).getAllTickets()).withRel("tickets")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable String id) {
        ticketService.deleteByID(id);
        return ResponseEntity.noContent().build();
    }
}
