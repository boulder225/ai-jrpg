package ai.rpg.persistence.entity;

import ai.rpg.core.domain.ActionType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SessionMetricsEmbeddable {
    
    @Column(name = "total_actions")
    private int totalActions = 0;
    
    @Column(name = "combat_actions")
    private int combatActions = 0;
    
    @Column(name = "social_actions")
    private int socialActions = 0;
    
    @Column(name = "explore_actions")
    private int exploreActions = 0;
    
    @Column(name = "session_time_minutes")
    private double sessionTimeMinutes = 0.0;
    
    @Column(name = "locations_visited")
    private int locationsVisited = 0;
    
    @Column(name = "npcs_interacted")
    private int npcsInteracted = 0;
    
    public SessionMetricsEmbeddable() {}
    
    public int getTotalActions() { return totalActions; }
    public void setTotalActions(int totalActions) { this.totalActions = totalActions; }
    
    public int getCombatActions() { return combatActions; }
    public void setCombatActions(int combatActions) { this.combatActions = combatActions; }
    
    public int getSocialActions() { return socialActions; }
    public void setSocialActions(int socialActions) { this.socialActions = socialActions; }
    
    public int getExploreActions() { return exploreActions; }
    public void setExploreActions(int exploreActions) { this.exploreActions = exploreActions; }
    
    public double getSessionTimeMinutes() { return sessionTimeMinutes; }
    public void setSessionTimeMinutes(double sessionTimeMinutes) { this.sessionTimeMinutes = sessionTimeMinutes; }
    
    public int getLocationsVisited() { return locationsVisited; }
    public void setLocationsVisited(int locationsVisited) { this.locationsVisited = locationsVisited; }
    
    public int getNpcsInteracted() { return npcsInteracted; }
    public void setNpcsInteracted(int npcsInteracted) { this.npcsInteracted = npcsInteracted; }
    
    public void incrementAction(ActionType actionType) {
        this.totalActions++;
        switch (actionType) {
            case ATTACK:
                this.combatActions++;
                break;
            case TALK:
                this.socialActions++;
                break;
            case EXAMINE:
            case MOVE:
                this.exploreActions++;
                break;
            default:
                // No specific counter for other actions
                break;
        }
    }
} 