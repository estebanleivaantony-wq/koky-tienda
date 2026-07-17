package com.koky.web.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.koky.web.domain.Ticket;
import com.koky.web.dto.TicketRequest;
import com.koky.web.repo.TicketRepo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    @Autowired
    private TicketRepo ticketRepo;

    @PostMapping("/ticket")
    public ResponseEntity<Map<String, String>> createTicket(@RequestBody TicketRequest request) {
        Ticket newTicket = new Ticket(
                request.getName(),
                request.getEmail(),
                request.getSubject(),
                request.getMessage());
        ticketRepo.save(newTicket);
        return ResponseEntity.ok(Collections.singletonMap("message", "Ticket recibido correctamente"));
    }

    // --- NUEVO MÉTODO PARA LEER TICKETS ---
    @GetMapping("/user/{email}")
    public ResponseEntity<List<Ticket>> getUserTickets(@PathVariable String email) {
        List<Ticket> tickets = ticketRepo.findByEmailOrderByCreatedAtDesc(email);
        return ResponseEntity.ok(tickets);
    }
}