package com.example.txdxai.ai.tool;

import com.example.txdxai.core.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.ToolMemoryId;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MerakiService {

    private static final String BASE_URL = "https://api.meraki.com/api/v1";

    private final CredentialService credentialService;
    private final RestTemplate restTemplate;

    @Autowired
    public MerakiService(CredentialService credentialService) {
        this.credentialService = credentialService;
        this.restTemplate = new RestTemplate();
    }

    private HttpHeaders headers(String adminUsername, Long credentialId) {
        String apiKey = credentialService.getApiKeyPlain(adminUsername, credentialId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Cisco-Meraki-API-Key", apiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private <T> T get(String url, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    @Tool("List all Meraki organizations")
    public List<Map<String, Object>> listOrganizationsTool(
            @ToolMemoryId String conversationId,
            @P("credential id") Long credentialId) {
        String user = conversationId.replaceFirst("-conversation$", "");
        String url = BASE_URL + "/organizations";
        return get(url, headers(user, credentialId), List.class);
    }

    @Tool("List networks in a Meraki organization")
    public List<Map<String, Object>> listNetworksTool(
            @ToolMemoryId String conversationId,
            @P("credential id") Long credentialId,
            @P("organization id") String orgId) {
        String user = conversationId.replaceFirst("-conversation$", "");
        String url = BASE_URL + "/organizations/" + orgId + "/networks";
        return get(url, headers(user, credentialId), List.class);
    }

    @Tool("List devices in a Meraki network")
    public List<Map<String, Object>> listDevicesTool(
            @ToolMemoryId String conversationId,
            @P("credential id") Long credentialId,
            @P("network id") String networkId) {
        String user = conversationId.replaceFirst("-conversation$", "");
        String url = BASE_URL + "/networks/" + networkId + "/devices";
        return get(url, headers(user, credentialId), List.class);
    }

    @Tool("List clients in a Meraki network")
    public List<Map<String, Object>> listClientsTool(
            @ToolMemoryId String conversationId,
            @P("credential id") Long credentialId,
            @P("network id") String networkId) {
        String user = conversationId.replaceFirst("-conversation$", "");
        String url = BASE_URL + "/networks/" + networkId + "/clients?total_pages=all";
        return get(url, headers(user, credentialId), List.class);
    }

    @Tool("Get Meraki subscription end date for an organization")
    public Map<String, Object> getSubscriptionEndDateTool(
            @ToolMemoryId String conversationId,
            @P("credential id") Long credentialId,
            @P("organization id") String orgId) {
        String user = conversationId.replaceFirst("-conversation$", "");
        String url = BASE_URL + "/organizations/" + orgId + "/licenses/overview";
        Map<String, Object> data = get(url, headers(user, credentialId), Map.class);
        Map<String, Object> result = new HashMap<>();
        result.put("expirationDate", data.getOrDefault("expirationDate", "unknown"));
        return result;
    }

    @Tool("Get Meraki network status summary")
    public Map<String, Object> getNetworkStatusTool(
            @ToolMemoryId String conversationId,
            @P("credential id") Long credentialId,
            @P("network id") String networkId) {
        List<Map<String, Object>> devices = listDevicesTool(conversationId, credentialId, networkId);
        List<Map<String, Object>> clients = listClientsTool(conversationId, credentialId, networkId);
        Map<String, Object> report = new HashMap<>();
        report.put("total_devices", devices != null ? devices.size() : 0);
        report.put("total_clients", clients != null ? clients.size() : 0);
        return report;
    }

    @Tool("List firewall rules in a Meraki network")
    public Map<String, Object> listFirewallRulesTool(
            @ToolMemoryId String conversationId,
            @P("credential id") Long credentialId,
            @P("network id") String networkId) {
        String user = conversationId.replaceFirst("-conversation$", "");
        String url = BASE_URL + "/networks/" + networkId + "/appliance/firewall/l3FirewallRules";
        return get(url, headers(user, credentialId), Map.class);
    }

    @Tool("List wireless channel utilization in a Meraki network")
    public List<Map<String, Object>> listWirelessChannelsTool(
            @ToolMemoryId String conversationId,
            @P("credential id") Long credentialId,
            @P("network id") String networkId) {
        String user = conversationId.replaceFirst("-conversation$", "");
        String url = BASE_URL + "/networks/" + networkId + "/wireless/channelUtilizationHistory?timespan=86400";
        return get(url, headers(user, credentialId), List.class);
    }

    @Tool("List VLANs in a Meraki network")
    public List<Map<String, Object>> listVlansTool(
            @ToolMemoryId String conversationId,
            @P("credential id") Long credentialId,
            @P("network id") String networkId) {
        String user = conversationId.replaceFirst("-conversation$", "");
        String url = BASE_URL + "/networks/" + networkId + "/appliance/vlans";
        return get(url, headers(user, credentialId), List.class);
    }

    @Tool("List saturated switch ports in a Meraki network")
    public List<Map<String, Object>> listSaturatedPortsTool(
            @ToolMemoryId String conversationId,
            @P("credential id") Long credentialId,
            @P("network id") String networkId) {
        String user = conversationId.replaceFirst("-conversation$", "");
        // Obtener todos los dispositivos y filtrar switches
        List<Map<String, Object>> devices = listDevicesTool(conversationId, credentialId, networkId);
        List<String> switchSerials = devices.stream()
                .filter(d -> d.get("model") != null && d.get("model").toString().startsWith("MS"))
                .map(d -> d.get("serial").toString())
                .collect(Collectors.toList());
        // Reunir puertos saturados
        List<Map<String, Object>> saturated = switchSerials.stream().flatMap(serial -> {
            String url = BASE_URL + "/devices/" + serial + "/switch/ports/statuses";
            List<Map<String, Object>> ports = get(url, headers(user, credentialId), List.class);
            return ports.stream()
                    .filter(p -> p.get("usageInKb") instanceof Map
                            && ((Map<?, ?>) p.get("usageInKb")).get("total") instanceof Number
                            && ((Number) ((Map<?, ?>) p.get("usageInKb")).get("total")).longValue() > 1000000)
                    .map(p -> {
                        Map<String, Object> entry = new HashMap<>();
                        entry.put("switch_serial", serial);
                        entry.put("port", p.get("portId"));
                        entry.put("usage_kb", ((Map<?, ?>) p.get("usageInKb")).get("total"));
                        return entry;
                    });
        }).collect(Collectors.toList());
        return saturated;
    }
}
