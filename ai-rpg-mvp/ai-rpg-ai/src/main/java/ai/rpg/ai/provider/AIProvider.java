package ai.rpg.ai.provider;

import reactor.core.publisher.Mono;

/**
 * AI Provider interface for different AI services
 * 
 * This interface defines the contract for AI providers (Claude, OpenAI, etc.)
 * using reactive programming with Spring WebFlux for non-blocking HTTP calls.
 */
public interface AIProvider {
    
    /**
     * Generate Game Master response based on context prompt
     */
    Mono<String> generateGMResponse(String prompt);
    
    /**
     * Generate NPC dialogue with personality
     */
    Mono<String> generateNPCDialogue(String npcName, String personality, String prompt);
    
    /**
     * Generate scene description for atmosphere
     */
    Mono<String> generateSceneDescription(String location, String context, String mood);
    
    /**
     * Get provider name for identification
     */
    String getProviderName();
    
    /**
     * Check if provider is available/healthy
     */
    Mono<Boolean> isHealthy();
}
