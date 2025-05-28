package com.example.txdxai.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateUserRequest {

    @NotBlank
    private String username;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;

}