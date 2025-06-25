package ai.rpg.repository.repository;

import ai.rpg.persistence.entity.ActionEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ActionEventRepository extends JpaRepository<ActionEventEntity, Long> {
    List<ActionEventEntity> findByPlayerId(String playerId);
    @Query("SELECT a FROM ActionEventEntity a WHERE a.playerId = :playerId AND a.type = :type")
    List<ActionEventEntity> findByPlayerIdAndType(@Param("playerId") String playerId, @Param("type") ai.rpg.core.domain.ActionType type);
    List<ActionEventEntity> findByPlayerIdAndTimestampBetween(String playerId, Instant start, Instant end);
} 