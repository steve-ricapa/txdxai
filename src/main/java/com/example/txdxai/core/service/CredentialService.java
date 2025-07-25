package com.example.txdxai.core.service;


import com.example.txdxai.core.model.Credential;
import com.example.txdxai.core.model.CredentialType;
import com.example.txdxai.core.model.Role;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.repository.CredentialRepository;
import com.example.txdxai.core.repository.UserRepository;
import com.example.txdxai.rest.dto.CreateCredentialRequest;
import com.example.txdxai.rest.dto.CredentialDetailDto;
import com.example.txdxai.rest.exception.ResourceConflictException;
import com.example.txdxai.rest.exception.ResourceNotFoundException;
import com.example.txdxai.rest.exception.UnauthorizeOperationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;

import org.springframework.stereotype.Service;

import java.util.List;


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
                        new ResourceNotFoundException("Admin no encontrado: " + adminUsername)
                );

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizeOperationException("Solo ADMIN puede añadir credenciales");
        }

        // 2) Validación por tipo
        switch (req.getType()) {
            case MERAKI -> {
                if (req.getApiKey() == null || req.getApiKey().isBlank()) {
                    throw new ResourceConflictException(
                            "Para MERAKI debes proporcionar apiKey"
                    );
                }
            }
            case NESSUS, WAZUH, SPLUNK -> {
                if (req.getManagerIp() == null
                        || req.getApiPort() == null
                        || req.getApiUser() == null
                        || req.getApiPassword() == null) {
                    throw new ResourceConflictException(
                            "Para " + req.getType() +
                                    " debes proporcionar managerIp, apiPort, apiUser y apiPassword"
                    );
                }
            }
            default -> throw new ResourceConflictException(
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
            throw new UnauthorizeOperationException("No tienes permiso para esta credencial");
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
            throw new UnauthorizeOperationException("No tienes permiso para esta credencial");
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
            throw new UnauthorizeOperationException("No tienes permiso para esta credencial");
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
            throw new UnauthorizeOperationException("No tienes permiso para esta credencial");
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
            throw new UnauthorizeOperationException("No tienes permiso para esta credencial");
        }

        return encryptor.decrypt(credential.getAPI_PASSWORD_ENCRYPTED());
    }

    @Transactional
    public List<Credential> getAllCredentials(String adminUsername) {
        // Verificar que es un admin
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin no encontrado: " + adminUsername));

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizeOperationException("Solo ADMIN puede listar credenciales");
        }

        // Obtener solo las credenciales de la compañía del admin
        return credentialRepository.findByCompanyId(admin.getCompany().getId());
    }

    @Transactional
    public CredentialDetailDto getCredentialDetails(String adminUsername, Long credentialId) {
        // 1) Verificar admin
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin no encontrado: " + adminUsername)
                );

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizeOperationException("Solo ADMIN puede acceder a las credenciales");
        }

        // 2) Obtener credencial
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Credencial no encontrada: " + credentialId)
                );

        // 3) Verificar pertenencia a la misma compañía
        if (!credential.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new UnauthorizeOperationException("No tienes permiso para esta credencial");
        }

        // 4) Construir DTO con valores descifrados
        CredentialDetailDto dto = new CredentialDetailDto(credential.getId(), credential.getType());

        if (credential.getType() == CredentialType.MERAKI) {
            dto.setApiKey(encryptor.decrypt(credential.getApiKeyEncrypted()));
        } else {
            // WAZUH, SPLUNK
            dto.setManagerIp(encryptor.decrypt(credential.getMANAGER_IP()));
            dto.setApiPort(encryptor.decrypt(credential.getAPI_PORT()));
            dto.setApiUser(encryptor.decrypt(credential.getAPI_USER()));
            dto.setApiPassword(encryptor.decrypt(credential.getAPI_PASSWORD_ENCRYPTED()));
        }

        return dto;
    }

}