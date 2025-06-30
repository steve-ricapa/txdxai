package com.example.txdxai.Ticket.application;

import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.Ticket;
import com.example.txdxai.core.model.*;
import com.example.txdxai.core.service.TicketService;
import com.example.txdxai.core.service.UserService;
import com.example.txdxai.rest.controller.TicketController;
import com.example.txdxai.rest.dto.TicketRequest;
import com.example.txdxai.rest.dto.TicketResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TicketController.class)
class TicketControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private TicketService ticketService;
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Autowired
//    private UserService userService;

//    @Test
//    @WithMockUser(username = "testuser")
//    void createTicket_ShouldSetUserAndCompanyFromAuth() throws Exception {
//        // Arrange
//        User mockUser = new User();
//        mockUser.setId(1L);
//        mockUser.setUsername("testuser");
//        Company mockCompany = new Company();
//        mockCompany.setId(100L);
//        mockUser.setCompany(mockCompany);
//
//        TicketRequest requestDto = new TicketRequest();
//        requestDto.setSubject("Test Subject");
//        requestDto.setDescription("Test Description");
//
//        Ticket savedTicket = new Ticket();
//        savedTicket.setId(1L);
//        savedTicket.setUser(mockUser);
//        savedTicket.setCompany(mockCompany);
//
//        Mockito.when(userService.findByUsername("testuser")).thenReturn(mockUser);
//        Mockito.when(ticketService.create(Mockito.any(Ticket.class))).thenReturn(savedTicket);
//        Mockito.when(modelMapper.map(Mockito.any(), Mockito.eq(TicketResponse.class)))
//                .thenReturn(new TicketResponse(1L, "Test Subject", "PENDING"));
//
//        // Act & Assert
//        mockMvc.perform(post("/api/tickets")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                           {
//                               "subject": "Test Subject",
//                               "description": "Test Description"
//                           }
//                          """))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.status").value("PENDING"));
//    }
//
//    @Test
//    void updateStatus_ShouldHandleValidActions() throws Exception {
//        // Arrange
//        Ticket updatedTicket = new Ticket();
//        updatedTicket.setId(1L);
//        updatedTicket.setStatus(TicketStatus.DERIVED);
//
//        Mockito.when(ticketService.updateStatus(1L, TicketStatus.DERIVED))
//                .thenReturn(updatedTicket);
//        Mockito.when(modelMapper.map(updatedTicket, TicketResponse.class))
//                .thenReturn(new TicketResponse(1L, "Test", "DERIVED"));
//
//        // Act & Assert
//        mockMvc.perform(put("/api/tickets/1?action=derive"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("DERIVED"));
//    }
//
//    @Test
//    void listTickets_ShouldFilterByCompanyId() throws Exception {
//        // Arrange
//        Ticket ticket = new Ticket();
//        ticket.setId(1L);
//        ticket.setSubject("Company Ticket");
//
//        Mockito.when(ticketService.findAllByCompany(100L)).thenReturn(List.of(ticket));
//        Mockito.when(modelMapper.map(ticket, TicketResponse.class))
//                .thenReturn(new TicketResponse(1L, "Company Ticket", "PENDING"));
//
//        // Act & Assert
//        mockMvc.perform(get("/api/tickets?companyId=100"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].subject").value("Company Ticket"));
//    }
}