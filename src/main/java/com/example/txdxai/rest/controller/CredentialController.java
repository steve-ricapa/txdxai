package com.example.txdxai.rest.controller;

import com.example.txdxai.core.model.Credential;
import com.example.txdxai.core.service.CredentialService;
import com.example.txdxai.rest.dto.CreateCredentialRequest;
import com.example.txdxai.rest.dto.CredentialDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/credentials")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CredentialController {

    private final CredentialService credentialService;

    @PostMapping
    public ResponseEntity<CredentialDto> addCredential(
            @Validated @RequestBody CreateCredentialRequest req,
            Authentication auth
    ) {
        String adminUsername = auth.getName();
        Credential created = credentialService.addCredential(
                adminUsername,
                req.getType(),
                req.getApiKey()
        );

        CredentialDto dto = new CredentialDto();
        dto.setId(created.getId());
        dto.setType(created.getType());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}