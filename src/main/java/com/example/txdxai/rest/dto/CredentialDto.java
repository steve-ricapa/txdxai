package com.example.txdxai.rest.dto;


import com.example.txdxai.core.model.CredentialType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CredentialDto {
    private Long            id;
    private CredentialType  type;
    // No devolvemos la apiKey cifrada
}