package ai.rpg.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "action_events")
public class ActionEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;
    
    @Column(name = "player_id", nullable = false)
    private Long playerId;
    
    @Column(name = "event_type", nullable = false)
    private String eventType;
    
    @Column(name = "details", nullable = false)
    private String details;
    
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    // No-args constructor required by JPA
    public ActionEventEntity() {
    }

    // Constructor with all fields
    public ActionEventEntity(Long eventId, Long playerId, String eventType, String details, Instant timestamp) {
        this.eventId = eventId;
        this.playerId = playerId;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
} 