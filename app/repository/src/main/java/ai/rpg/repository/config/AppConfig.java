package ai.rpg.repository.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    ClaudeProperties.class,
    OpenAiProperties.class,
    DatabaseProperties.class,
    RedisProperties.class
})
public class AppConfig {} 