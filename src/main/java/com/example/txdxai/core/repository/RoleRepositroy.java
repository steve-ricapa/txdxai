package com.example.txdxai.core.repository;

import com.example.txdxai.core.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepositroy extends JpaRepository<Role, Long> {
}

//public interface  CompanyRepository   extends JpaRepository<Company, Long>