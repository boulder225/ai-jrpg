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
        var newMood = getMoodFromDisposition(newDisposition);
        
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
        return getRelationshipLabel(disposition);
    }

    private static String getRelationshipLabel(int disposition) {
        if (disposition >= 75) return "Best Friend";
        else if (disposition >= 50) return "Close Friend";
        else if (disposition >= 25) return "Friend";
        else if (disposition >= 10) return "Ally";
        else if (disposition >= -10) return "Neutral";
        else if (disposition >= -25) return "Unfriendly";
        else if (disposition >= -50) return "Hostile";
        else if (disposition >= -75) return "Enemy";
        else return "Nemesis";
    }

    private static NPCMood getMoodFromDisposition(int disposition) {
        if (disposition >= 80) return NPCMood.ECSTATIC;
        else if (disposition >= 60) return NPCMood.JOYFUL;
        else if (disposition >= 40) return NPCMood.FRIENDLY;
        else if (disposition >= 20) return NPCMood.HELPFUL;
        else if (disposition >= -20) return NPCMood.NEUTRAL;
        else if (disposition >= -40) return NPCMood.SUSPICIOUS;
        else if (disposition >= -60) return NPCMood.UNFRIENDLY;
        else if (disposition >= -80) return NPCMood.HOSTILE;
        else return NPCMood.HOSTILE;
    }
}
