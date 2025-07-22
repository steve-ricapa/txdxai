package com.example.txdxai.rest.controller;


import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.model.TicketStatus;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.service.CompanyService;
import com.example.txdxai.core.service.TicketService;
import com.example.txdxai.core.service.UserService;
import com.example.txdxai.rest.dto.TicketRequest;
import com.example.txdxai.rest.dto.TicketResponse;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import io.opentelemetry.instrumentation.annotations.WithSpan;


import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final ModelMapper modelMapper;
    private final CompanyService companyService;
    private final UserService userService;


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

    @WithSpan ("ticket.create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TicketResponse> create(
            @RequestBody TicketRequest dto,
            Authentication authentication) {

        // 1) Saca el username del JWT
        String username = authentication.getName();

        // 2) Recupera el User y su Company
        User user = userService.findByUsername(username);
        Company company = user.getCompany();

        // 3) Construye la entidad Ticket
        Ticket entity = new Ticket();
        entity.setSubject(dto.getSubject());
        entity.setDescription(dto.getDescription());
        entity.setUser(user);
        entity.setCompany(company);

        // 4) Guarda y transforma a DTO respuesta
        Ticket saved = ticketService.create(entity);
        TicketResponse responseDto = modelMapper.map(saved, TicketResponse.class);

        return ResponseEntity
                .created(URI.create("/api/tickets/" + saved.getId()))
                .body(responseDto);
    }

    @WithSpan("ticket.getById")
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id) {
        // findById lanza ResourceNotFoundException si no existe
        Ticket ticket = ticketService.findById(id);
        TicketResponse dto = modelMapper.map(ticket, TicketResponse.class);
        return ResponseEntity.ok(dto);

    }

    @WithSpan("ticket.updateStatus")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
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
    @WithSpan("ticket.delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}