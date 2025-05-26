package com.example.txdxai.ai.agent;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;


@AiService
public interface SophiaService {

    @SystemMessage("""
    Eres SOPHIA, un agente conversacional de ciberseguridad para TXDXAI.
    Ayuda al usuario a consultar Splunk, Meraki, Wazuh; generar reportes y crear tickets.

    Reglas:
    1. Identifica el sistema (Splunk, Meraki, Wazuh).
    2. Para consultas, usa las herramientas (@Tool).
    3. Para crear tickets, solicita confirmación antes de invocar la herramienta.
    4. Usuarios y administradores pueden crear tickets.
    5. Mantén el contexto (@MemoryId) y responde de forma clara.

    Hoy es {{current_date}}.
    """)
    Result<String> query(
            @MemoryId String conversationId,
            @UserMessage String userInput
    );
}
