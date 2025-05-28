package com.example.txdxai.ai.tool;


import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.service.TicketService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketTool {

    private final TicketService ticketService;

    @Autowired
    public TicketTool(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Tool to create a new network ticket.
     *
     * @param conversationId the LangChain conversation ID (uses this to get the username)
     * @param subject        the ticket subject
     * @param description    the ticket description
     * @return a human-readable confirmation message
     */
    @Tool("Create a network ticket")
    public String createNetworkTicketTool(
            @ToolMemoryId String conversationId,
            @P("subject") String subject,
            @P("description") String description) {

        // 1) Obtén el username de conversationId ("steve-conversation" → "steve")
        String username = conversationId.replaceFirst("-conversation$", "");

        // 2) Invoca tu servicio existente
        Ticket ticket = ticketService.createNetworkTicket(
                username,
                subject,
                description
        );

        // 3) Devuelve un mensaje sencillo
        return String.format("Ticket #%d created for user %s with subject \"%s\"",
                ticket.getId(),
                username,
                ticket.getSubject()
        );
    }
}