package com.example.txdxai.core.repository;

import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCompanyId(Long companyId);
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByStatus(TicketStatus status);
}
