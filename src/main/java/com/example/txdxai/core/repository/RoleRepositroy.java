package com.example.txdxai.core.repository;

import com.example.txdxai.core.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepositroy extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}

