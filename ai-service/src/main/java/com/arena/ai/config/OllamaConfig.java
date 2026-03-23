package com.arena.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaConfig {

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("""
                You are a helpful coding assistant for a competitive programming platform.
                Your role is to help users improve their coding skills by:
                1. Providing hints without giving away complete solutions
                2. Analyzing code quality and suggesting improvements
                3. Explaining concepts and algorithms
                
                Always be encouraging and educational in your responses.
                Keep responses concise and focused.
                """)
                .build();
    }
}