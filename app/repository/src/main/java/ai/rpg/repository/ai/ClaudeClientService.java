package ai.rpg.repository.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class ClaudeClientService {
    private final WebClient webClient;

    public ClaudeClientService(@Value("${claude.api.base-url:https://api.anthropic.com/v1}") String baseUrl,
                              @Value("${claude.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .build();
    }

    public Mono<String> chatCompletion(String prompt) {
        return webClient.post()
                .uri("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "model", "claude-3-opus-20240229",
                        "max_tokens", 1024,
                        "messages", new Object[]{
                                Map.of("role", "user", "content", prompt)
                        }
                ))
                .retrieve()
                .bodyToMono(String.class);
    }
} 