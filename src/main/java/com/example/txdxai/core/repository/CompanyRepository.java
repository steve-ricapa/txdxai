package com.example.txdxai.core.repository;

import com.example.txdxai.core.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Quiro que extienda de JpaRepository
public interface  CompanyRepository   extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);

}
