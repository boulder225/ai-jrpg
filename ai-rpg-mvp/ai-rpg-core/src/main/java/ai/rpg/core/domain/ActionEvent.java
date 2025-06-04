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

/**
 * ActionType enum for type-safe action categorization
 */
public enum ActionType {
    MOVE("move"),
    TALK("talk"), 
    ATTACK("attack"),
    EXAMINE("examine"),
    USE("use"),
    CAST("cast"),
    TRADE("trade"),
    REST("rest"),
    UNKNOWN("unknown");
    
    private final String value;
    
    ActionType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Parse action type from string command
     */
    public static ActionType fromCommand(String command) {
        if (command == null || command.isBlank()) {
            return UNKNOWN;
        }
        
        var lowerCommand = command.toLowerCase().trim();
        
        return switch (lowerCommand) {
            case var cmd when cmd.startsWith("/move") || cmd.startsWith("/go") -> MOVE;
            case var cmd when cmd.startsWith("/talk") || cmd.startsWith("/say") -> TALK;
            case var cmd when cmd.startsWith("/attack") || cmd.startsWith("/fight") -> ATTACK;
            case var cmd when cmd.startsWith("/look") || cmd.startsWith("/examine") -> EXAMINE;
            case var cmd when cmd.startsWith("/use") || cmd.startsWith("/activate") -> USE;
            case var cmd when cmd.startsWith("/cast") || cmd.startsWith("/spell") -> CAST;
            case var cmd when cmd.startsWith("/trade") || cmd.startsWith("/buy") -> TRADE;
            case var cmd when cmd.startsWith("/rest") || cmd.startsWith("/sleep") -> REST;
            default -> UNKNOWN;
        };
    }
}
