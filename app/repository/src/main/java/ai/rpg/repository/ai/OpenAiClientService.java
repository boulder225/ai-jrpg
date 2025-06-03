package ai.rpg.repository.ai;

import ai.rpg.repository.config.OpenAiProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenAiClientService {
    private final WebClient webClient;
    private final OpenAiProperties properties;

    public OpenAiClientService(WebClient.Builder webClientBuilder, OpenAiProperties properties) {
        this.properties = properties;
        this.webClient = webClientBuilder
            .baseUrl(properties.baseUrl())
            .defaultHeader("Authorization", "Bearer " + properties.apiKey())
            .build();
    }

    public Mono<String> getChatCompletion(String prompt) {
        return webClient.post()
            .uri("/chat/completions")
            .bodyValue(new ChatRequest(prompt))
            .retrieve()
            .bodyToMono(ChatResponse.class)
            .map(response -> response.choices().get(0).message().content());
    }

    private record ChatRequest(String prompt) {}
    private record ChatResponse(java.util.List<Choice> choices) {}
    private record Choice(Message message) {}
    private record Message(String content) {}
} 