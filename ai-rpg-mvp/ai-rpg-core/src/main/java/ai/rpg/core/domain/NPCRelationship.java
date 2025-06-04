package ai.rpg.core.domain;

import java.time.Instant;
import java.util.List;

/**
 * NPCRelationship tracks relationship with specific NPCs
 */
public record NPCRelationship(
    String npcId,
    String name,
    int disposition, // -100 to 100
    Instant firstMet,
    Instant lastInteraction,
    int interactionCount,
    List<String> knownFacts,
    NPCMood mood,
    String location,
    List<String> notes
) {
    public NPCRelationship {
        if (npcId == null || npcId.isBlank()) {
            throw new IllegalArgumentException("NPC ID cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("NPC name cannot be null or blank");
        }
        if (disposition < -100 || disposition > 100) {
            throw new IllegalArgumentException("Disposition must be between -100 and 100");
        }
        if (firstMet == null) {
            throw new IllegalArgumentException("First met time cannot be null");
        }
        if (lastInteraction == null) {
            throw new IllegalArgumentException("Last interaction time cannot be null");
        }
        if (interactionCount < 0) {
            throw new IllegalArgumentException("Interaction count cannot be negative");
        }
        if (mood == null) {
            throw new IllegalArgumentException("Mood cannot be null");
        }
        
        knownFacts = knownFacts != null ? List.copyOf(knownFacts) : List.of();
        notes = notes != null ? List.copyOf(notes) : List.of();
    }
    
    /**
     * Factory method for first meeting
     */
    public static NPCRelationship firstMeeting(String npcId, String name, String location) {
        var now = Instant.now();
        return new NPCRelationship(
            npcId,
            name,
            0, // Neutral disposition
            now,
            now,
            1,
            List.of(),
            NPCMood.NEUTRAL,
            location,
            List.of()
        );
    }
    
    /**
     * Update relationship after interaction
     */
    public NPCRelationship afterInteraction(int dispositionChange, List<String> newFacts) {
        var newDisposition = Math.max(-100, Math.min(100, disposition + dispositionChange));
        var newMood = NPCMood.fromDisposition(newDisposition);
        
        var updatedFacts = new java.util.ArrayList<>(knownFacts);
        if (newFacts != null) {
            newFacts.stream()
                .filter(fact -> !updatedFacts.contains(fact))
                .forEach(updatedFacts::add);
        }
        
        return new NPCRelationship(
            npcId,
            name,
            newDisposition,
            firstMet,
            Instant.now(),
            interactionCount + 1,
            List.copyOf(updatedFacts),
            newMood,
            location,
            notes
        );
    }
    
    /**
     * Get relationship level as string
     */
    public String getRelationshipLevel() {
        return switch (disposition) {
            case int d when d >= 75 -> "Best Friend";
            case int d when d >= 50 -> "Close Friend";
            case int d when d >= 25 -> "Friend";
            case int d when d >= 10 -> "Ally";
            case int d when d >= -10 -> "Neutral";
            case int d when d >= -25 -> "Unfriendly";
            case int d when d >= -50 -> "Hostile";
            case int d when d >= -75 -> "Enemy";
            default -> "Nemesis";
        };
    }
}

/**
 * NPC mood based on disposition and recent interactions
 */
public enum NPCMood {
    ECSTATIC("ecstatic"),
    JOYFUL("joyful"), 
    FRIENDLY("friendly"),
    HELPFUL("helpful"),
    NEUTRAL("neutral"),
    SUSPICIOUS("suspicious"),
    UNFRIENDLY("unfriendly"),
    HOSTILE("hostile"),
    ENRAGED("enraged");
    
    private final String value;
    
    NPCMood(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Determine mood from disposition
     */
    public static NPCMood fromDisposition(int disposition) {
        return switch (disposition) {
            case int d when d >= 80 -> ECSTATIC;
            case int d when d >= 60 -> JOYFUL;
            case int d when d >= 40 -> FRIENDLY;
            case int d when d >= 20 -> HELPFUL;
            case int d when d >= -20 -> NEUTRAL;
            case int d when d >= -40 -> SUSPICIOUS;
            case int d when d >= -60 -> UNFRIENDLY;
            case int d when d >= -80 -> HOSTILE;
            default -> ENRAGED;
        };
    }
}
