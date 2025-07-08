package com.example.txdxai.rest.controller;

import com.example.txdxai.core.model.Credential;
import com.example.txdxai.core.service.CredentialService;
import com.example.txdxai.rest.dto.CreateCredentialRequest;
import com.example.txdxai.rest.dto.CredentialDetailDto;
import com.example.txdxai.rest.dto.CredentialDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/credentials")
@PreAuthorize("hasRole('ADMIN')")    // <— se aplica a todos los métodos
@RequiredArgsConstructor
@Validated
public class CredentialController {

    private final CredentialService credentialService;

    @PostMapping
    public ResponseEntity<CredentialDto> addCredential(
            @Valid @RequestBody CreateCredentialRequest req,
            Authentication auth
    ) {
        String adminUsername = auth.getName();
        Credential created = credentialService.addCredential(adminUsername, req);

        CredentialDto dto = new CredentialDto(created.getId(), created.getType());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CredentialDetailDto> getCredentialDetails(
            @PathVariable Long id,
            Authentication auth
    ) {
        String adminUsername = auth.getName();
        CredentialDetailDto dto = credentialService.getCredentialDetails(adminUsername, id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<CredentialDto>> getAllCredentials(Authentication auth) {
        // Este metodo se mantiene igual, solo devuelve la lista básica
        String adminUsername = auth.getName();
        List<Credential> credentials = credentialService.getAllCredentials(adminUsername);
        List<CredentialDto> dtos = credentials.stream()
                .map(c -> new CredentialDto(c.getId(), c.getType()))
                .toList();
        return ResponseEntity.ok(dtos);
    }
}