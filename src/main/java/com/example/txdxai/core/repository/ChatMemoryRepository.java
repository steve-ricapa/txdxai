package com.example.txdxai.core.repository;

//Quiro que este repositorio extienda de JpaRepository
import com.example.txdxai.core.model.ChatMemoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ChatMemoryRepository extends JpaRepository<ChatMemoryEntry, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
}
