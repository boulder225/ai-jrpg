package ai.rpg.persistence.entity;

import ai.rpg.core.domain.ActionType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FIXED: JPA Entity for PlayerContext - Proper mutable class
 * 
 * CRITICAL CHANGES:
 * - Converted from record to class
 * - Added no-args constructor for JPA
 * - Added proper getters/setters
 * - Made all fields mutable for Hibernate
 */
@Entity
@Table(name = "player_contexts", 
       indexes = {
           @Index(name = "idx_player_id", columnList = "playerId"),
           @Index(name = "idx_session_id", columnList = "sessionId"),
           @Index(name = "idx_last_update", columnList = "lastUpdate")
       })
public class PlayerContextEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "player_id", nullable = false, length = 255)
    private String playerId;
    
    @Column(name = "session_id", nullable = false, unique = true, length = 255)
    private String sessionId;
    
    @Column(name = "start_time", nullable = false)
    private Instant startTime;
    
    @Column(name = "last_update", nullable = false)
    private Instant lastUpdate;
    
    // Character state embedded
    @Embedded
    private CharacterStateEmbeddable character;
    
    // Location state embedded  
    @Embedded
    private LocationStateEmbeddable location;
    
    // Actions as separate entity with proper relationship
    @OneToMany(mappedBy = "playerContext", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("timestamp DESC")
    private List<ActionEventEntity> actions = new ArrayList<>();
    
    // NPC relationships as JSON (for complex Map structure)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "npc_states", columnDefinition = "jsonb")
    private Map<String, NPCRelationshipData> npcStates = new HashMap<>();
    
    // Session metrics embedded
    @Embedded
    private SessionMetricsEmbeddable sessionStats;
    
    // ✅ REQUIRED: No-args constructor for JPA
    public PlayerContextEntity() {
        this.character = new CharacterStateEmbeddable();
        this.location = new LocationStateEmbeddable();
        this.sessionStats = new SessionMetricsEmbeddable();
        this.actions = new ArrayList<>();
        this.npcStates = new HashMap<>();
    }
    
    // ✅ Business constructor
    public PlayerContextEntity(String playerId, String sessionId) {
        this(); // Call no-args constructor first
        this.playerId = playerId;
        this.sessionId = sessionId;
        this.startTime = Instant.now();
        this.lastUpdate = Instant.now();
    }
    
    // ✅ REQUIRED: All getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    
    public Instant getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(Instant lastUpdate) { this.lastUpdate = lastUpdate; }
    
    public CharacterStateEmbeddable getCharacter() { return character; }
    public void setCharacter(CharacterStateEmbeddable character) { this.character = character; }
    
    public LocationStateEmbeddable getLocation() { return location; }
    public void setLocation(LocationStateEmbeddable location) { this.location = location; }
    
    public List<ActionEventEntity> getActions() { return actions; }
    public void setActions(List<ActionEventEntity> actions) { 
        this.actions = actions != null ? actions : new ArrayList<>();
    }
    
    public Map<String, NPCRelationshipData> getNpcStates() { return npcStates; }
    public void setNpcStates(Map<String, NPCRelationshipData> npcStates) { 
        this.npcStates = npcStates != null ? npcStates : new HashMap<>();
    }
    
    public SessionMetricsEmbeddable getSessionStats() { return sessionStats; }
    public void setSessionStats(SessionMetricsEmbeddable sessionStats) { this.sessionStats = sessionStats; }
    
    // ✅ Business methods
    public void addAction(ActionEventEntity action) {
        if (this.actions == null) {
            this.actions = new ArrayList<>();
        }
        this.actions.add(action);
        action.setPlayerContext(this);
        this.lastUpdate = Instant.now();
    }
    
    public void updateLastUpdate() {
        this.lastUpdate = Instant.now();
    }
    
    // Limit actions to prevent memory issues (matching Go implementation)
    public void trimActions(int maxActions) {
        if (this.actions != null && this.actions.size() > maxActions) {
            // Keep only the most recent actions
            this.actions = new ArrayList<>(
                this.actions.subList(this.actions.size() - maxActions, this.actions.size())
            );
        }
    }
}

/**
 * FIXED: Embeddable for character state - Proper mutable class
 */
@Embeddable
class CharacterStateEmbeddable {
    
