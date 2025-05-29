package com.example.txdxai.core.service;


import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.UserRepository;
import com.example.txdxai.rest.exception.ResourceNotFoundException;
import com.example.txdxai.rest.exception.UserAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public List<User> findAllByCompany(Long companyId) {
        return repo.findByCompanyId(companyId);
    }

    public List<User> findAll() {
        return repo.findAll();
    }

    public User findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado con id: " + id)
                );
    }

    public User findByUsername(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado: " + username)
                );
    }

    public User create(User user) {
        if (repo.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(
                    "El username '" + user.getUsername() + "' ya está en uso"
            );
        }
        return repo.save(user);
    }

    public User update(Long id, User updated) {
        User existing = repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado con id: " + id)
                );

        // Opcional: impedir cambiar username a uno ya existente
        if (!existing.getUsername().equals(updated.getUsername())
                && repo.existsByUsername(updated.getUsername())) {
            throw new UserAlreadyExistsException(
                    "El username '" + updated.getUsername() + "' ya está en uso"
            );
        }

        // Copiamos solo los campos modificables
        existing.setUsername(updated.getUsername());
        existing.setEmail(updated.getEmail());
        // si permites cambiar rol o company, ajústalo aquí

        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        repo.deleteById(id);
    }

    //agregado
    public Optional<User> findOptionalByUsername(String username) {
        return repo.findByUsername(username);
    }
}