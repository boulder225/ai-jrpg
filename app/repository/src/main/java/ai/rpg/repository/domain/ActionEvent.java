package ai.rpg.repository.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity representing a recorded player action.
 */
@Entity
@Table(name = "action_events")
public record ActionEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long eventId,

    Long playerId,

    String eventType,

    String details,

    Instant timestamp
) {}
