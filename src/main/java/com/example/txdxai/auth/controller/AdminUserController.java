package com.example.txdxai.auth.controller;

import com.example.txdxai.auth.domain.AuthenticationService;
import com.example.txdxai.auth.dto.CreateUserRequest;
import com.example.txdxai.auth.dto.JwtAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AuthenticationService authService;

    @PostMapping
    public ResponseEntity<JwtAuthResponse> createUser(
            @Validated @RequestBody CreateUserRequest request) {
        JwtAuthResponse resp = authService.createUserAsAdmin(request);
        return ResponseEntity.ok(resp);
    }
}