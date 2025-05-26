package com.example.txdxai.ai.tool;

import org.springframework.stereotype.Component;

@Component
public class WazuhTools {

    /**
     * Llama a la API de Wazuh para buscar alertas críticas.
     */
    public String searchAlerts(String filter) {
        // TODO: implementar llamada real a Wazuh
        return "Alertas simuladas de Wazuh con filtro: " + filter;
    }
}