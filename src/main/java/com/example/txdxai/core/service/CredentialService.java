package com.example.txdxai.core.service;

import com.example.txdxai.core.model.Credential;
import com.example.txdxai.core.model.CredentialType;
import com.example.txdxai.core.model.Role;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.CredentialRepository;
import com.example.txdxai.core.repository.UserRepository;
import com.example.txdxai.rest.exception.ResourceNotFoundException;
import com.example.txdxai.rest.exception.UnauthorizeOperationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final UserRepository       userRepository;
    private final StringEncryptor      encryptor;

    /**
     * Añade una nueva credencial a la compañía del Admin autenticado.
     */
    @Transactional
    public Credential addCredential(String adminUsername,
                                    CredentialType type,
                                    String apiKeyPlain) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin no encontrado: " + adminUsername)
                );

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizeOperationException("Solo ADMIN puede añadir credenciales");
        }

        String encryptedKey = encryptor.encrypt(apiKeyPlain);
        Credential credential = new Credential();
        credential.setType(type);
        credential.setApiKeyEncrypted(encryptedKey);
        credential.setCompany(admin.getCompany());

        return credentialRepository.save(credential);
    }

    /**
     * Devuelve la API key original (descifrada) de una credencial existente,
     * verificando que el llamador sea Admin de la misma compañía.
     */
    @Transactional
    public String getApiKeyPlain(String adminUsername, Long credentialId) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin no encontrado: " + adminUsername)
                );

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizeOperationException("Solo ADMIN puede acceder a las credenciales");
        }

        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Credencial no encontrada: " + credentialId)
                );

        if (!credential.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new UnauthorizeOperationException("No tienes permiso para acceder a esta credencial");
        }

        return encryptor.decrypt(credential.getApiKeyEncrypted());
    }
}