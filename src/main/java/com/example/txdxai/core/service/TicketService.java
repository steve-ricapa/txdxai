package com.example.txdxai.core.service;

import com.example.txdxai.core.model.Role;
import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.model.TicketStatus;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.TicketRepository;
import com.example.txdxai.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

//    public Ticket createNetworkTicket(String createdBy, String subject, String description) {
//        User user = userRepository.findByUsername(createdBy)
//                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + createdBy));
//
//        if (!(user.getRole() == Role.ADMIN || user.getRole() == Role.USER)) {
//            throw new AccessDeniedException("No tienes permisos para crear tickets");
//        }
//
//        Ticket ticket = Ticket.builder()
//                .subject(subject)
//                .description(description)
//                .status(TicketStatus.PENDING)
//                .createdBy(user)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        return ticketRepository.save(ticket);
//    }
//
//    public Ticket getTicket(String username, Long ticketId) {
//        Ticket ticket = ticketRepository.findById(ticketId)
//                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
//
//        if (user.getRole() == Role.USER && !ticket.getCreatedBy().getUsername().equals(username)) {
//            throw new AccessDeniedException("No tienes acceso a este ticket");
//        }
//        return ticket;
//    }

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

    public Ticket updateStatus(Long id, TicketStatus status) {
        Ticket t = ticketRepository.findById(id).orElseThrow();
        t.setStatus(status);
        return ticketRepository.save(t);
    }

    public void delete(Long id) {
        ticketRepository.deleteById(id);
    }
}