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




@Service
@RequiredArgsConstructor
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final UserRepository       userRepository;
    private final StringEncryptor      encryptor;

    /**
     * Añade una nueva credencial a la compañía del Admin autenticado.
     * @param adminUsername nombre de usuario del Admin (debe existir y ser ROLE_ADMIN)
     * @param type          tipo de credencial (SPLUNK, MERAKI, WAZUH)
     * @param apiKeyPlain   clave API en texto claro
     * @return la entidad Credential guardada
     */
    @Transactional
    public Credential addCredential(String adminUsername,
                                    CredentialType type,
                                    String apiKeyPlain) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new IllegalStateException("Admin no encontrado"));
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Solo ADMIN puede añadir credenciales");
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
     * @param adminUsername usuario que solicita la clave
     * @param credentialId  id de la credencial
     * @return la API key en texto claro
     */
    @Transactional
    public String getApiKeyPlain(String adminUsername, Long credentialId) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new IllegalStateException("Admin no encontrado"));
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Solo ADMIN puede acceder a las credenciales");
        }
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new IllegalArgumentException("Credencial no existe"));
        if (!credential.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new AccessDeniedException("No tienes permiso para esta credencial");
        }
        return encryptor.decrypt(credential.getApiKeyEncrypted());
    }
}