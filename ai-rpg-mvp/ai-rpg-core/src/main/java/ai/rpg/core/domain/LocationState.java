package ai.rpg.core.domain;

import java.time.Instant;
import java.util.List;

/**
 * LocationState tracks player movement and location history
 */
public record LocationState(
    String current,
    String previous,
    int visitCount,
    Instant firstVisit,
    int timeInLocationMinutes,
    List<LocationVisit> locationHistory
) {
    public LocationState {
        if (current == null || current.isBlank()) {
            throw new IllegalArgumentException("Current location cannot be null or blank");
        }
        if (visitCount < 0) {
            throw new IllegalArgumentException("Visit count cannot be negative");
        }
        if (timeInLocationMinutes < 0) {
            throw new IllegalArgumentException("Time in location cannot be negative");
        }
        
        locationHistory = locationHistory != null ? List.copyOf(locationHistory) : List.of();
    }
    
    /**
     * Factory method for starting location
     */
    public static LocationState startingLocation() {
        return new LocationState(
            "starting_village",
            null,
            1,
            Instant.now(),
            0,
            List.of()
        );
    }
    
    /**
     * Move to new location
     */
    public LocationState moveTo(String newLocation) {
        if (newLocation == null || newLocation.isBlank()) {
            throw new IllegalArgumentException("New location cannot be null or blank");
        }
        
        var visit = new LocationVisit(current, Instant.now(), null, 0);
        var updatedHistory = new java.util.ArrayList<>(locationHistory);
        updatedHistory.add(visit);
        
        return new LocationState(
            newLocation,
            current,
            current.equals(newLocation) ? visitCount + 1 : 1,
            current.equals(newLocation) ? firstVisit : Instant.now(),
            0,
            List.copyOf(updatedHistory)
        );
    }
}

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
