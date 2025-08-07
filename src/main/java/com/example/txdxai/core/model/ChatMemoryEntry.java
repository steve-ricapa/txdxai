package com.example.txdxai.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat_memory")
public class ChatMemoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sender sender; // USER o AGENT

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Nuevo: Relación directa con la empresa para facilitar queries
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false) //temporalmente cambiamos el nullable a true
    private Company company;

    // Nuevo: Nombre del agente ("SOPHIA", "VICTORIA", etc.)
    @Column(nullable = false)
    private String agentName;

    @Column(name = "input_tokens")
    private Integer inputTokens;


    @Column(name = "output_tokens")
    private Integer outputTokens;

    @Column(name = "total_tokens")
    private Integer totalTokens;


    private String modelUsed;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    public int getTotalTokens() {
        return (inputTokens != null ? inputTokens : 0) + (outputTokens != null ? outputTokens : 0);
    }
}
