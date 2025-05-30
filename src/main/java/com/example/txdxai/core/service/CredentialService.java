package com.example.txdxai.core.service;


import com.example.txdxai.core.model.Credential;
import com.example.txdxai.core.model.CredentialType;
import com.example.txdxai.core.model.Role;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.CredentialRepository;
import com.example.txdxai.core.repository.UserRepository;
import com.example.txdxai.rest.dto.CreateCredentialRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


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
                                    CreateCredentialRequest req) {
        // 1) Verifica Admin
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin no encontrado")
                );

        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Solo ADMIN puede añadir credenciales");
        }

        // 2) Validación por tipo
        switch (req.getType()) {
            case MERAKI -> {
                if (req.getApiKey() == null || req.getApiKey().isBlank()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Para MERAKI debes proporcionar apiKey"
                    );
                }
            }
            case WAZUH, SPLUNK -> {
                if (req.getManagerIp() == null
                        || req.getApiPort() == null
                        || req.getApiUser() == null
                        || req.getApiPassword() == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Para " + req.getType() +
                                    " debes proporcionar managerIp, apiPort, apiUser y apiPassword"
                    );
                }
            }
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo desconocido: " + req.getType()
            );
        }

        // 3) Construcción de la entidad y encriptación de todos los campos sensibles
        Credential credential = new Credential();
        credential.setType(req.getType());
        credential.setCompany(admin.getCompany());

        if (req.getType() == CredentialType.MERAKI) {
            credential.setApiKeyEncrypted(encryptor.encrypt(req.getApiKey()));
        } else {
            // WAZUH y SPLUNK: encriptamos managerIp, apiPort, apiUser, apiPassword
            credential.setMANAGER_IP(encryptor.encrypt(req.getManagerIp()));
            credential.setAPI_PORT(encryptor.encrypt(req.getApiPort()));
            credential.setAPI_USER(encryptor.encrypt(req.getApiUser()));
            credential.setAPI_PASSWORD_ENCRYPTED(encryptor.encrypt(req.getApiPassword()));
        }

        // 4) Persistir
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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Admin no encontrado"));
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

    /**
     * Devuelve la dirección IP del manager descifrada de una credencial existente,
     * verificando que el llamador sea Admin de la misma compañía.
     * @param adminUsername usuario que solicita la IP
     * @param credentialId  id de la credencial
     * @return la IP escriturada en texto claro
     */
    @Transactional
    public String getManagerIpPlain(String adminUsername, Long credentialId) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Admin no encontrado"));
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Solo ADMIN puede acceder a las credenciales");
        }

        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new IllegalArgumentException("Credencial no existe"));
        if (!credential.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new AccessDeniedException("No tienes permiso para esta credencial");
        }

        return encryptor.decrypt(credential.getMANAGER_IP());
    }

    /**
     * Devuelve el puerto de la API descifrado de una credencial existente,
     * verificando que el llamador sea Admin de la misma compañía.
     * @param adminUsername usuario que solicita el puerto
     * @param credentialId  id de la credencial
     * @return el puerto en texto claro
     */
    @Transactional
    public String getApiPortPlain(String adminUsername, Long credentialId) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Admin no encontrado"));
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Solo ADMIN puede acceder a las credenciales");
        }

        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new IllegalArgumentException("Credencial no existe"));
        if (!credential.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new AccessDeniedException("No tienes permiso para esta credencial");
        }

        return encryptor.decrypt(credential.getAPI_PORT());
    }

    /**
     * Devuelve el usuario de la API descifrado de una credencial existente,
     * verificando que el llamador sea Admin de la misma compañía.
     * @param adminUsername usuario que solicita el nombre de usuario
     * @param credentialId  id de la credencial
     * @return el nombre de usuario en texto claro
     */
    @Transactional
    public String getApiUserPlain(String adminUsername, Long credentialId) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Admin no encontrado"));
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Solo ADMIN puede acceder a las credenciales");
        }

        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new IllegalArgumentException("Credencial no existe"));
        if (!credential.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new AccessDeniedException("No tienes permiso para esta credencial");
        }

        return encryptor.decrypt(credential.getAPI_USER());
    }

    /**
     * Devuelve la contraseña de la API descifrada de una credencial existente,
     * verificando que el llamador sea Admin de la misma compañía.
     * @param adminUsername usuario que solicita la contraseña
     * @param credentialId  id de la credencial
     * @return la contraseña en texto claro
     */
    @Transactional
    public String getApiPasswordPlain(String adminUsername, Long credentialId) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Admin no encontrado"));
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Solo ADMIN puede acceder a las credenciales");
        }

        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new IllegalArgumentException("Credencial no existe"));
        if (!credential.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new AccessDeniedException("No tienes permiso para esta credencial");
        }

        return encryptor.decrypt(credential.getAPI_PASSWORD_ENCRYPTED());
    }
}