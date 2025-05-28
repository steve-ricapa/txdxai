package com.example.txdxai.ai.config;

import com.example.txdxai.core.model.ChatMemoryEntry;
import com.example.txdxai.core.model.Sender;
import com.example.txdxai.core.service.ChatMemoryService;
import com.example.txdxai.core.service.UserService;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.AiMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
public class MemoryConfig {

    @Bean
    public ChatMemoryProvider chatMemoryProvider(
            ChatMemoryService chatMemoryService,
            UserService userService
    ) {
        return conversationIdObj -> {
            // 1) Convierte el ID de memoria a String
            String conversationId = conversationIdObj.toString();

            // 2) Crea la memoria de ventana de hasta 20 mensajes
            MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                    .id(conversationId)
                    .maxMessages(20)
                    .build();

            // 3) Extrae el username (quitando el sufijo "-conversation")
            String username = conversationId.replaceFirst("-conversation$", "");

            // 4) Carga todas las entradas previas de BD y las añade
            userService.findByUsername(username).ifPresent(user -> {
                List<ChatMemoryEntry> entries = chatMemoryService.getRecent20ByUser(user);
                for (ChatMemoryEntry e : entries) {
                    if (e.getSender() == Sender.USER) {
                        memory.add(new UserMessage(e.getMessage()) );
                    } else {
                        memory.add(new AiMessage(e.getMessage()));
                    }
                }
            });

            return memory;
        };
    }
}
