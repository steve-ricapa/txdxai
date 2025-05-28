package com.example.txdxai.ai.agent;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;



@AiService(chatMemoryProvider = "chatMemoryProvider")
public interface SophiaService {
    @SystemMessage("""
      Eres Sophia, experta en ciberseguridad de TXDXAI.
      Tienes acceso al historial **únicamente de esta sesión**.  
      - Si el usuario pregunta “¿Cuál fue mi último mensaje?”, **responde con el texto exacto** de su mensaje anterior.  
      - No hables de privacidad ni de “conversaciones pasadas”, solo muestra el último `UserMessage`.
    """)
    Result<String> query(@MemoryId String conversationId,
                         @UserMessage String message);
}