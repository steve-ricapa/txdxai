package com.example.txdxai.ai.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
public class MemoryConfig {

    @Bean
    public Map<Object, ChatMemory> chatMemoryStore() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider(Map<Object, ChatMemory> store) {
        return id -> store.computeIfAbsent(
                id,
                key -> MessageWindowChatMemory.builder()
                        .id(key)
                        .maxMessages(20)  // Ventana de 20 mensajes
                        .build()
        );
    }
}
