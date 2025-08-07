package com.example.txdxai.rest.dto;

import lombok.Data;

@Data
public class TokenUsageDto {
    private Long companyId;
    private int tokensUsed; // Tokens a registrar (sumar)
}
