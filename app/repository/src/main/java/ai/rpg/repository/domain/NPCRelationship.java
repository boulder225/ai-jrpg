package ai.rpg.repository.domain;

public record NPCRelationship(
    Long playerId,
    Long npcId,
    String relationshipType,
    int relationshipScore
) {} 