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

    @Column(name = "api_key_encrypted", nullable = true)
    private String apiKeyEncrypted;

    @Column(name = "manager_ip", nullable = true)
    private String MANAGER_IP;

    @Column(name = "api_port", nullable = true)
    private String API_PORT;

    @Column(name = "api_user", nullable = true)
    private String API_USER;

    @Column(name = "api_password_encrypted", nullable = true)
    private String API_PASSWORD_ENCRYPTED;




    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
