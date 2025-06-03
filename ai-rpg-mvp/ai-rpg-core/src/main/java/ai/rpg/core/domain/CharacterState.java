package ai.rpg.core.domain;

import java.util.Map;

/**
 * CharacterState represents the player's character information.
 * 
 * Direct port from Go CharacterState struct with Java 21 records.
 */
public record CharacterState(
    String name,
    HealthStatus health,
    java.util.List<EquipmentItem> equipment,
    java.util.List<InventoryItem> inventory,
    int reputation, // -100 to 100
    Map<String, Integer> attributes, // strength, charisma, etc.
    Map<String, Object> metadata
) {
    
    public CharacterState {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Character name cannot be null or blank");
        }
        if (health == null) {
            throw new IllegalArgumentException("Health status cannot be null");
        }
        if (reputation < -100 || reputation > 100) {
            throw new IllegalArgumentException("Reputation must be between -100 and 100");
        }
        
        // Defensive copies
        equipment = equipment != null ? java.util.List.copyOf(equipment) : java.util.List.of();
        inventory = inventory != null ? java.util.List.copyOf(inventory) : java.util.List.of();
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }
    
    /**
     * Factory method for new character creation
     */
    public static CharacterState newCharacter(String name) {
        return new CharacterState(
            name,
            new HealthStatus(20, 20), // Default health
            java.util.List.of(),
            java.util.List.of(),
            0, // Neutral reputation
            Map.of( // Default attributes
                "strength", 10,
                "dexterity", 10,
                "intelligence", 10,
                "charisma", 10
            ),
            Map.of()
        );
    }
    
    /**
     * Update character health
     */
    public CharacterState withHealthChange(int healthChange) {
        var newHealth = health.withChange(healthChange);
        return new CharacterState(name, newHealth, equipment, inventory, reputation, attributes, metadata);
    }
    
    /**
     * Update character reputation
     */
    public CharacterState withReputationChange(int reputationChange) {
        int newReputation = Math.max(-100, Math.min(100, reputation + reputationChange));
        return new CharacterState(name, health, equipment, inventory, newReputation, attributes, metadata);
    }
}

/**
 * HealthStatus tracks character health
 */
public record HealthStatus(int current, int max) {
    
    public HealthStatus {
        if (max <= 0) {
            throw new IllegalArgumentException("Max health must be positive");
        }
        if (current < 0) {
            throw new IllegalArgumentException("Current health cannot be negative");
        }
        if (current > max) {
            throw new IllegalArgumentException("Current health cannot exceed max health");
        }
    }
    
    /**
     * Apply health change with bounds checking
     */
    public HealthStatus withChange(int change) {
        int newCurrent = Math.max(0, Math.min(max, current + change));
        return new HealthStatus(newCurrent, max);
    }
    
    /**
     * Check if character is alive
     */
    public boolean isAlive() {
        return current > 0;
    }
    
    /**
     * Get health percentage
     */
    public double healthPercentage() {
        return (double) current / max;
    }
}
