package com.example.txdxai.ai.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AgentConfig {

    // Memoria compartida para todos los usuarios
    @Bean
    public dev.langchain4j.memory.ChatMemory chatMemory() {
        return dev.langchain4j.memory.chat.MessageWindowChatMemory.withMaxMessages(20);
    }

    // O bien, memoria por usuario: cada vez que aparezca un MemoryId nuevo
    @Bean
    public dev.langchain4j.memory.chat.ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> dev.langchain4j.memory.chat.MessageWindowChatMemory.withMaxMessages(20);
    }
}