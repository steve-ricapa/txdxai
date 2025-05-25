package com.example.txdxai.core.service;


import com.example.txdxai.core.model.Credential;
import com.example.txdxai.core.model.CredentialType;
import com.example.txdxai.core.repository.CredentialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CredentialService {

    private final CredentialRepository repo;

    public CredentialService(CredentialRepository repo) {
        this.repo = repo;
    }

    public List<Credential> findAllByCompany(Long companyId) {
        return repo.findByCompanyId(companyId);
    }

    public List<Credential> findByType(CredentialType type) {
        return repo.findByType(type);
    }

    public Credential create(Credential credential) {
        return repo.save(credential);
    }

    public Credential update(Long id, Credential updated) {
        updated.setId(id);
        return repo.save(updated);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}