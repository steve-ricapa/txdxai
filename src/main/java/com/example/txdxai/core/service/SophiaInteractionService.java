package com.example.txdxai.core.service;

import com.example.txdxai.ai.agent.SophiaService;
import com.example.txdxai.core.model.ChatMemoryEntry;
import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.Sender;
import com.example.txdxai.core.model.User;
import dev.langchain4j.service.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SophiaInteractionService {

    private final SophiaService sophiaService;
    private final TokenUsageService tokenUsageService;
    private final ChatMemoryService chatMemoryService;

    public String interact(String message, User user) {
        Company company = user.getCompany();

        // (1) Calcular tokens de entrada (prompt)
        int inputTokens = tokenUsageService.countTokens(message);

        // (2) Verificar si se pueden usar (basado en tokens de empresa)
        tokenUsageService.checkTokenAvailability(company, inputTokens);

        // (3) Guardar entrada del usuario
        ChatMemoryEntry userEntry = ChatMemoryEntry.builder()
                .message(message)
                .sender(Sender.USER)
                .user(user)
                .inputTokens(inputTokens)
                .outputTokens(0)
                .totalTokens(inputTokens)
                .build();
        chatMemoryService.addEntry(userEntry);

        // (4) Enviar a Sophia
        Result<String> result = sophiaService.query(user.getId().toString(), message);
        String response = result.content();

        // (5) Calcular tokens de salida (respuesta)
        int outputTokens = tokenUsageService.countTokens(response);

        // (6) Crear entrada con respuesta
        ChatMemoryEntry aiEntry = ChatMemoryEntry.builder()
                .message(response)
                .sender(Sender.AGENT)
                .user(user)
                .inputTokens(0)
                .outputTokens(outputTokens)
                .totalTokens(outputTokens)
                .build();

        // (7) Registrar token usage final
        tokenUsageService.registerTokenUsage(company, user, aiEntry, inputTokens, outputTokens);
        chatMemoryService.addEntry(aiEntry);

        return response;
    }
}
