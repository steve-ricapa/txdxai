package com.example.txdxai.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private String name;

    // Nuevos campos para suscripción
    private LocalDate subscriptionEndDate;
    private Integer tokenLimit;
    private Integer tokensUsed;
    private String subscriptionPlan;
}
