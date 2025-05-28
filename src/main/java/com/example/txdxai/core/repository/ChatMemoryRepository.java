package com.example.txdxai.core.repository;

import com.example.txdxai.core.model.ChatMemoryEntry;
import com.example.txdxai.core.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMemoryRepository extends JpaRepository<ChatMemoryEntry, Long> {

    /**
     * Para tu getAllByUser():
     * recupera TODO el historial ordenado ascendentemente
     */
    List<ChatMemoryEntry> findByUserOrderByTimestampAsc(User user);

    /**
     * Para getRecent20ByUser():
     * recupera los 20 últimos en orden descendente
     */
    List<ChatMemoryEntry> findTop20ByUserOrderByTimestampDesc(User user);

    /**
     * Para getRecentByUser(User, limit):
     * paginado descendente
     */
    List<ChatMemoryEntry> findByUserOrderByTimestampDesc(User user, Pageable pageable);
}
