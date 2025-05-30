package com.example.txdxai.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private Long companyId;
    private Long userId;
    private String subject;
    private String description;
    private String status;

    public TicketResponse(Long id, String subject, String status) {
        this.id = id;
        this.companyId = companyId;
        this.userId = userId;
        this.subject = subject;
        this.description = description;
        this.status = status;
    }
}