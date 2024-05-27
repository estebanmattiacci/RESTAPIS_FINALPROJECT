package rest.ConcertAPI;


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

@Profile("concert")
@RestController
@RequestMapping("/api/concert-tickets")
public class ConcertController {


    private final ConcertService ticketService;

    @Autowired
    public ConcertController(ConcertService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<EntityModel<ConcertTicket>> getAllTickets() {
        List<ConcertTicket> tickets = ticketService.findAll();
        return tickets.stream()
                .map(ticket -> EntityModel.of(ticket,
                        linkTo(methodOn(ConcertController.class).getTicketById(ticket.getId())).withSelfRel(),
                        linkTo(methodOn(ConcertController.class).getAllTickets()).withRel("tickets")))
                .collect(Collectors.toList());
    }



    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ConcertTicket>> getTicketById(@PathVariable String id) {
        Optional<ConcertTicket> ticket = ticketService.findByID(id);
        return ticket.map(t -> ResponseEntity.ok(
                        EntityModel.of(t,
                                linkTo(methodOn(ConcertController.class).getTicketById(t.getId())).withSelfRel(),
                                linkTo(methodOn(ConcertController.class).getAllTickets()).withRel("tickets"))))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<EntityModel<ConcertTicket>> createTicket(@RequestBody ConcertTicket ticket) {
        ConcertTicket savedTicket = ticketService.save(ticket);
        return ResponseEntity.created(
                        linkTo(methodOn(ConcertController.class).getTicketById(savedTicket.getId())).toUri())
                .body(EntityModel.of(savedTicket,
                        linkTo(methodOn(ConcertController.class).getTicketById(savedTicket.getId())).withSelfRel(),
                        linkTo(methodOn(ConcertController.class).getAllTickets()).withRel("tickets")));
    }


    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ConcertTicket>> updateTicket(@PathVariable String id, @RequestBody ConcertTicket ticket) {
        Optional<ConcertTicket> optionalTicket = ticketService.findByID(id);
        if (optionalTicket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ticket.setId(id);
        ConcertTicket updatedTicket = ticketService.save(ticket);
        return ResponseEntity.ok(
                EntityModel.of(updatedTicket,
                        linkTo(methodOn(ConcertController.class).getTicketById(updatedTicket.getId())).withSelfRel(),
                        linkTo(methodOn(ConcertController.class).getAllTickets()).withRel("tickets")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable String id) {
        ticketService.deleteByID(id);
        return ResponseEntity.noContent().build();
    }

}
