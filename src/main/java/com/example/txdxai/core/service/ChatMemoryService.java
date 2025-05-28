package com.example.txdxai.core.service;

import com.example.txdxai.core.model.ChatMemoryEntry;
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

    /** Guarda cada entrada */
    public ChatMemoryEntry addEntry(ChatMemoryEntry entry) {
        return repo.save(entry);
    }

    /** Recupera TODO el historial (si lo necesitas) */
    public List<ChatMemoryEntry> getAllByUser(User user) {
        return repo.findByUserOrderByTimestampAsc(user);
    }

    /** Recupera los últimos 20 mensajes, en orden cronológico */
    public List<ChatMemoryEntry> getRecent20ByUser(User user) {
        List<ChatMemoryEntry> recentDesc =
                repo.findTop20ByUserOrderByTimestampDesc(user);
        Collections.reverse(recentDesc);
        return recentDesc;
    }

    /** Recupera los últimos N mensajes, en orden cronológico */
    public List<ChatMemoryEntry> getRecentByUser(User user, int limit) {
        Pageable pg = PageRequest.of(0, limit);
        List<ChatMemoryEntry> recentDesc =
                repo.findByUserOrderByTimestampDesc(user, pg);
        Collections.reverse(recentDesc);
        return recentDesc;
    }

    /** Limpia todo el historial de un usuario */
    public void deleteEntriesForUser(User user) {
        List<ChatMemoryEntry> entries = repo.findByUserOrderByTimestampAsc(user);
        repo.deleteAll(entries);
    }
}