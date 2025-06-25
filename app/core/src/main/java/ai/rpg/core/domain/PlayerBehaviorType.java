package ai.rpg.core.domain;

/**
 * Player behavior classification for AI context
 */
public enum PlayerBehaviorType {
    WARRIOR("warrior"),
    DIPLOMAT("diplomat"),
    EXPLORER("explorer"),
    BALANCED("balanced");
    
    private final String value;
    
    PlayerBehaviorType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
} 