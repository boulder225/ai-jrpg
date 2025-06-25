package ai.rpg.persistence.entity;

import ai.rpg.core.domain.ActionType;
import ai.rpg.core.domain.NPCRelationshipData;
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

    @Column(name = "is_active")
    private Boolean isActive = true; 
    
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
    @Column(name = "npc_states", columnDefinition = "CLOB")
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
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
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
