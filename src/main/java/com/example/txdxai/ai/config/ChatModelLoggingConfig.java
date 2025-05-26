package com.example.txdxai.ai.config;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
@Configuration
public class ChatModelLoggingConfig {

    @Bean
    public ChatModelListener chatModelListener() {
        return new ChatModelListener() {
            private final Logger log = LoggerFactory.getLogger(ChatModelLoggingConfig.class);

            @Override
            public void onRequest(ChatModelRequestContext ctx) {
                ChatRequest chatRequest = ctx.chatRequest();
                log.info("[LLM Request] model={}, messages={} ",
                        chatRequest.parameters().modelName(),
                        chatRequest.messages());
            }

            @Override
            public void onResponse(ChatModelResponseContext ctx) {
                ChatResponse chatResponse = ctx.chatResponse();
                TokenUsage usage = chatResponse.tokenUsage();
                String content = chatResponse.aiMessage().text();
                log.info("[LLM Response] model={}, tokensUsed={}, response={} ",
                        chatResponse.modelName(),
                        usage.totalTokenCount(),
                        content);
            }

            @Override
            public void onError(ChatModelErrorContext ctx) {
                log.error("[LLM Error] message={} ", ctx.error().getMessage(), ctx.error());
            }
        };
    }
}
