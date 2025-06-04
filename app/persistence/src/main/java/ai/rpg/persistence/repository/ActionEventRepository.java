package ai.rpg.persistence.repository;

import ai.rpg.persistence.entity.ActionEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ActionEventRepository extends JpaRepository<ActionEventEntity, Long> {
    List<ActionEventEntity> findByPlayerId(Long playerId);
    List<ActionEventEntity> findByPlayerIdAndEventType(Long playerId, String eventType);
    List<ActionEventEntity> findByPlayerIdAndTimestampBetween(Long playerId, Instant startTime, Instant endTime);
} 