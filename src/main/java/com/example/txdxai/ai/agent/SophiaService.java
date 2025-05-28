package com.example.txdxai.ai.agent;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;



@AiService(chatMemoryProvider = "chatMemoryProvider")
public interface SophiaService {
    @SystemMessage("Eres Sophia, experta en ciberseguridad de TXDXAI")
    Result<String> query(@MemoryId String conversationId,
                         @UserMessage String message);
}