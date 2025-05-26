package com.example.txdxai.auth.domain;

import com.example.txdxai.auth.config.JwtService;
import com.example.txdxai.auth.dto.JwtAuthResponse;
import com.example.txdxai.auth.dto.LoginRequest;
import com.example.txdxai.auth.dto.RegisterRequest;
import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.Role;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.CompanyRepository;
import com.example.txdxai.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthResponse register(RegisterRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setCompany(company);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER); // Todos se registran como USER

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(token);
        return response;
    }

    public JwtAuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generateToken(user);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(token);
        return response;
    }
}