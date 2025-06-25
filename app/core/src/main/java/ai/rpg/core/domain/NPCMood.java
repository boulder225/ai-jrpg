package ai.rpg.core.domain;

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
        if (disposition >= 80) return ECSTATIC;
        else if (disposition >= 60) return JOYFUL;
        else if (disposition >= 40) return FRIENDLY;
        else if (disposition >= 20) return HELPFUL;
        else if (disposition >= -20) return NEUTRAL;
        else if (disposition >= -40) return SUSPICIOUS;
        else if (disposition >= -60) return UNFRIENDLY;
        else if (disposition >= -80) return HOSTILE;
        else return ENRAGED;
    }

    public static NPCMood fromString(String text) {
        for (NPCMood b : NPCMood.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return NEUTRAL;
    }
} 