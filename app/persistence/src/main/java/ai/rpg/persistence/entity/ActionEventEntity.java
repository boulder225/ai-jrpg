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
 * FIXED: JPA Entity for ActionEvent - Proper mutable class
 * 
 * CRITICAL CHANGES:
 * - Converted from record to class
 * - Added no-args constructor for JPA
 * - Added proper getters/setters
 * - Made all fields mutable for Hibernate
 */
@Entity
@Table(name = "action_events",
       indexes = {
           @Index(name = "idx_action_timestamp", columnList = "timestamp"),
           @Index(name = "idx_action_type", columnList = "type"),
           @Index(name = "idx_action_location", columnList = "location")
       })
public class ActionEventEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "action_id", nullable = false, unique = true)
    private String actionId;
    
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ActionType type;
    
    @Column(name = "command", nullable = false, length = 500)
    private String command;
    
    @Column(name = "target", length = 255)
    private String target;
    
    @Column(name = "location", nullable = false, length = 255)
    private String location;
    
    @Column(name = "outcome", nullable = false, columnDefinition = "TEXT")
    private String outcome;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "consequences", columnDefinition = "CLOB")
    private List<String> consequences = new ArrayList<>();
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "CLOB")
    private Map<String, Object> metadata = new HashMap<>();
    
    @Column(name = "player_id", nullable = false, length = 255)
    private String playerId;
    
    // Relationship to PlayerContext
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_context_id", nullable = false)
    private PlayerContextEntity playerContext;
    
    // ✅ REQUIRED: No-args constructor for JPA
    public ActionEventEntity() {
        this.consequences = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    // ✅ Business constructor
    public ActionEventEntity(String actionId, ActionType type, String command, 
                           String target, String location, String outcome,
                           List<String> consequences) {
        this();
        this.actionId = actionId;
        this.timestamp = Instant.now();
        this.type = type;
        this.command = command;
        this.target = target;
        this.location = location;
        this.outcome = outcome;
        this.consequences = consequences != null ? new ArrayList<>(consequences) : new ArrayList<>();
    }
    
    // ✅ REQUIRED: All getters and setters for JPA
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getActionId() { return actionId; }
    public void setActionId(String actionId) { this.actionId = actionId; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    public ActionType getType() { return type; }
    public void setType(ActionType type) { this.type = type; }
    
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
    
    public List<String> getConsequences() { return consequences; }
    public void setConsequences(List<String> consequences) { 
        this.consequences = consequences != null ? new ArrayList<>(consequences) : new ArrayList<>();
    }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
    
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    
    public PlayerContextEntity getPlayerContext() { return playerContext; }
    public void setPlayerContext(PlayerContextEntity playerContext) { this.playerContext = playerContext; }
    
    // ✅ Business methods
    public void addConsequence(String consequence) {
        if (this.consequences == null) {
            this.consequences = new ArrayList<>();
        }
        this.consequences.add(consequence);
    }
    
    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }
    
    public boolean isSuccessful() {
        return consequences != null && consequences.stream()
            .anyMatch(c -> c.contains("success") || c.contains("victory"));
    }
    
    public boolean isCombat() {
        return type == ActionType.ATTACK || 
               (consequences != null && consequences.stream().anyMatch(c -> c.contains("combat")));
    }
}
