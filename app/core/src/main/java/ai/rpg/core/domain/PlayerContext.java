package ai.rpg.core.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * PlayerContext represents the complete context state for a player session.
 * 
 * This is a direct port from the Go implementation, using Java 21 records
 * for immutability and clean data representation.
 * 
 * CRITICAL: This should match exactly the Go types.go PlayerContext struct.
 */
public record PlayerContext(
    String playerId,
    String sessionId,
    Instant startTime,
    Instant lastUpdate,
    CharacterState character,
    LocationState location,
    List<ActionEvent> actions,
    Map<String, NPCRelationship> npcStates,
    SessionMetrics sessionStats
) {
    
    /**
     * Validation constructor to ensure data integrity
     */
    public PlayerContext {
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player ID cannot be null or blank");
        }
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Session ID cannot be null or blank");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (lastUpdate == null) {
            throw new IllegalArgumentException("Last update cannot be null");
        }
        if (character == null) {
            throw new IllegalArgumentException("Character state cannot be null");
        }
        if (location == null) {
            throw new IllegalArgumentException("Location state cannot be null");
        }
        // Defensive copies for mutable collections
        actions = actions != null ? List.copyOf(actions) : List.of();
        npcStates = npcStates != null ? Map.copyOf(npcStates) : Map.of();
    }
    
    /**
     * Factory method for creating new player contexts
     */
    public static PlayerContext newPlayer(String playerId, String sessionId, String playerName) {
        var now = Instant.now();
        return new PlayerContext(
            playerId,
            sessionId,
            now,
            now,
            CharacterState.newCharacter(playerName),
            LocationState.startingLocation(),
            List.of(),
            Map.of(),
            SessionMetrics.empty()
        );
    }
    
    /**
     * Create updated context with new last update time
     */
    public PlayerContext withUpdatedTime() {
        return new PlayerContext(
            playerId,
            sessionId,
            startTime,
            Instant.now(), // Update last update time
            character,
            location,
            actions,
            npcStates,
            sessionStats
        );
    }
    
    /**
     * Add new action to context
     */
    public PlayerContext withNewAction(ActionEvent action) {
        var updatedActions = new java.util.ArrayList<>(actions);
        updatedActions.add(action);
        
        // Keep only last N actions (matching Go implementation)
        final int MAX_ACTIONS = 50;
        if (updatedActions.size() > MAX_ACTIONS) {
            updatedActions = new java.util.ArrayList<>(
                updatedActions.subList(updatedActions.size() - MAX_ACTIONS, updatedActions.size())
            );
        }
        
        return new PlayerContext(
            playerId,
            sessionId,
            startTime,
            Instant.now(),
            character,
            location,
            List.copyOf(updatedActions),
            npcStates,
            sessionStats.incrementAction(action.type())
        );
    }
}
