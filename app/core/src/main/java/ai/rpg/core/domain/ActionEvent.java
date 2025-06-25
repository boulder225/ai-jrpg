package ai.rpg.core.domain;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * ActionEvent represents a player action with context and consequences.
 * 
 * Using Java 21 records with pattern matching for clean event processing.
 */
public record ActionEvent(
    String id,
    Instant timestamp,
    ActionType type,
    String command,
    String target,
    String location,
    String outcome,
    java.util.List<String> consequences,
    Map<String, Object> metadata
) {
    public ActionEvent {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Action ID cannot be null or blank");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Action type cannot be null");
        }
        if (command == null || command.isBlank()) {
            throw new IllegalArgumentException("Command cannot be null or blank");
        }
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location cannot be null or blank");
        }
        if (outcome == null || outcome.isBlank()) {
            throw new IllegalArgumentException("Outcome cannot be null or blank");
        }
        
        consequences = consequences != null ? java.util.List.copyOf(consequences) : java.util.List.of();
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }
    
    /**
     * Factory method for creating new actions
     */
    public static ActionEvent create(
            ActionType type, 
            String command, 
            String target, 
            String location, 
            String outcome, 
            java.util.List<String> consequences) {
        return new ActionEvent(
            UUID.randomUUID().toString(),
            Instant.now(),
            type,
            command,
            target,
            location,
            outcome,
            consequences,
            Map.of()
        );
    }
    
    /**
     * Check if action was successful based on consequences
     */
    public boolean isSuccessful() {
        return consequences.stream()
            .anyMatch(c -> c.contains("success") || c.contains("victory"));
    }
    
    /**
     * Check if action involved combat
     */
    public boolean isCombat() {
        return type == ActionType.COMBAT || 
               consequences.stream().anyMatch(c -> c.contains("combat"));
    }
}
