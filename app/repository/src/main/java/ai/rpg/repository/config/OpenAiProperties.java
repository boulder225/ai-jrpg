package ai.rpg.repository.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai")
public record OpenAiProperties(
    String apiKey,
    String baseUrl
) {} 