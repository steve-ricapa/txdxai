package com.example.txdxai.core.service;

import com.example.txdxai.core.model.ChatMemoryEntry;
import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.User;
import com.example.txdxai.rest.exception.LimitExceededException;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import org.springframework.stereotype.Service;

@Service
public class TokenUsageService {

    private final Encoding encoding;

    public TokenUsageService() {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        this.encoding = registry.getEncodingForModel("gpt-4").orElseThrow();
    }

    public int countTokens(String text) {
        return encoding.countTokens(text);
    }

    public void checkTokenAvailability(Company company, int tokensToConsume) {
        if (company.getSubscriptionEndDate().isBefore(java.time.LocalDate.now())) {
            throw new LimitExceededException("La suscripción ha expirado");
        }

        int remaining = company.getTokenLimit() - company.getTokensUsed();
        if (tokensToConsume > remaining) {
            throw new LimitExceededException("Se ha alcanzado el límite de tokens de la empresa");
        }
    }

    public void registerTokenUsage(Company company, User user, ChatMemoryEntry entry, int inputTokens, int outputTokens) {
        int total = inputTokens + outputTokens;

        entry.setInputTokens(inputTokens);
        entry.setOutputTokens(outputTokens);
        entry.setTotalTokens(total);

        company.setTokensUsed(company.getTokensUsed() + total);
    }
}
