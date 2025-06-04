package ai.rpg.ai.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * Claude AI Provider implementation using Anthropic's API
 * 
 * CRITICAL: This replicates the Go claude.go implementation with:
 * - Reactive HTTP client for non-blocking calls
 * - Circuit breaker for fault tolerance
 * - Rate limiting to prevent API abuse
 * - Proper error handling and retries
 */
@Component
public class ClaudeAIProvider implements AIProvider {
    
    private static final Logger log = LoggerFactory.getLogger(ClaudeAIProvider.class);
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final int maxTokens;
    private final double temperature;
    
    public ClaudeAIProvider(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${ai.claude.api-key}") String apiKey,
            @Value("${ai.claude.model:claude-3-sonnet-20240229}") String model,
            @Value("${ai.claude.max-tokens:1000}") int maxTokens,
            @Value("${ai.claude.temperature:0.7}") double temperature) {
        
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        
        this.webClient = webClientBuilder
            .baseUrl("https://api.anthropic.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("x-api-key", apiKey)
            .defaultHeader("anthropic-version", "2023-06-01")
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();
    }
    
    @Override
    @CircuitBreaker(name = "claude", fallbackMethod = "generateGMResponseFallback")
    @RateLimiter(name = "claude")
    @Retry(name = "claude")
    public Mono<String> generateGMResponse(String prompt) {
        log.debug("Generating GM response with Claude for prompt length: {}", prompt.length());
        
        var requestBody = createRequestBody(prompt, "You are an expert Game Master for a fantasy RPG. " +
            "Provide immersive, engaging responses that move the story forward. " +
            "Be descriptive but concise. Respond in character as the omniscient narrator.");
        
        return sendRequest(requestBody)
            .doOnSuccess(response -> log.debug("Successfully generated GM response"))
            .doOnError(error -> log.error("Failed to generate GM response", error));
    }
    
    @Override
    @CircuitBreaker(name = "claude", fallbackMethod = "generateNPCDialogueFallback")
    @RateLimiter(name = "claude")
    @Retry(name = "claude")
    public Mono<String> generateNPCDialogue(String npcName, String personality, String prompt) {
        log.debug("Generating NPC dialogue for {} with personality: {}", npcName, personality);
        
        var systemPrompt = String.format(
            "You are %s, an NPC in a fantasy RPG. Your personality: %s. " +
            "Respond only with dialogue that this character would say. " +
            "Stay in character and be consistent with the personality described.",
            npcName, personality
        );
        
        var requestBody = createRequestBody(prompt, systemPrompt);
        
        return sendRequest(requestBody)
            .doOnSuccess(response -> log.debug("Successfully generated dialogue for {}", npcName))
            .doOnError(error -> log.error("Failed to generate dialogue for {}", npcName, error));
    }
    
    @Override
    @CircuitBreaker(name = "claude", fallbackMethod = "generateSceneDescriptionFallback")
    @RateLimiter(name = "claude")
    @Retry(name = "claude")
    public Mono<String> generateSceneDescription(String location, String context, String mood) {
        log.debug("Generating scene description for location: {} with mood: {}", location, mood);
        
        var systemPrompt = String.format(
            "You are a master storyteller describing a scene in a fantasy RPG. " +
            "Create a vivid, atmospheric description of %s. " +
            "The mood should be %s. Context: %s. " +
            "Focus on sensory details that immerse the player in the world.",
            location, mood, context
        );
        
        var requestBody = createRequestBody("Describe this scene in detail.", systemPrompt);
        
        return sendRequest(requestBody)
            .doOnSuccess(response -> log.debug("Successfully generated scene description for {}", location))
            .doOnError(error -> log.error("Failed to generate scene description for {}", location, error));
    }
    
    @Override
    public String getProviderName() {
        return "claude";
    }
    
    @Override
    public Mono<Boolean> isHealthy() {
        var healthCheckBody = createRequestBody("Test", "Respond with 'OK'");
        
        return sendRequest(healthCheckBody)
            .map(response -> response.contains("OK"))
            .onErrorReturn(false)
            .timeout(Duration.ofSeconds(5));
    }
    
    // Private helper methods
    
    private Map<String, Object> createRequestBody(String userMessage, String systemPrompt) {
        return Map.of(
            "model", model,
            "max_tokens", maxTokens,
            "temperature", temperature,
            "system", systemPrompt,
            "messages", java.util.List.of(
                Map.of(
                    "role", "user",
                    "content", userMessage
                )
            )
        );
    }
    
    private Mono<String> sendRequest(Map<String, Object> requestBody) {
        return webClient
            .post()
            .uri("/v1/messages")
            .bodyValue(requestBody)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError(),
                response -> response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new AIProviderException("Claude API client error: " + body)))
            )
            .onStatus(
                status -> status.is5xxServerError(),
                response -> Mono.error(new AIProviderException("Claude API server error"))
            )
            .bodyToMono(String.class)
            .flatMap(this::extractContentFromResponse)
            .timeout(Duration.ofSeconds(30));
    }
    
    private Mono<String> extractContentFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("content");
            
            if (content.isArray() && content.size() > 0) {
                JsonNode firstContent = content.get(0);
                String text = firstContent.path("text").asText();
                
                if (text.isEmpty()) {
                    return Mono.error(new AIProviderException("Empty response from Claude API"));
                }
                
                return Mono.just(text);
            } else {
                return Mono.error(new AIProviderException("Invalid response format from Claude API"));
            }
        } catch (Exception e) {
            return Mono.error(new AIProviderException("Failed to parse Claude API response", e));
        }
    }
    
    // Fallback methods for circuit breaker
    
    public Mono<String> generateGMResponseFallback(String prompt, Exception ex) {
        log.warn("Using fallback for GM response generation due to: {}", ex.getMessage());
        return Mono.just("The world responds to your action, though the details are unclear at this moment. " +
                        "The Game Master's connection seems to be experiencing difficulties.");
    }
    
    public Mono<String> generateNPCDialogueFallback(String npcName, String personality, String prompt, Exception ex) {
        log.warn("Using fallback for NPC dialogue generation for {} due to: {}", npcName, ex.getMessage());
        return Mono.just(String.format("%s seems distracted and doesn't respond clearly.", npcName));
    }
    
    public Mono<String> generateSceneDescriptionFallback(String location, String context, String mood, Exception ex) {
        log.warn("Using fallback for scene description for {} due to: {}", location, ex.getMessage());
        return Mono.just(String.format("You find yourself in %s. The area feels %s, though the details are hazy.", 
                                      location, mood));
    }
}

/**
 * Custom exception for AI provider errors
 */
class AIProviderException extends RuntimeException {
    public AIProviderException(String message) {
        super(message);
    }
    
    public AIProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
