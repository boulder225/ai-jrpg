package ai.rpg.core.domain;

import java.util.List;
import java.util.Map;

public record NPCRelationshipData(
    String npcId,
    String npcName,
    String mood,
    int affinity,
    List<String> conversationHistory,
    Map<String, String> knowledge,
    Map<String, Object> metadata
) {} 