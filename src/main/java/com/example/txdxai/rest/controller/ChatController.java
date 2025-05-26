package com.example.txdxai.rest.controller;


import com.example.txdxai.ai.agent.SophiaService;
import com.example.txdxai.core.model.ChatMemoryEntry;
import com.example.txdxai.core.service.ChatMemoryService;
import com.example.txdxai.rest.dto.ChatRequest;
import com.example.txdxai.rest.dto.ChatResponse;
import dev.langchain4j.service.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;


@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final SophiaService sophiaService;
    private final HttpServletRequest request;

    public ChatController(SophiaService sophiaService, HttpServletRequest request) {
        this.sophiaService = sophiaService;
        this.request = request;
    }

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest body) {
        // Usa el nombre de usuario autenticado como conversationId
        String conversationId = request.getUserPrincipal().getName();
        // Llama al servicio AI y obtiene la respuesta con .content()
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