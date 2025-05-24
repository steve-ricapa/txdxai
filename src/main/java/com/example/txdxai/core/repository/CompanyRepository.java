package com.example.txdxai.core.repository;

import com.example.txdxai.core.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

//Quiro que extienda de JpaRepository
public interface  CompanyRepository   extends JpaRepository<Company, Long> {
    // Aqui se pueden agregar metodos para buscar por nombre, id, etc
    // Por ejemplo:
    // List<Company> findByName(String name);
    // List<Company> findById(Long id);
    // List<Company> findByNameAndId(String name, Long id);
}
