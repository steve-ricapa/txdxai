package com.example.txdxai.rest.controller;


import com.example.txdxai.ai.agent.SophiaService;
import com.example.txdxai.ai.tool.MerakiService;
import com.example.txdxai.core.model.*;
import com.example.txdxai.core.service.ChatMemoryService;
import com.example.txdxai.core.service.UserService;
import com.example.txdxai.rest.dto.ChatMemoryEntryDto;
import com.example.txdxai.rest.dto.ChatRequest;
import com.example.txdxai.rest.dto.ChatResponse;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SophiaService sophiaService;
    private final ChatMemoryProvider chatMemoryProvider;
    private final MerakiService merakiService;
    private final ChatMemoryService chatMemoryService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request,
                                             Authentication auth) {
        // 1) Identidad y sesión
        String username       = auth.getName();
        String conversationId = username + "-conversation";

        // 2) Carga o crea la memoria de esta sesión
        ChatMemory memory = chatMemoryProvider.get(conversationId);

        // 3) Inyecta las organizaciones de Meraki solo la primera vez
        if (memory.messages().isEmpty()) {
            // --- aquí reemplaza la lógica antigua por la nueva ---
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
            var company    = user.getCompany();
            var merakiCred = company.getCredentials().stream()
                    .filter(c -> c.getType() == CredentialType.MERAKI)
                    .findFirst();

            if (merakiCred.isPresent()) {
                Long merakiCredId = merakiCred.get().getId();
                try {
                    List<Map<String, Object>> orgList = merakiService
                            .listOrganizationsTool(conversationId, merakiCredId);

                    String orgs = orgList.stream()
                            .map(o -> o.get("name") + " (ID:" + o.get("id") + ")")
                            .collect(Collectors.joining(", "));

                    memory.add(SystemMessage.from("Organizaciones de Meraki disponibles: " + orgs));
                } catch (Exception e) {
                    // Si falla la inyección de Meraki, lo logueamos pero no abortamos
                    System.err.println("Error al inyectar orgs de Meraki: " + e.getMessage());
                }
            }
            // si no hay creds MERAKI, se salta sin lanzar error
        }

        // 4) Persiste el mensaje del usuario en BD
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
        String message = request.getMessage();
        chatMemoryService.addEntry(
                ChatMemoryEntry.builder()
                        .user(user)
                        .sender(Sender.USER)
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        // 5) Llamada a Sophia con contexto y reglas de ticket
        Result<String> result = sophiaService.query(conversationId, message);
        String reply = result.content();

        // 6) Persiste la respuesta del agente
        chatMemoryService.addEntry(
                ChatMemoryEntry.builder()
                        .user(user)
                        .sender(Sender.AGENT)
                        .message(reply)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        return ResponseEntity.ok(new ChatResponse(reply));
    }

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMemoryEntryDto>> history(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                org.springframework.http.HttpStatus.BAD_REQUEST,
                                "Usuario no encontrado"
                        )
                );

        List<ChatMemoryEntryDto> dtos = chatMemoryService
                .getRecent20ByUser(user)          // ya devuelve los últimos 20 en orden ascendente
                .stream()
                .map(e -> new ChatMemoryEntryDto(
                        e.getId(),
                        e.getMessage(),
                        e.getSender().name(),
                        e.getTimestamp()
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }
}