package com.example.txdxai.auth.controller;

import com.example.txdxai.auth.domain.AuthenticationService;
import com.example.txdxai.auth.dto.InitialRegisterRequest;
import com.example.txdxai.auth.dto.JwtAuthResponse;
import com.example.txdxai.auth.dto.LoginRequest;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @GetMapping("/hello")
    public String hello() {
        return "La nube funciona!";
    }

    @WithSpan("auth.register")
    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> initialRegister(
            @Validated @RequestBody InitialRegisterRequest request) {
        JwtAuthResponse resp = authService.registerFirstAdmin(request);
        return ResponseEntity.ok(resp);
    }

    @WithSpan("auth.login")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(
            @Validated @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}