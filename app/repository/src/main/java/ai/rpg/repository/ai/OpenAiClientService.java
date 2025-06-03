package ai.rpg.repository.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class OpenAiClientService {
    private final WebClient webClient;

    public OpenAiClientService(@Value("${openai.api.base-url:https://api.openai.com/v1}") String baseUrl,
                                @Value("${openai.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public Mono<String> chatCompletion(String prompt) {
        return webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", new Object[]{Map.of("role", "user", "content", prompt)}
                ))
                .retrieve()
                .bodyToMono(String.class);
    }
} 