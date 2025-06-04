package ai.rpg.persistence.repository;

import ai.rpg.core.domain.ActionType;
import ai.rpg.persistence.entity.ActionEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for ActionEvent entities with analytics-focused queries
 */
@Repository
public interface ActionEventRepository extends JpaRepository<ActionEventEntity, Long> {
    
    /**
     * Find recent actions for a session (for AI context)
     */
    @Query("SELECT ae FROM ActionEventEntity ae WHERE ae.playerContext.sessionId = :sessionId " +
           "ORDER BY ae.timestamp DESC")
    Page<ActionEventEntity> findRecentActionsBySessionId(@Param("sessionId") String sessionId, Pageable pageable);
    
    /**
     * Find actions by type for analytics
     */
    List<ActionEventEntity> findByTypeOrderByTimestampDesc(ActionType type);
    
    /**
     * Find actions in location (for area analysis)
     */
    List<ActionEventEntity> findByLocationOrderByTimestampDesc(String location);
    
    /**
     * Find successful actions (for AI training data)
     */
    @Query("SELECT ae FROM ActionEventEntity ae WHERE " +
           "EXISTS (SELECT 1 FROM ae.consequences c WHERE c LIKE '%success%' OR c LIKE '%victory%')")
    List<ActionEventEntity> findSuccessfulActions();
    
    /**
     * Count actions by type in time period (analytics)
     */
    @Query("SELECT ae.type, COUNT(ae) FROM ActionEventEntity ae " +
           "WHERE ae.timestamp BETWEEN :start AND :end " +
           "GROUP BY ae.type")
    List<Object[]> countActionsByTypeInPeriod(@Param("start") Instant start, @Param("end") Instant end);
    
    /**
     * Find most common locations (for world design insights)
     */
    @Query("SELECT ae.location, COUNT(ae) as actionCount FROM ActionEventEntity ae " +
           "GROUP BY ae.location ORDER BY actionCount DESC")
    List<Object[]> findMostActiveLocations();
    
    /**
     * Find player behavior patterns
     */
    @Query("SELECT pc.playerId, ae.type, COUNT(ae) FROM ActionEventEntity ae " +
           "JOIN ae.playerContext pc " +
           "WHERE ae.timestamp > :since " +
           "GROUP BY pc.playerId, ae.type " +
           "ORDER BY pc.playerId, COUNT(ae) DESC")
    List<Object[]> findPlayerBehaviorPatterns(@Param("since") Instant since);
    
    /**
     * Delete old actions for cleanup
     */
    void deleteByTimestampBefore(Instant before);
    
    /**
     * Find actions with specific consequences (for event correlation)
     */
    @Query("SELECT ae FROM ActionEventEntity ae WHERE " +
           "EXISTS (SELECT 1 FROM ae.consequences c WHERE c = :consequence)")
    List<ActionEventEntity> findByConsequence(@Param("consequence") String consequence);
}
