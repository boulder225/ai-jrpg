package ai.rpg.core.domain;

import java.util.Map;

/**
 * Equipment and inventory item records for character state
 */
public record EquipmentItem(
    String id,
    String name,
    String type, // weapon, armor, accessory
    String slot, // mainhand, offhand, chest, etc.
    Map<String, Integer> stats,
    Map<String, Object> metadata
) {
    public EquipmentItem {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Equipment ID cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Equipment name cannot be null or blank");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Equipment type cannot be null or blank");
        }
        
        stats = stats != null ? Map.copyOf(stats) : Map.of();
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }
}

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
