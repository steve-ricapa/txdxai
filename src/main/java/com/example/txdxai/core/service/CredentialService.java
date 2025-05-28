package com.example.txdxai.core.service;


import com.example.txdxai.core.model.Credential;
import com.example.txdxai.core.model.CredentialType;
import com.example.txdxai.core.model.Role;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.CredentialRepository;
import com.example.txdxai.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final UserRepository       userRepository;
    private final StringEncryptor encryptor;

    /**
     * Añade una nueva credencial a la compañía del Admin autenticado.
     *
     * @param adminUsername nombre de usuario del Admin (debe existir y ser ROLE_ADMIN)
     * @param type          tipo de credencial (SPLUNK, MERAKI, WAZUH)
     * @param apiKeyPlain   clave API en texto claro
     * @return la entidad Credential guardada
     */


    @Transactional
    public Credential addCredential(String adminUsername,
                                    CredentialType type,
                                    String apiKeyPlain) {
        // 1) Recuperar al Admin
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new IllegalStateException("Admin no encontrado"));

        // 2) Verificar rol
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Solo ADMIN puede añadir credenciales");
        }

        // 3) Cifrar la API key
        String encryptedKey = encryptor.encrypt(apiKeyPlain);

        // 4) Construir y guardar la credencial
        Credential credential = new Credential();
        credential.setType(type);
        credential.setApiKeyEncrypted(encryptedKey);
        credential.setCompany(admin.getCompany());

        return credentialRepository.save(credential);
    }
}