    @Column(name = "character_name", nullable = false)
    private String name;
    
    @Column(name = "health_current")
    private int healthCurrent = 20;
    
    @Column(name = "health_max")
    private int healthMax = 20;
    
    @Column(name = "reputation")
    private int reputation = 0;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private Map<String, Integer> attributes = new HashMap<>();
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "equipment", columnDefinition = "jsonb")
    private List<EquipmentItemData> equipment = new ArrayList<>();
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "inventory", columnDefinition = "jsonb")
    private List<InventoryItemData> inventory = new ArrayList<>();
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "character_metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata = new HashMap<>();
    
    // ✅ REQUIRED: No-args constructor
    public CharacterStateEmbeddable() {
        this.attributes = new HashMap<>();
        this.equipment = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    // ✅ Business constructor
    public CharacterStateEmbeddable(String name) {
        this();
        this.name = name;
        // Set default attributes
        this.attributes.put("strength", 10);
        this.attributes.put("dexterity", 10);
        this.attributes.put("intelligence", 10);
        this.attributes.put("charisma", 10);
    }
    
    // ✅ All getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getHealthCurrent() { return healthCurrent; }
    public void setHealthCurrent(int healthCurrent) { this.healthCurrent = healthCurrent; }
    
    public int getHealthMax() { return healthMax; }
    public void setHealthMax(int healthMax) { this.healthMax = healthMax; }
    
    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }
    
    public Map<String, Integer> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Integer> attributes) { 
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }
    
    public List<EquipmentItemData> getEquipment() { return equipment; }
    public void setEquipment(List<EquipmentItemData> equipment) { 
        this.equipment = equipment != null ? equipment : new ArrayList<>();
    }
    
    public List<InventoryItemData> getInventory() { return inventory; }
    public void setInventory(List<InventoryItemData> inventory) { 
        this.inventory = inventory != null ? inventory : new ArrayList<>();
    }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
}

/**
 * FIXED: Embeddable for location state - Proper mutable class
 */
@Embeddable
class LocationStateEmbeddable {
    
    @Column(name = "current_location", nullable = false)
    private String current = "starting_village";
    
    @Column(name = "previous_location")
    private String previous;
    
    @Column(name = "visit_count")
    private int visitCount = 1;
    
    @Column(name = "first_visit")
    private Instant firstVisit = Instant.now();
    
    @Column(name = "time_in_location_minutes")
    private int timeInLocationMinutes = 0;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "location_history", columnDefinition = "jsonb")
    private List<LocationVisitData> locationHistory = new ArrayList<>();
    
    // ✅ REQUIRED: No-args constructor
    public LocationStateEmbeddable() {
        this.locationHistory = new ArrayList<>();
    }
    
    // ✅ All getters and setters
    public String getCurrent() { return current; }
    public void setCurrent(String current) { this.current = current; }
    
    public String getPrevious() { return previous; }
    public void setPrevious(String previous) { this.previous = previous; }
    
    public int getVisitCount() { return visitCount; }
    public void setVisitCount(int visitCount) { this.visitCount = visitCount; }
    
    public Instant getFirstVisit() { return firstVisit; }
    public void setFirstVisit(Instant firstVisit) { this.firstVisit = firstVisit; }
    
    public int getTimeInLocationMinutes() { return timeInLocationMinutes; }
    public void setTimeInLocationMinutes(int timeInLocationMinutes) { this.timeInLocationMinutes = timeInLocationMinutes; }
    
    public List<LocationVisitData> getLocationHistory() { return locationHistory; }
    public void setLocationHistory(List<LocationVisitData> locationHistory) { 
        this.locationHistory = locationHistory != null ? locationHistory : new ArrayList<>();
    }
}

/**
 * FIXED: Embeddable for session metrics - Proper mutable class
 */
@Embeddable
class SessionMetricsEmbeddable {
    
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
    
    // ✅ REQUIRED: No-args constructor
    public SessionMetricsEmbeddable() {}
    
    // ✅ All getters and setters
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
    
    // ✅ Business methods
    public void incrementAction(ActionType actionType) {
        totalActions++;
        switch (actionType) {
            case ATTACK -> combatActions++;
            case TALK -> socialActions++;
            case EXAMINE, MOVE -> exploreActions++;
        }
    }
}
