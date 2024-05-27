package rest.FootballAPI;

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
    public List<EntityModel<FootballTicket>> getAllTickets() {
        List<FootballTicket> tickets = ticketService.findAll();
        return tickets.stream()
                .map(ticket -> EntityModel.of(ticket,
                        linkTo(methodOn(FootballController.class).getTicketById(ticket.getId())).withSelfRel(),
                        linkTo(methodOn(FootballController.class).getAllTickets()).withRel("tickets")))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<FootballTicket>> getTicketById(@PathVariable String id) {
        Optional<FootballTicket> ticket = ticketService.findByID(id);
        return ticket.map(t -> ResponseEntity.ok(
                        EntityModel.of(t,
                                linkTo(methodOn(FootballController.class).getTicketById(t.getId())).withSelfRel(),
                                linkTo(methodOn(FootballController.class).getAllTickets()).withRel("tickets"))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<FootballTicket>> createTicket(@RequestBody FootballTicket ticket) {
        FootballTicket savedTicket = ticketService.save(ticket);
        return ResponseEntity.created(
                        linkTo(methodOn(FootballController.class).getTicketById(savedTicket.getId())).toUri())
                .body(EntityModel.of(savedTicket,
                        linkTo(methodOn(FootballController.class).getTicketById(savedTicket.getId())).withSelfRel(),
                        linkTo(methodOn(FootballController.class).getAllTickets()).withRel("tickets")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<FootballTicket>> updateTicket(@PathVariable String id, @RequestBody FootballTicket ticket) {
        Optional<FootballTicket> optionalTicket = ticketService.findByID(id);
        if (optionalTicket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ticket.setId(id);
        FootballTicket updatedTicket = ticketService.save(ticket);
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
}
