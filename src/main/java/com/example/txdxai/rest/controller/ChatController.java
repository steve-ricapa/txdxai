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
    public ResponseEntity<String> chat(@RequestBody ChatRequest body) {
        // 1. Obtén la autenticación del SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Si no hay usuario autenticado, devuelve 401
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No estás autenticado");
        }

        // 3. Usa el nombre del usuario como conversationId
        String conversationId = authentication.getName();

        // 4. Llama al AiService con un ID no-nulo
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