package com.example.txdxai.ai.tool;

import org.springframework.stereotype.Service;

@Service
public class MerakiService {

    /**
     * Llama a la API de Meraki y devuelve el estado del dispositivo.
     */
    public String getDeviceStatus(String deviceId) {
        // TODO: implementar llamada real a Meraki
        return "Estado simulado de dispositivo Meraki ID=" + deviceId;
    }
}