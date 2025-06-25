package ai.rpg.core.domain;

/**
 * HealthStatus tracks character health
 */
public record HealthStatus(int current, int max) {
    
    public HealthStatus {
        if (max <= 0) {
            throw new IllegalArgumentException("Max health must be positive");
        }
        if (current < 0) {
            throw new IllegalArgumentException("Current health cannot be negative");
        }
        if (current > max) {
            throw new IllegalArgumentException("Current health cannot exceed max health");
        }
    }
    
    /**
     * Apply health change with bounds checking
     */
    public HealthStatus withChange(int change) {
        int newCurrent = Math.max(0, Math.min(max, current + change));
        return new HealthStatus(newCurrent, max);
    }
    
    /**
     * Check if character is alive
     */
    public boolean isAlive() {
        return current > 0;
    }
    
    /**
     * Get health percentage
     */
    public double healthPercentage() {
        return (double) current / max;
    }
} 