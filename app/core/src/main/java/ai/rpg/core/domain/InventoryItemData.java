package ai.rpg.core.domain;

import java.util.Map;

public record InventoryItemData(
    String id,
    String name,
    String type,
    int quantity,
    Map<String, Object> metadata
) {} 