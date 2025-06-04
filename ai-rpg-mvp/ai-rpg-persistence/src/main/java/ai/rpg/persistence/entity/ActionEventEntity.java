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
    @Column(name = "consequences", columnDefinition = "jsonb")
    private List<String> consequences = new ArrayList<>();
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata = new HashMap<>();
    
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

/**
 * FIXED: Data classes for JSON storage - Proper mutable classes
 */

/**
 * FIXED: JSON-serializable representation of NPCRelationship
 */
class NPCRelationshipData {
    private String npcId;
    private String name;
    private int disposition;
    private Instant firstMet;
    private Instant lastInteraction;
    private int interactionCount;
    private List<String> knownFacts;
    private String mood;
    private String location;
    private List<String> notes;
    
    // ✅ REQUIRED: No-args constructor
    public NPCRelationshipData() {
        this.knownFacts = new ArrayList<>();
        this.notes = new ArrayList<>();
    }
    
    // ✅ Business constructor
    public NPCRelationshipData(String npcId, String name, int disposition, 
                              Instant firstMet, Instant lastInteraction,
                              int interactionCount, List<String> knownFacts,
                              String mood, String location, List<String> notes) {
        this();
        this.npcId = npcId;
        this.name = name;
        this.disposition = disposition;
        this.firstMet = firstMet;
        this.lastInteraction = lastInteraction;
        this.interactionCount = interactionCount;
        this.knownFacts = knownFacts != null ? new ArrayList<>(knownFacts) : new ArrayList<>();
        this.mood = mood;
        this.location = location;
        this.notes = notes != null ? new ArrayList<>(notes) : new ArrayList<>();
    }
    
    // ✅ All getters and setters
    public String getNpcId() { return npcId; }
    public void setNpcId(String npcId) { this.npcId = npcId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getDisposition() { return disposition; }
    public void setDisposition(int disposition) { this.disposition = disposition; }
    
    public Instant getFirstMet() { return firstMet; }
    public void setFirstMet(Instant firstMet) { this.firstMet = firstMet; }
    
    public Instant getLastInteraction() { return lastInteraction; }
    public void setLastInteraction(Instant lastInteraction) { this.lastInteraction = lastInteraction; }
    
    public int getInteractionCount() { return interactionCount; }
    public void setInteractionCount(int interactionCount) { this.interactionCount = interactionCount; }
    
    public List<String> getKnownFacts() { return knownFacts; }
    public void setKnownFacts(List<String> knownFacts) { 
        this.knownFacts = knownFacts != null ? knownFacts : new ArrayList<>();
    }
    
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public List<String> getNotes() { return notes; }
    public void setNotes(List<String> notes) { 
        this.notes = notes != null ? notes : new ArrayList<>();
    }
}

/**
 * FIXED: JSON-serializable representation of EquipmentItem
 */
class EquipmentItemData {
    private String id;
    private String name;
    private String type;
    private String slot;
    private Map<String, Integer> stats;
    private Map<String, Object> metadata;
    
    // ✅ REQUIRED: No-args constructor
    public EquipmentItemData() {
        this.stats = new HashMap<>();
        this.metadata = new HashMap<>();
    }
    
    // ✅ Business constructor
    public EquipmentItemData(String id, String name, String type, String slot,
                           Map<String, Integer> stats, Map<String, Object> metadata) {
        this();
        this.id = id;
        this.name = name;
        this.type = type;
        this.slot = slot;
        this.stats = stats != null ? new HashMap<>(stats) : new HashMap<>();
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
    
    // ✅ All getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
    
    public Map<String, Integer> getStats() { return stats; }
    public void setStats(Map<String, Integer> stats) { 
        this.stats = stats != null ? stats : new HashMap<>();
    }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
}

/**
 * FIXED: JSON-serializable representation of InventoryItem
 */
class InventoryItemData {
    private String id;
    private String name;
    private String type;
    private int quantity;
    private int value;
    private Map<String, Object> metadata;
    
    // ✅ REQUIRED: No-args constructor
    public InventoryItemData() {
        this.metadata = new HashMap<>();
    }
    
    // ✅ Business constructor
    public InventoryItemData(String id, String name, String type, int quantity,
                           int value, Map<String, Object> metadata) {
        this();
        this.id = id;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.value = value;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
    
    // ✅ All getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
}

/**
 * FIXED: JSON-serializable representation of LocationVisit
 */
class LocationVisitData {
    private String location;
    private Instant entryTime;
    private Instant exitTime;
    private int durationMinutes;
    
    // ✅ REQUIRED: No-args constructor
    public LocationVisitData() {}
    
    // ✅ Business constructor
    public LocationVisitData(String location, Instant entryTime, Instant exitTime, int durationMinutes) {
        this.location = location;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.durationMinutes = durationMinutes;
    }
    
    // ✅ All getters and setters
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Instant getEntryTime() { return entryTime; }
    public void setEntryTime(Instant entryTime) { this.entryTime = entryTime; }
    
    public Instant getExitTime() { return exitTime; }
    public void setExitTime(Instant exitTime) { this.exitTime = exitTime; }
    
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}
