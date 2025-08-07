package com.example.txdxai.core.service;

import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.repository.CompanyRepository;
import com.example.txdxai.rest.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository repo;

    public CompanyService(CompanyRepository repo) {
        this.repo = repo;
    }

    public List<Company> findAll() {
        return repo.findAll();
    }

    public Optional<Company> findById(Long id) {
        return repo.findById(id);
    }

    public Company create(Company company) {
        return repo.save(company);
    }

    public Company update(Long id, Company updated) {
        updated.setId(id);
        return repo.save(updated);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public void registerTokenUsage(Long companyId, int tokensToAdd) {
        Company company = repo.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + companyId));

        if (company.getTokensUsed() == null) {
            company.setTokensUsed(0);
        }

        company.setTokensUsed(company.getTokensUsed() + tokensToAdd);
        repo.save(company);
    }

}