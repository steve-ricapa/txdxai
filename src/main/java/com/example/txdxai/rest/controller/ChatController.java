package com.example.txdxai.rest.controller;


import com.example.txdxai.ai.agent.SophiaService;
import com.example.txdxai.core.model.ChatMemoryEntry;
import com.example.txdxai.core.model.Sender;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.service.ChatMemoryService;
import com.example.txdxai.core.service.UserService;
import com.example.txdxai.rest.dto.ChatRequest;
import com.example.txdxai.rest.dto.ChatResponse;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SophiaService sophiaService;
    private final ChatMemoryService chatMemoryService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest body,
                                             Authentication authentication) {
        // 1) Obtengo el username del token
        String username = authentication.getName();

        // 2) Busco al User por username
        User user = userService.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado")
                );

        String message = body.getMessage();

        // 3) Persiste entrada de usuario
        ChatMemoryEntry userEntry = ChatMemoryEntry.builder()
                .user(user)
                .sender(Sender.USER)
                .message(message)
                .build();
        chatMemoryService.addEntry(userEntry);

        // 4) Llamada a Sophia
        String conversationId = username + "-conversation";
        Result<String> result = sophiaService.query(conversationId, message);
        String reply = result.content();

        // 5) Persiste respuesta de Sophia
        ChatMemoryEntry aiEntry = ChatMemoryEntry.builder()
                .user(user)
                .sender(Sender.AGENT)
                .message(reply)
                .build();
        chatMemoryService.addEntry(aiEntry);

        // 6) Devuelvo respuesta
        return ResponseEntity.ok(new ChatResponse(reply));
    }
}