package com.example.txdxai.rest.controller;


import com.example.txdxai.core.model.ChatMemoryEntry;
import com.example.txdxai.core.service.ChatMemoryService;
import com.example.txdxai.rest.dto.ChatRequest;
import com.example.txdxai.rest.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SophiaAgent sophiaAgent;
    private final ChatMemoryService memoryService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        // Guarda mensaje de usuario
        ChatMemoryEntry userEntry = ChatMemoryEntry.builder()
                .userId(request.getUserId())
                .sender("USER")
                .message(request.getMessage())
                .timestamp(Instant.now())
                .build();
        memoryService.addEntry(userEntry);

        // Lógica AI
        String reply = sophiaAgent.query(request.getMessage(), request.getUserId());

        // Guarda respuesta del agente
        ChatMemoryEntry agentEntry = ChatMemoryEntry.builder()
                .userId(request.getUserId())
                .sender("AGENT")
                .message(reply)
                .timestamp(Instant.now())
                .build();
        memoryService.addEntry(agentEntry);

        return ResponseEntity.ok(new ChatResponse(reply));
    }
}