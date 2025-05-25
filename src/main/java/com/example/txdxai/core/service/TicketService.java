package com.example.txdxai.core.service;

import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.model.TicketStatus;
import com.example.txdxai.core.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository repo;

    public TicketService(TicketRepository repo) {
        this.repo = repo;
    }

    public List<Ticket> findAll() {
        return repo.findAll();
    }

    public List<Ticket> findAllByCompany(Long companyId) {
        return repo.findByCompanyId(companyId);
    }

    public List<Ticket> findAllByUser(Long userId) {
        return repo.findByUserId(userId);
    }

    public Optional<Ticket> findById(Long id) {
        return repo.findById(id);
    }

    public Ticket create(Ticket ticket) {
        return repo.save(ticket);
    }

    public Ticket updateStatus(Long id, TicketStatus status) {
        Ticket t = repo.findById(id).orElseThrow();
        t.setStatus(status);
        return repo.save(t);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}