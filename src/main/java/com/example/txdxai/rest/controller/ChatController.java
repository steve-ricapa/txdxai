package com.example.txdxai.rest.controller;


import com.example.txdxai.ai.agent.SophiaService;
import com.example.txdxai.core.model.ChatMemoryEntry;
import com.example.txdxai.core.service.ChatMemoryService;
import com.example.txdxai.rest.dto.ChatRequest;
import com.example.txdxai.rest.dto.ChatResponse;
import dev.langchain4j.service.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;


@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final SophiaService sophiaService;

    public ChatController(SophiaService sophiaService) {
        this.sophiaService = sophiaService;
    }


    @PostMapping
    @PreAuthorize("isAuthenticated()")  // sólo permite peticiones con un JWT válido
    public ResponseEntity<String> chat(@RequestBody ChatRequest body) {
        // Extraemos el usuario autenticado del JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        String username = auth.getName();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no valido");
        }

        String conversationId = username + "-conversation";  // ahora dinámico por usuario

        String response = sophiaService
                .query(conversationId, body.getMessage())
                .content();

        return ResponseEntity.ok(response);
    }

    public static class ChatRequest {
        private String message;
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}