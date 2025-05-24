package com.example.txdxai.core.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "credentials")
@Data
@NoArgsConstructor
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CredentialType type; // SPLUNK, MERAKI, WAZUH

    @Column(name = "api_key_encrypted", nullable = false)
    private String apiKeyEncrypted;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
