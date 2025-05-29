package com.example.txdxai.ai.tool;


import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.service.CompanyService;
import com.example.txdxai.core.service.TicketService;
import com.example.txdxai.core.service.UserService;
import com.example.txdxai.rest.dto.TicketRequest;
import com.example.txdxai.rest.dto.TicketResponse;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TicketTool {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketTool(TicketService ticketService,
                      UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @Tool(
            name  = "create_ticket",
            value = "Crea un nuevo ticket con los datos proporcionados"
    )
    public TicketResponse createTicket(@P("Datos del ticket a crear: 'subject', 'description'") TicketRequest request) {
        // Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));

        // Obtener compañía del usuario
        Company company = user.getCompany();

        // Validar que request contiene subject y description
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new IllegalArgumentException("Falta el campo 'subject' para crear el ticket.");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Falta el campo 'description' para crear el ticket.");
        }

        // Mapear DTO a entidad
        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setCompany(company);
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());

        // Crear ticket
        Ticket saved = ticketService.create(ticket);

        // Convertir entidad a DTO
        return new TicketResponse(
                saved.getId(),
                company.getId(),
                user.getId(),
                saved.getSubject(),
                saved.getDescription(),
                saved.getStatus().name()
        );
    }

    @Tool(
            name  = "get_ticket",
            value = "Obtiene un ticket dado su ID"
    )
    public TicketResponse getTicketById(@P("ID del ticket") Long id) {
        Ticket ticket = ticketService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + id));
        return toDto(ticket);
    }

    @Tool(
            name  = "list_tickets",
            value = "Lista todos los tickets registrados"
    )
    public List<TicketResponse> listTickets() {
        return ticketService.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Helper para convertir Entidad → DTO
    private TicketResponse toDto(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getCompany().getId(),
                ticket.getUser().getId(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus().name()
        );
    }
}