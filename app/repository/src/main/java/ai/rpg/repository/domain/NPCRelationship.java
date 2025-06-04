package ai.rpg.repository.domain;

import jakarta.persistence.*;

/**
 * JPA entity representing the relationship score between a player and an NPC.
 */
@Entity
@Table(name = "npc_relationships")
public record NPCRelationship(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id,

    Long playerId,

    Long npcId,

    String relationshipType,

    int relationshipScore
) {}
