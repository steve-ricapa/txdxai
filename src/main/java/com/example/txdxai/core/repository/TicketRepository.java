package com.example.txdxai.core.repository;

import com.example.txdxai.core.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
