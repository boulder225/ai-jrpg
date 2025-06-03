package ai.rpg.repository.ai;

import ai.rpg.repository.config.ClaudeProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ClaudeClientService {
    private final WebClient webClient;
    private final ClaudeProperties properties;

    public ClaudeClientService(WebClient.Builder webClientBuilder, ClaudeProperties properties) {
        this.properties = properties;
        this.webClient = webClientBuilder
            .baseUrl(properties.baseUrl())
            .defaultHeader("x-api-key", properties.apiKey())
            .defaultHeader("anthropic-version", "2023-06-01")
            .build();
    }

    public Mono<String> getChatCompletion(String prompt) {
        return webClient.post()
            .uri("/messages")
            .bodyValue(new ChatRequest(prompt))
            .retrieve()
            .bodyToMono(ChatResponse.class)
            .map(response -> response.content().get(0).text());
    }

    private record ChatRequest(String prompt) {}
    private record ChatResponse(java.util.List<Content> content) {}
    private record Content(String text) {}
} 