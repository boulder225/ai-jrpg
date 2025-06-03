package ai.rpg.repository.repository;

import ai.rpg.repository.domain.NPCRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NPCRelationshipRepository extends JpaRepository<NPCRelationship, Long> {
    List<NPCRelationship> findByPlayerId(Long playerId);
    List<NPCRelationship> findByNpcId(Long npcId);
    Optional<NPCRelationship> findByPlayerIdAndNpcId(Long playerId, Long npcId);
    List<NPCRelationship> findByPlayerIdAndRelationshipType(Long playerId, String relationshipType);
} 