package com.example.txdxai.core.service;

import com.example.txdxai.core.model.ChatMemoryEntry;
import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.ChatMemoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ChatMemoryService {

    private final ChatMemoryRepository repo;

    public ChatMemoryService(ChatMemoryRepository repo) {
        this.repo = repo;
    }

    /** Guarda entrada y registra los tokens */
    public ChatMemoryEntry addEntryWithTokens(ChatMemoryEntry entry, int inputTokens, int outputTokens, String modelUsed, String agentName) {
        entry.setInputTokens(inputTokens);
        entry.setOutputTokens(outputTokens);
        entry.setModelUsed(modelUsed);
        entry.setAgentName(agentName);

        // Si no se asignó empresa explícitamente, usar la del usuario
        if (entry.getCompany() == null && entry.getUser() != null) {
            Company company = entry.getUser().getCompany();
            if (company != null) {
                entry.setCompany(company);
            }
        }

        return repo.save(entry);
    }

    /** Versión original para mensajes sin tokens (por compatibilidad) */
    public ChatMemoryEntry addEntry(ChatMemoryEntry entry) {
        return repo.save(entry);
    }

    /** Historial completo */
    public List<ChatMemoryEntry> getAllByUser(User user) {
        return repo.findByUserOrderByTimestampAsc(user);
    }

    /** Últimos 20 */
    public List<ChatMemoryEntry> getRecent20ByUser(User user) {
        List<ChatMemoryEntry> recentDesc =
                repo.findTop20ByUserOrderByTimestampDesc(user);
        Collections.reverse(recentDesc);
        return recentDesc;
    }

    /** Últimos N */
    public List<ChatMemoryEntry> getRecentByUser(User user, int limit) {
        Pageable pg = PageRequest.of(0, limit);
        List<ChatMemoryEntry> recentDesc =
                repo.findByUserOrderByTimestampDesc(user, pg);
        Collections.reverse(recentDesc);
        return recentDesc;
    }

    /** Limpiar memoria */
    public void deleteEntriesForUser(User user) {
        List<ChatMemoryEntry> entries = repo.findByUserOrderByTimestampAsc(user);
        repo.deleteAll(entries);
    }
}
