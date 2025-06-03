package ai.rpg.repository.domain;

import java.time.Instant;

public record ActionEvent(
    Long eventId,
    Long playerId,
    String eventType,
    String details,
    Instant timestamp
) {} 