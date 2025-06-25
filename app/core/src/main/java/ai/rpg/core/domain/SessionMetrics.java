package ai.rpg.core.domain;

/**
 * SessionMetrics tracks session statistics for analytics and AI context
 */
public record SessionMetrics(
    int totalActions,
    int combatActions,
    int socialActions,
    int exploreActions,
    double sessionTimeMinutes,
    int locationsVisited,
    int npcsInteracted
) {
    public SessionMetrics {
        if (totalActions < 0) {
            throw new IllegalArgumentException("Total actions cannot be negative");
        }
        if (combatActions < 0) {
            throw new IllegalArgumentException("Combat actions cannot be negative");
        }
        if (socialActions < 0) {
            throw new IllegalArgumentException("Social actions cannot be negative");
        }
        if (exploreActions < 0) {
            throw new IllegalArgumentException("Explore actions cannot be negative");
        }
        if (sessionTimeMinutes < 0) {
            throw new IllegalArgumentException("Session time cannot be negative");
        }
        if (locationsVisited < 0) {
            throw new IllegalArgumentException("Locations visited cannot be negative");
        }
        if (npcsInteracted < 0) {
            throw new IllegalArgumentException("NPCs interacted cannot be negative");
        }
    }
    
    /**
     * Factory method for empty metrics
     */
    public static SessionMetrics empty() {
        return new SessionMetrics(0, 0, 0, 0, 0.0, 0, 0);
    }
    
    /**
     * Increment action count based on type
     */
    public SessionMetrics incrementAction(ActionType actionType) {
        return switch (actionType) {
            case ATTACK -> new SessionMetrics(
                totalActions + 1, combatActions + 1, socialActions, exploreActions,
                sessionTimeMinutes, locationsVisited, npcsInteracted
            );
            case TALK -> new SessionMetrics(
                totalActions + 1, combatActions, socialActions + 1, exploreActions,
                sessionTimeMinutes, locationsVisited, npcsInteracted
            );
            case EXAMINE, MOVE -> new SessionMetrics(
                totalActions + 1, combatActions, socialActions, exploreActions + 1,
                sessionTimeMinutes, locationsVisited, npcsInteracted
            );
            default -> new SessionMetrics(
                totalActions + 1, combatActions, socialActions, exploreActions,
                sessionTimeMinutes, locationsVisited, npcsInteracted
            );
        };
    }
    
    /**
     * Update session time
     */
    public SessionMetrics withSessionTime(double minutes) {
        return new SessionMetrics(
            totalActions, combatActions, socialActions, exploreActions,
            minutes, locationsVisited, npcsInteracted
        );
    }
    
    /**
     * Increment location visited
     */
    public SessionMetrics incrementLocation() {
        return new SessionMetrics(
            totalActions, combatActions, socialActions, exploreActions,
            sessionTimeMinutes, locationsVisited + 1, npcsInteracted
        );
    }
    
    /**
     * Increment NPC interaction
     */
    public SessionMetrics incrementNPCInteraction() {
        return new SessionMetrics(
            totalActions, combatActions, socialActions, exploreActions,
            sessionTimeMinutes, locationsVisited, npcsInteracted + 1
        );
    }
    
    /**
     * Calculate action distribution percentages
     */
    public record ActionDistribution(
        double combatPercentage,
        double socialPercentage,
        double explorePercentage,
        double otherPercentage
    ) {}
    
    public ActionDistribution getActionDistribution() {
        if (totalActions == 0) {
            return new ActionDistribution(0, 0, 0, 0);
        }
        
        double combat = (double) combatActions / totalActions * 100;
        double social = (double) socialActions / totalActions * 100;
        double explore = (double) exploreActions / totalActions * 100;
        double other = 100 - combat - social - explore;
        
        return new ActionDistribution(combat, social, explore, other);
    }
    
    /**
     * Get player behavior type based on action distribution
     */
    public PlayerBehaviorType getBehaviorType() {
        var distribution = getActionDistribution();
        
        if (distribution.combatPercentage() > 50) {
            return PlayerBehaviorType.WARRIOR;
        } else if (distribution.socialPercentage() > 50) {
            return PlayerBehaviorType.DIPLOMAT;
        } else if (distribution.explorePercentage() > 50) {
            return PlayerBehaviorType.EXPLORER;
        } else {
            return PlayerBehaviorType.BALANCED;
        }
    }
}
