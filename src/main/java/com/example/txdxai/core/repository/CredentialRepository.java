package com.example.txdxai.core.repository;


import com.example.txdxai.core.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

//Lo mismo que el de arriba pero con otro nombre JPA
public interface CredentialRepository extends JpaRepository<Credential,Long> {
}
//public interface  CompanyRepository   extends JpaRepository<Company, Long>