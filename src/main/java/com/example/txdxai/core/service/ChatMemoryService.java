package com.example.txdxai.core.service;


import com.example.txdxai.core.model.ChatMemoryEntry;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.ChatMemoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMemoryService {

    private final ChatMemoryRepository repo;

    public ChatMemoryService(ChatMemoryRepository repo) {
        this.repo = repo;
    }

    public List<ChatMemoryEntry> getAllByUser(User user) {
        return repo.findByUserOrderByTimestampAsc(user);
    }

    public ChatMemoryEntry addEntry(ChatMemoryEntry entry) {
        return repo.save(entry);
    }

    public void deleteEntriesForUser(User user) {
        List<ChatMemoryEntry> entries = repo.findByUserOrderByTimestampAsc(user);
        repo.deleteAll(entries);
    }
}