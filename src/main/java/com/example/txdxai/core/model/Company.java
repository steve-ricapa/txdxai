package com.example.txdxai.core.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Credential> credentials;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Company(Long id) {
        this.id = id;
    }

    //añadimos los nuvos campos de la empresa

    @Column(name = "subscription_end_date")
    private LocalDate subscriptionEndDate;

    @Column(name = "token_limit")
    private Integer tokenLimit;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @Column(name = "subscription_plan", length = 50)
    private String subscriptionPlan;

}