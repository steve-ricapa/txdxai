package com.example.txdxai.ai.tool;

import com.example.txdxai.core.service.CredentialService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import jakarta.annotation.PostConstruct;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Component
public class WazuhService {

    private static final String API_ENDPOINT = "/agents";

    /**
     * Desactiva la verificación SSL para permitir conexiones
     * a managers con certificados autofirmados (solo testing).
     */
    static {
        try {
            TrustManager[] trustAll = new TrustManager[]{ new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }};
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAll, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to disable SSL verification", e);
        }
    }

    private final CredentialService credentialService;
    private final RestTemplate restTemplate = new RestTemplate();

    public WazuhService(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @Tool(
            name  = "listWazuhAgents",
            value = "Lista todos los agentes registrados en el manager de Wazuh"
    )
    public List<Map<String, Object>> listAgentsTool(
            @ToolMemoryId String conversationId,
            @P("ID de la credencial de Wazuh") Long credentialId) {

        // 1) Extrae el usuario de la sesión
        String adminUsername = conversationId.replaceFirst("-conversation$", "");

        // 2) Descifra las credenciales
        String managerIp = credentialService.getManagerIpPlain(adminUsername, credentialId);
        String apiPort   = credentialService.getApiPortPlain(adminUsername, credentialId);
        String apiUser   = credentialService.getApiUserPlain(adminUsername, credentialId);
        String apiPass   = credentialService.getApiPasswordPlain(adminUsername, credentialId);

        String baseUrl = "https://" + managerIp + ":" + apiPort;

        // 3) Autenticación básica para obtener token Bearer
        String authUrl = baseUrl + "/security/user/authenticate";
        HttpHeaders authHeaders = new HttpHeaders();
        String basicAuth = Base64.getEncoder()
                .encodeToString((apiUser + ":" + apiPass).getBytes());
        authHeaders.set("Authorization", "Basic " + basicAuth);
        authHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> authEntity = new HttpEntity<>(authHeaders);

        ResponseEntity<Map> authResponse = restTemplate.exchange(
                authUrl, HttpMethod.POST, authEntity, Map.class);

        System.out.println("🚀 Wazuh Auth Status: " + authResponse.getStatusCode());
        System.out.println("🚀 Wazuh Auth Body:   " + authResponse.getBody());

        if (authResponse.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Fallo al autenticar en Wazuh: "
                    + authResponse.getStatusCode());
        }

        @SuppressWarnings("unchecked")
        Map<String,Object> authBody = authResponse.getBody();
        String token = ((Map<String,Object>)authBody.get("data")).get("token").toString();

        // 4) Llamada GET a /agents con Bearer token
        String agentsUrl = baseUrl + API_ENDPOINT;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> resp = restTemplate.exchange(
                agentsUrl, HttpMethod.GET, entity, Map.class);

        System.out.println("🚀 Wazuh GET /agents Status: " + resp.getStatusCode());
        System.out.println("🚀 Wazuh GET /agents Body:   " + resp.getBody());

        if (resp.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Error al obtener agentes de Wazuh: "
                    + resp.getStatusCode());
        }

        // 5) Parseo correcto de "affected_items"
        @SuppressWarnings("unchecked")
        Map<String,Object> body    = resp.getBody();
        @SuppressWarnings("unchecked")
        Map<String,Object> dataMap = (Map<String,Object>) body.get("data");

        Object affected = dataMap.get("affected_items");
        if (affected instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> agents = (List<Map<String,Object>>) affected;
            System.out.println("🚀 Parsed agents: " + agents);
            return agents;
        } else {
            System.out.println("🚀 No se encontró 'affected_items'");
            return Collections.emptyList();
        }
    }
    @Tool(
            name  = "listWazuhAgentAlerts",
            value = "Lista las alertas (eventos) de un agente de Wazuh"
    )
    public List<Map<String, Object>> listAgentAlertsTool(
            @ToolMemoryId String conversationId,
            @P("ID de la credencial de Wazuh") Long credentialId,
            @P("ID del agente") String agentId,
            @P("Máximo número de alertas a traer") Integer limit) {

        // Extrae adminUsername, descifra creds, obtiene baseUrl y token (igual que en listAgentsTool)…
        String adminUsername = conversationId.replaceFirst("-conversation$", "");
        String managerIp = credentialService.getManagerIpPlain(adminUsername, credentialId);
        String apiPort   = credentialService.getApiPortPlain(adminUsername, credentialId);
        String apiUser   = credentialService.getApiUserPlain(adminUsername, credentialId);
        String apiPass   = credentialService.getApiPasswordPlain(adminUsername, credentialId);
        String baseUrl   = "https://" + managerIp + ":" + apiPort;

        // 1) Authentication (igual que antes)…
        String authUrl = baseUrl + "/security/user/authenticate";
        HttpHeaders authHeaders = new HttpHeaders();
        String basic = Base64.getEncoder().encodeToString((apiUser + ":" + apiPass).getBytes());
        authHeaders.set("Authorization", "Basic " + basic);
        authHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> authEntity = new HttpEntity<>(authHeaders);

        ResponseEntity<Map> authResp = restTemplate.exchange(
                authUrl, HttpMethod.POST, authEntity, Map.class);

        if (authResp.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Error auth Wazuh: " + authResp.getStatusCode());
        }
        @SuppressWarnings("unchecked")
        String token = ((Map<String,Object>)((Map<String,Object>)authResp.getBody()).get("data"))
                .get("token").toString();

        // 2) Llamada a /alerts?agent_id={agentId}&limit={limit}
        String alertsUrl = String.format("%s/alerts?agent_id=%s&limit=%d",
                baseUrl, agentId, limit);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> resp = restTemplate.exchange(
                alertsUrl, HttpMethod.GET, entity, Map.class);

        if (resp.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Error al obtener alertas: " + resp.getStatusCode());
        }

        @SuppressWarnings("unchecked")
        Map<String,Object> body    = resp.getBody();
        @SuppressWarnings("unchecked")
        Map<String,Object> dataMap = (Map<String,Object>) body.get("data");

        Object affected = dataMap.get("affected_items");
        if (affected instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> alerts = (List<Map<String,Object>>) affected;
            return alerts;
        } else {
            return Collections.emptyList();
        }
    }
}