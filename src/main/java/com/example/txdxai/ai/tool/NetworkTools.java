package com.example.txdxai.ai.tool;


import com.example.txdxai.auth.domain.CustomUserDetailsService;
import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.service.TicketService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class NetworkTools {

    private final SplunkService splunkService;
    private final MerakiService merakiService;
    private final WazuhTools wazuhTools;
    private final TicketService ticketService;
    private final CustomUserDetailsService userDetailsService;

    public NetworkTools(SplunkService splunkService,
                        MerakiService merakiService,
                        WazuhTools wazuhTools,
                        TicketService ticketService,
                        CustomUserDetailsService userDetailsService) {
        this.splunkService = splunkService;
        this.merakiService = merakiService;
        this.wazuhTools = wazuhTools;
        this.ticketService = ticketService;
        this.userDetailsService = userDetailsService;
    }

    @Tool("Consulta eventos en Splunk con la query dada")
    public String searchSplunkEvents(String query) {
        return splunkService.searchEvents(query);
    }

    @Tool("Devuelve estado de un dispositivo Meraki por su ID")
    public String getMerakiDeviceStatus(String deviceId) {
        return merakiService.getDeviceStatus(deviceId);
    }

    @Tool("Busca alertas críticas en Wazuh usando un filtro")
    public String searchWazuhAlerts(String filter) {
        return wazuhTools.searchAlerts(filter);
    }

    @Tool("Crea un ticket de red solicitando asunto y descripción")
    public String createNetworkTicket(String subject, String description) {
        // Obtiene el usuario actualmente autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = (User) userDetailsService.loadUserByUsername(username);
        Company company = currentUser.getCompany();

        // Construye el ticket
        Ticket ticket = new Ticket();
        ticket.setUser(currentUser);
        ticket.setCompany(company);
        ticket.setSubject(subject);
        ticket.setDescription(description);
        // status y createdAt asignados en @PrePersist

        Ticket saved = ticketService.create(ticket);
        return "Ticket creado con ID: " + saved.getId();
    }
}
