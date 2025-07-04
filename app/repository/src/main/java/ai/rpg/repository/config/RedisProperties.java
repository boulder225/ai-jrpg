package ai.rpg.repository.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.redis")
public record RedisProperties(
    String host,
    int port,
    String password
) {} 