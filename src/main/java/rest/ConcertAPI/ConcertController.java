package rest.ConcertAPI;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest.Ticket;

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
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> getAllTickets() {
        List<EntityModel<Ticket>> tickets = ticketService.findAll().stream()
                .map(ticket -> {
                    EntityModel<Ticket> ticketModel = EntityModel.of(ticket,
                            linkTo(methodOn(ConcertController.class).getTicketById(ticket.getId())).withSelfRel(),
                            linkTo(methodOn(ConcertController.class).getAllTickets()).withRel("tickets"),
                            linkTo(methodOn(ConcertController.class).placeOrder(ticket.getId())).withRel("order"));
                    if ("Available".equals(ticket.getStatus())) {
                        ticketModel.add(linkTo(methodOn(ConcertController.class).reserveTicket(ticket.getId())).withRel("reserve"));
                    }
                    return ticketModel;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(tickets,
                linkTo(methodOn(ConcertController.class).getAllTickets()).withSelfRel()));
    }



    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Ticket>> getTicketById(@PathVariable String id) {
        Optional<Ticket> ticket = ticketService.findByID(id);
        return ticket.map(t -> {
            EntityModel<Ticket> ticketModel = EntityModel.of(t,
                    linkTo(methodOn(ConcertController.class).getTicketById(t.getId())).withSelfRel(),
                    linkTo(methodOn(ConcertController.class).getAllTickets()).withRel("tickets"),
                    linkTo(methodOn(ConcertController.class).placeOrder(t.getId())).withRel("order"));
            if ("Available".equals(t.getStatus())) {
                ticketModel.add(linkTo(methodOn(ConcertController.class).reserveTicket(t.getId())).withRel("reserve"));
            }
            return ResponseEntity.ok(ticketModel);
        }).orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<EntityModel<Ticket>> createTicket(@RequestBody Ticket ticket) {
        Ticket savedTicket = ticketService.save(ticket);
        return ResponseEntity.created(
                        linkTo(methodOn(ConcertController.class).getTicketById(savedTicket.getId())).toUri())
                .body(EntityModel.of(savedTicket,
                        linkTo(methodOn(ConcertController.class).getTicketById(savedTicket.getId())).withSelfRel(),
                        linkTo(methodOn(ConcertController.class).getAllTickets()).withRel("tickets")));
    }


    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Ticket>> updateTicket(@PathVariable String id, @RequestBody Ticket ticket) {
        Optional<Ticket> optionalTicket = ticketService.findByID(id);
        if (optionalTicket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ticket.setId(id);
        Ticket updatedTicket = ticketService.save(ticket);
        EntityModel<Ticket> ticketModel = EntityModel.of(updatedTicket,
                linkTo(methodOn(ConcertController.class).getTicketById(updatedTicket.getId())).withSelfRel(),
                linkTo(methodOn(ConcertController.class).getAllTickets()).withRel("tickets"));
        if ("Available".equals(updatedTicket.getStatus())) {
            ticketModel.add(linkTo(methodOn(ConcertController.class).reserveTicket(updatedTicket.getId())).withRel("reserve"));
        }
        return ResponseEntity.ok(ticketModel);
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
                        linkTo(methodOn(ConcertController.class).getTicketById(updatedTicket.getId())).withSelfRel(),
                        linkTo(methodOn(ConcertController.class).getAllTickets()).withRel("tickets"),
                        linkTo(methodOn(ConcertController.class).placeOrder(updatedTicket.getId())).withRel("order")));
    }


    @PostMapping("/{id}/reserve")
    public ResponseEntity<EntityModel<Ticket>> reserveTicket(@PathVariable String id) {
        Optional<Ticket> optionalTicket = ticketService.reserveTicket(id);
        if (optionalTicket.isEmpty()) {
            return ResponseEntity.status(409).body(null);
        }
        Ticket ticket = optionalTicket.get();
        EntityModel<Ticket> ticketModel = EntityModel.of(ticket,
                linkTo(methodOn(ConcertController.class).getTicketById(ticket.getId())).withSelfRel(),
                linkTo(methodOn(ConcertController.class).getAllTickets()).withRel("tickets"),
                linkTo(methodOn(ConcertController.class).placeOrder(ticket.getId())).withRel("order"));
        if ("Available".equals(ticket.getStatus())) {
            ticketModel.add(linkTo(methodOn(ConcertController.class).reserveTicket(ticket.getId())).withRel("reserve"));
        }
        return ResponseEntity.ok(ticketModel);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<EntityModel<Ticket>> cancelOrder(@PathVariable String id) {
        Optional<Ticket> optionalTicket = ticketService.findByID(id);
        if (optionalTicket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Ticket ticket = optionalTicket.get();
        ticket.setStatus("Available");
        ticket.setReservedUntil(null);
        Ticket updatedTicket = ticketService.save(ticket);
        return ResponseEntity.ok(
                EntityModel.of(updatedTicket,
                        linkTo(methodOn(ConcertController.class).getTicketById(updatedTicket.getId())).withSelfRel(),
                        linkTo(methodOn(ConcertController.class).getAllTickets()).withRel("tickets"),
                        linkTo(methodOn(ConcertController.class).placeOrder(updatedTicket.getId())).withRel("order")));
    }
}
