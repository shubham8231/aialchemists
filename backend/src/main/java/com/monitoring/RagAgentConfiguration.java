package com.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RAG Agent beans.
 * Provides ObjectMapper and other necessary beans.
 */
@Configuration
public class RagAgentConfiguration {

    /**
     * Creates and configures an ObjectMapper bean for JSON serialization/deserialization.
     * This is required by OpenAiGenerator for handling JSON operations.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
