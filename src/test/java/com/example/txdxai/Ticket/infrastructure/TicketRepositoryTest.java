package com.example.txdxai.Ticket.infrastructure;

import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.model.TicketStatus;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.Assert.assertEquals;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void findByCompanyId_ShouldFilterTickets() {
        // Arrange
        Company company = new Company();
        company.setName("TestCompany");
        entityManager.persist(company);

        Ticket ticket = new Ticket();
        ticket.setCompany(company);
        ticket.setSubject("Test Ticket");
        ticket.setStatus(TicketStatus.PENDING);
        entityManager.persist(ticket);

        // Act
        List<Ticket> tickets = ticketRepository.findByCompanyId(company.getId());

        // Assert
        assertEquals(1, tickets.size());
        assertEquals("Test Ticket", tickets.get(0).getSubject());
    }

    @Test
    void findByUserId_ShouldReturnUserTickets() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        entityManager.persist(user);

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setSubject("User Ticket");
        entityManager.persist(ticket);

        // Act
        List<Ticket> tickets = ticketRepository.findByUserId(user.getId());

        // Assert
        assertEquals(1, tickets.size());
        assertEquals("User Ticket", tickets.get(0).getSubject());
    }

    @Test
    void findByStatus_ShouldFilterByStatus() {
        // Arrange
        Ticket pendingTicket = new Ticket();
        pendingTicket.setStatus(TicketStatus.PENDING);
        entityManager.persist(pendingTicket);

        Ticket executedTicket = new Ticket();
        executedTicket.setStatus(TicketStatus.EXECUTED);
        entityManager.persist(executedTicket);

        // Act
        List<Ticket> pendingTickets = ticketRepository.findByStatus(TicketStatus.PENDING);

        // Assert
        assertEquals(1, pendingTickets.size());
        assertEquals(TicketStatus.PENDING, pendingTickets.get(0).getStatus());
    }
}
