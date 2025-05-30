package com.example.txdxai.Company.infrastructure;

import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;

@DataJpaTest
class CompanyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void findByName_ShouldReturnCompany() {
        // Arrange
        Company company = new Company();
        company.setName("TechCorp");
        entityManager.persistAndFlush(company);

        // Act
        Optional<Company> found = companyRepository.findByName("TechCorp");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("TechCorp", found.get().getName());
    }

    @Test
    void findByName_ShouldBeCaseSensitive() {
        // Arrange
        Company company = new Company();
        company.setName("Apple");
        entityManager.persistAndFlush(company);

        // Act
        Optional<Company> found = companyRepository.findByName("apple"); // Diferente casing

        // Assert
        assertFalse(found.isPresent()); // Debería ser case-sensitive
    }

    @Test
    void save_ShouldSetCreatedAtAutomatically() {
        // Arrange
        Company newCompany = new Company();
        newCompany.setName("NewCo");

        // Act
        Company saved = companyRepository.save(newCompany);

        // Assert
        assertNotNull(saved.getCreatedAt());
        assertTrue(saved.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}