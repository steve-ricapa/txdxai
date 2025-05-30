package com.example.txdxai.rest.dto;

import com.example.txdxai.core.model.CredentialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCredentialRequest {

    @NotNull
    private CredentialType type;    // SPLUNK, MERAKI o WAZUH


    /** Para MERAKI */
    private String apiKey;

    /** Para WAZUH y SPLUNK */
    private String managerIp;
    private String apiPort;
    private String apiUser;
    private String apiPassword;
}