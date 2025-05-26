package com.example.txdxai.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Long companyId; // ID de la empresa a la que se asocia el usuario
}