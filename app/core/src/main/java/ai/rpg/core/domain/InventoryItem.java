package ai.rpg.core.domain;

import java.util.Map;

/**
 * Inventory item record for character state
 */
public record InventoryItem(
    String id,
    String name,
    String type,
    int quantity,
    int value,
    Map<String, Object> metadata
) {
    public InventoryItem {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Inventory item ID cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Inventory item name cannot be null or blank");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
        
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }
} 