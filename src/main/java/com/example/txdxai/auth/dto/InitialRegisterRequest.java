package com.example.txdxai.auth.dto;



import com.example.txdxai.rest.dto.CompanyDto;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
@Data
public class InitialRegisterRequest {

    @NotBlank
    private String username;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private CompanyDto company;   // Requerido sólo aquí
}