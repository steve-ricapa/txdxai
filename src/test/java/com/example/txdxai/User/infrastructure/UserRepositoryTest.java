package com.example.txdxai.User.infrastructure;

import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_ShouldReturnUser() {
        // Preparar datos
        User user = new User();
        user.setUsername("johndoe");
        user.setPasswordHash("hash");
        user.setEmail("john@example.com");
        entityManager.persistAndFlush(user);

        // Ejecutar consulta
        Optional<User> found = userRepository.findByUsername("johndoe");

        // Verificar
        assertTrue(found.isPresent());
        assertEquals("johndoe", found.get().getUsername());
    }

    @Test
    void existsByUsername_ShouldReturnTrueIfExists() {
        User user = new User();
        user.setUsername("existinguser");
        user.setPasswordHash("hash");
        entityManager.persistAndFlush(user);

        assertTrue(userRepository.existsByUsername("existinguser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void findByCompanyId_ShouldFilterUsers() {
        // Asume que Company ya está persistida con ID 1
        User user1 = new User();
        user1.setCompany(new Company(1L)); // Simula relación
        user1.setUsername("user1");
        entityManager.persistAndFlush(user1);

        List<User> users = userRepository.findByCompanyId(1L);
        assertEquals(1, users.size());
    }
}