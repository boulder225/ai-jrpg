package ai.rpg.core.domain;

import java.util.Map;

public record EquipmentItemData(
    String id,
    String name,
    String type,
    String slot,
    Map<String, Integer> stats,
    Map<String, Object> metadata
) {} 