package ai.rpg.repository.repository;

import ai.rpg.repository.domain.ActionEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ActionEventRepository extends JpaRepository<ActionEvent, Long> {
    List<ActionEvent> findByPlayerId(Long playerId);
    List<ActionEvent> findByPlayerIdAndEventType(Long playerId, String eventType);
    List<ActionEvent> findByPlayerIdAndTimestampBetween(Long playerId, Instant startTime, Instant endTime);
} 