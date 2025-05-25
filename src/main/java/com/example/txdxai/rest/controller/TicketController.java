package com.example.txdxai.rest.controller;


import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.model.TicketStatus;
import com.example.txdxai.core.service.TicketService;
import com.example.txdxai.rest.dto.TicketRequest;
import com.example.txdxai.rest.dto.TicketResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<TicketResponse> list(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long userId) {

        List<Ticket> tickets = (companyId != null)
                ? ticketService.findAllByCompany(companyId)
                : (userId != null)
                ? ticketService.findAllByUser(userId)
                : ticketService.findAll();

        return tickets.stream()
                .map(t -> modelMapper.map(t, TicketResponse.class))
                .toList();
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@RequestBody TicketRequest dto) {
        Ticket entity = modelMapper.map(dto, Ticket.class);
        Ticket saved  = ticketService.create(entity);
        TicketResponse responseDto = modelMapper.map(saved, TicketResponse.class);
        return ResponseEntity
                .created(URI.create("/api/tickets/" + saved.getId()))
                .body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id) {
        return ticketService.findById(id)
                .map(t -> modelMapper.map(t, TicketResponse.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam("action") String action) {

        TicketStatus status = switch (action.toLowerCase()) {
            case "confirm" -> TicketStatus.EXECUTED;
            case "cancel"  -> TicketStatus.FAILED;
            case "derive"  -> TicketStatus.DERIVED;
            default        -> throw new IllegalArgumentException("Acción inválida: " + action);
        };

        Ticket updated = ticketService.updateStatus(id, status);
        return ResponseEntity.ok(modelMapper.map(updated, TicketResponse.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}