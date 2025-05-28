package com.example.txdxai.rest.dto;


import com.example.txdxai.core.model.CredentialType;
import lombok.Data;

@Data
public class CredentialDto {
    private Long            id;
    private CredentialType  type;
    // No devolvemos la apiKey cifrada
}