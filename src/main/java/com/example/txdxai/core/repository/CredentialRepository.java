package com.example.txdxai.core.repository;


import com.example.txdxai.core.model.Credential;
import com.example.txdxai.core.model.CredentialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CredentialRepository extends JpaRepository<Credential,Long> {
    List<Credential> findByCompanyId(Long companyId);
    List<Credential> findByType(CredentialType type);
}
