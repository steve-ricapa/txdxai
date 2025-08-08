package com.example.txdxai.core.service;

import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.repository.CompanyRepository;
import com.example.txdxai.rest.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Transactional
    public void activateSubscription(Long companyId, String plan) {
        Company c = repo.findById(companyId)
                .orElseThrow(() -> new IllegalStateException("Company not found: " + companyId));

        String normalized = (plan == null ? "STANDARD" : plan.toUpperCase());
        int limit = "ENTERPRISE".equals(normalized) ? 1_000_000 : 100_000;

        c.setSubscriptionPlan(normalized);
        c.setSubscriptionEndDate(LocalDate.now().plusMonths(6));
        c.setTokensUsed(0);
        c.setTokenLimit(limit);

        repo.save(c);
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
