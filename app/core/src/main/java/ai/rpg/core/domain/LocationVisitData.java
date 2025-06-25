package ai.rpg.core.domain;

import java.time.Instant;

public record LocationVisitData(
    String locationId,
    Instant timestamp,
    int durationMinutes
) {} 