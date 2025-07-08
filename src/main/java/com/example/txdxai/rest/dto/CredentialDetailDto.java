package com.example.txdxai.rest.dto;

import com.example.txdxai.core.model.CredentialType;
import lombok.Data;

@Data
public class CredentialDetailDto {
    private Long id;
    private CredentialType type;
    private String apiKey;
    private String managerIp;
    private String apiPort;
    private String apiUser;
    private String apiPassword;

    public CredentialDetailDto(Long id, CredentialType type) {
        this.id = id;
        this.type = type;
    }
}
