package com.example.txdxai.auth.domain;

import com.example.txdxai.auth.config.JwtService;
import com.example.txdxai.auth.dto.CreateUserRequest;
import com.example.txdxai.auth.dto.InitialRegisterRequest;
import com.example.txdxai.auth.dto.JwtAuthResponse;
import com.example.txdxai.auth.dto.LoginRequest;
import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.Role;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.CompanyRepository;
import com.example.txdxai.core.repository.UserRepository;
import com.example.txdxai.rest.exception.ResourceNotFoundException;
import com.example.txdxai.rest.exception.UnauthorizeOperationException;
import com.example.txdxai.rest.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registro inicial del primer Admin: crea la compañía y asigna ROLE_ADMIN
     */
    public JwtAuthResponse registerFirstAdmin(InitialRegisterRequest request) {
        // 1) Verificar que no exista usuario con el mismo username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken: " + request.getUsername());
        }
        // 2) Crear o reusar la compañía indicada
        Company company = companyRepository
                .findByName(request.getCompany().getName())
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(request.getCompany().getName());
                    newCompany.setCreatedAt(LocalDateTime.now());
                    return companyRepository.save(newCompany);
                });

        // 3) Crear usuario Admin
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setCompany(company);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        // 4) Generar token JWT
        String token = jwtService.generateToken(user);
        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(token);
        return response;
    }

    /**
     * Autenticación de usuario existente: LOGIN
     */
    public JwtAuthResponse login(LoginRequest request) {
        // 1) Autenticar credenciales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        // 2) Recuperar usuario
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + request.getUsername()));
        // 3) Generar token
        String token = jwtService.generateToken(user);
        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(token);
        return response;
    }

    /**
     * Creación de un nuevo User por un Admin autenticado
     */
    public JwtAuthResponse createUserAsAdmin(CreateUserRequest request) {
        // 1) Verificar rol ADMIN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new UnauthorizeOperationException("Solo ADMIN puede crear nuevos usuarios");
        }
        String adminUsername = auth.getName();
        // 2) Recuperar Admin
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Admin no encontrado: " + adminUsername));

        // 3) Validar unicidad de username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken: " + request.getUsername());
        }

        // 4) Crear nuevo usuario con rol USER
        Company company = admin.getCompany();
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setCompany(company);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        // 5) Generar token
        String token = jwtService.generateToken(user);
        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(token);
        return response;
    }
}
