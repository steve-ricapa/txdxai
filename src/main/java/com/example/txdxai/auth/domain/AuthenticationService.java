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
        // Crear o reusar la compañía indicada
        Company company = companyRepository
                .findByName(request.getCompany().getName())
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(request.getCompany().getName());
                    newCompany.setCreatedAt(LocalDateTime.now());
                    return companyRepository.save(newCompany);
                });

        // Crear usuario Admin
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setCompany(company);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        // Generar token JWT
        String token = jwtService.generateToken(user);
        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(token);
        return response;
    }

    /**
     * Autenticación de usuario existente: LOGIN
     */
    public JwtAuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .   orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generateToken(user);
        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(token);
        return response;
    }

    /**
     * Creación de un nuevo User por un Admin autenticado
     */
    public JwtAuthResponse createUserAsAdmin(CreateUserRequest request) {
        // Verificar que el llamador sea un Admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new UnauthorizeOperationException("Solo ADMIN puede crear nuevos usuarios");
        }


        // Recuperar la compañía del Admin que realiza la petición
        String adminUsername = auth.getName();
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Admin no encontrado: " + adminUsername));

        // 3) Validar unicidad de username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken: " + request.getUsername());
        }

        // Crear nuevo usuario con rol USER
        User user = new User();
        Company company = admin.getCompany(); // Usar la misma compañía del Admin
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setCompany(company);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        // Generar token JWT para el nuevo usuario
        String token = jwtService.generateToken(user);
        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(token);
        return response;
    }
}
