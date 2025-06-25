package ai.rpg.persistence.repository;

import ai.rpg.persistence.entity.PlayerContextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PlayerContext entities with optimized queries
 * 
 * CRITICAL: This follows Spring Data JPA best practices for performance:
 * - Custom queries for complex operations
 * - Projection interfaces for partial data loading
 * - Proper indexing strategy
 */
@Repository
public interface PlayerContextRepository extends JpaRepository<PlayerContextEntity, Long> {
    
    /**
     * Find context by session ID (most common lookup)
     */
    Optional<PlayerContextEntity> findBySessionId(String sessionId);
    
    /**
     * Find all contexts for a player (for cross-session analysis)
     */
    List<PlayerContextEntity> findByPlayerIdOrderByLastUpdateDesc(String playerId);
    
    /**
     * Find active sessions (updated recently)
     */
    @Query("SELECT pc FROM PlayerContextEntity pc WHERE pc.lastUpdate > :since ORDER BY pc.lastUpdate DESC")
    List<PlayerContextEntity> findActiveSessions(@Param("since") Instant since);
    
    /**
     * Find sessions that need cleanup (old sessions)
     */
    @Query("SELECT pc FROM PlayerContextEntity pc WHERE pc.lastUpdate < :before")
    List<PlayerContextEntity> findSessionsForCleanup(@Param("before") Instant before);
    
    /**
     * Count active sessions
     */
    @Query("SELECT COUNT(pc) FROM PlayerContextEntity pc WHERE pc.lastUpdate > :since")
    long countActiveSessions(@Param("since") Instant since);
    
    /**
     * Find sessions by location (for area-based queries)
     */
    @Query("SELECT pc FROM PlayerContextEntity pc WHERE pc.location.current = :location")
    List<PlayerContextEntity> findByCurrentLocation(@Param("location") String location);
    
    /**
     * Projection interface for lightweight session info
     */
    interface SessionSummary {
        String getSessionId();
        String getPlayerId();
        Instant getLastUpdate();
        String getCurrentLocation();
        int getTotalActions();
    }
    
    /**
     * Get session summaries for dashboard/monitoring
     */
    @Query("SELECT pc.sessionId as sessionId, pc.playerId as playerId, " +
           "pc.lastUpdate as lastUpdate, pc.location.current as currentLocation, " +
           "pc.sessionStats.totalActions as totalActions " +
           "FROM PlayerContextEntity pc WHERE pc.lastUpdate > :since")
    List<SessionSummary> findSessionSummaries(@Param("since") Instant since);
    
    /**
     * Delete old sessions (cleanup operation)
     */
    void deleteByLastUpdateBefore(Instant before);
    
    /**
     * Check if session exists
     */
    boolean existsBySessionId(String sessionId);
    
    /**
     * Find player's most recent session
     */
    @Query("SELECT pc FROM PlayerContextEntity pc WHERE pc.playerId = :playerId " +
           "ORDER BY pc.lastUpdate DESC LIMIT 1")
    Optional<PlayerContextEntity> findMostRecentSessionByPlayerId(@Param("playerId") String playerId);
}
