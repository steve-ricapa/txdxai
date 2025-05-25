package com.example.txdxai.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TicketRequest {
    private Long companyId;
    private Long userId;
    private String subject;
    private String description;
}
