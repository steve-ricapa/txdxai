package com.example.txdxai.core.service;

import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.model.TicketStatus;
import com.example.txdxai.core.repository.TicketRepository;
import com.example.txdxai.core.repository.UserRepository;
import com.example.txdxai.rest.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository          ticketRepository;
    private final UserRepository            userRepository;    // si lo usas en otros métodos
    private final ApplicationEventPublisher events;

    @Transactional
    public Ticket updateStatus(Long id, TicketStatus status) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket no encontrado: " + id)
                );

        ticket.setStatus(status);
        Ticket updated = ticketRepository.save(ticket);

        if (status == TicketStatus.DERIVED) {
            String to      = "steve.ricapa@gmail.com";
            String subject = "Ticket derivado: #" + updated.getId();
            String content = "El ticket con ID " + updated.getId()
                    + " ha cambiado a estado DERIVED.\n"
                    + "Asunto: " + updated.getSubject() + "\n"
                    + "Descripción: " + updated.getDescription();

            events.publishEvent(new com.example.txdxai.email.event.EmailEvent(this, to, subject, content));
        }

        return updated;
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public List<Ticket> findAllByCompany(Long companyId) {
        return ticketRepository.findByCompanyId(companyId);
    }

    public List<Ticket> findAllByUser(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket no encontrado: " + id)
                );
    }

    public Ticket create(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public void delete(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket no encontrado: " + id);
        }
        ticketRepository.deleteById(id);
    }
}