package com.example.txdxai.core.repository;

import com.example.txdxai.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
//public interface  CompanyRepository   extends JpaRepository<Company, Long>