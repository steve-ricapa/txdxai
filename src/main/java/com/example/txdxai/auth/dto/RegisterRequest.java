package com.example.txdxai.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private CompanyDto company;

    @Data
    public static class CompanyDto {
        private String name;
    }
}