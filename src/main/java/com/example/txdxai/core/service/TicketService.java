package com.example.txdxai.core.service;

import com.example.txdxai.core.model.Role;
import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.model.TicketStatus;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.TicketRepository;
import com.example.txdxai.core.repository.UserRepository;
import com.example.txdxai.email.event.EmailEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher events;





    @Transactional
    public Ticket updateStatus(Long id, TicketStatus status) {
        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + id));
        t.setStatus(status);
        Ticket updated = ticketRepository.save(t);

        // ------------- disparo del correo -------------
        if (status == TicketStatus.DERIVED) {
            // ejemplo de destinatario fijo: pepito@gmail.com
            String to      = "steve.ricapa@gmail.com";
            String subject = "Ticket derivado: #" + updated.getId();
            String content = "El ticket con ID " + updated.getId()
                    + " ha cambiado a estado DERIVED.\n"
                    + "Asunto: " + updated.getSubject() + "\n"
                    + "Descripción: " + updated.getDescription();
            events.publishEvent(new EmailEvent(this, to, subject, content));
        }
        // -----------------------------------------------

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

    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    public Ticket create(Ticket ticket) {
        return ticketRepository.save(ticket);
    }


    public void delete(Long id) {
        ticketRepository.deleteById(id);
    }
}