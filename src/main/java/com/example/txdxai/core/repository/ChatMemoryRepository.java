package com.example.txdxai.core.repository;

//Quiro que este repositorio extienda de JpaRepository
import com.example.txdxai.core.model.ChatMemoryEntry;
import com.example.txdxai.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMemoryRepository extends JpaRepository<ChatMemoryEntry, Long> {
    List<ChatMemoryEntry> findByUserOrderByTimestampAsc(User user);

}
