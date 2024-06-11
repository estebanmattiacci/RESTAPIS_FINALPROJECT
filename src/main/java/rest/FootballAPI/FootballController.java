package rest.FootballAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Profile(" football")
@RestController
@RequestMapping("/api/football-tickets")
public class FootballController {

    private final FootballService ticketService;

    @Autowired
    public FootballController(FootballService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> getAllTickets() {
        List<EntityModel<Ticket>> tickets = ticketService.findAll().stream()
                .map(ticket -> EntityModel.of(ticket,
                        linkTo(methodOn(FootballController.class).getTicketById(ticket.getId())).withSelfRel(),
                        linkTo(methodOn(FootballController.class).getAllTickets()).withRel("tickets"),
                        linkTo(methodOn(FootballController.class).placeOrder(ticket.getId())).withRel("order")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(tickets,
                linkTo(methodOn(FootballController.class).getAllTickets()).withSelfRel()));
        
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Ticket>> getTicketById(@PathVariable String id) {
        Optional<Ticket> ticket = ticketService.findByID(id);
        return ticket.map(t -> ResponseEntity.ok(
                        EntityModel.of(t,
                                linkTo(methodOn(FootballController.class).getTicketById(t.getId())).withSelfRel(),
                                linkTo(methodOn(FootballController.class).getAllTickets()).withRel("tickets"),
                                linkTo(methodOn(FootballController.class).placeOrder(t.getId())).withRel("order"))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<Ticket>> createTicket(@RequestBody Ticket ticket) {
        Ticket savedTicket = ticketService.save(ticket);
        return ResponseEntity.created(
                        linkTo(methodOn(FootballController.class).getTicketById(savedTicket.getId())).toUri())
                .body(EntityModel.of(savedTicket,
                        linkTo(methodOn(FootballController.class).getTicketById(savedTicket.getId())).withSelfRel(),
                        linkTo(methodOn(FootballController.class).getAllTickets()).withRel("tickets")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Ticket>> updateTicket(@PathVariable String id, @RequestBody Ticket ticket) {
        Optional<Ticket> optionalTicket = ticketService.findByID(id);
        if (optionalTicket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ticket.setId(id);
        Ticket updatedTicket = ticketService.save(ticket);
        return ResponseEntity.ok(
                EntityModel.of(updatedTicket,
                        linkTo(methodOn(FootballController.class).getTicketById(updatedTicket.getId())).withSelfRel(),
                        linkTo(methodOn(FootballController.class).getAllTickets()).withRel("tickets")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable String id) {
        ticketService.deleteByID(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/order")
    public ResponseEntity<EntityModel<Ticket>> placeOrder(@PathVariable String id) {
        Optional<Ticket> optionalTicket = ticketService.findByID(id);
        if (optionalTicket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Ticket ticket = optionalTicket.get();
        ticket.setStatus("Booked");
        Ticket updatedTicket = ticketService.save(ticket);
        return ResponseEntity.ok(
                EntityModel.of(updatedTicket,
                        linkTo(methodOn(FootballController.class).getTicketById(updatedTicket.getId())).withSelfRel(),
                        linkTo(methodOn(FootballController.class).getAllTickets()).withRel("tickets"),
                        linkTo(methodOn(FootballController.class).placeOrder(updatedTicket.getId())).withRel("order")));
    }
}
