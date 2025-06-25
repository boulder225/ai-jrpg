package ai.rpg.core.domain;

import java.time.Instant;

/**
 * LocationVisit represents a visit to a location
 */
public record LocationVisit(
    String location,
    Instant entryTime,
    Instant exitTime,
    int durationMinutes
) {
    public LocationVisit {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location cannot be null or blank");
        }
        if (entryTime == null) {
            throw new IllegalArgumentException("Entry time cannot be null");
        }
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
    }
    
    /**
     * Complete the visit with exit time
     */
    public LocationVisit withExit(Instant exitTime) {
        if (exitTime == null) {
            throw new IllegalArgumentException("Exit time cannot be null");
        }
        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time cannot be before entry time");
        }
        
        var duration = (int) java.time.Duration.between(entryTime, exitTime).toMinutes();
        return new LocationVisit(location, entryTime, exitTime, duration);
    }
} 