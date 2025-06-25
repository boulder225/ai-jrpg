package ai.rpg.repository.repository;

import ai.rpg.persistence.entity.PlayerContextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface PlayerContextRepository extends JpaRepository<PlayerContextEntity, Long> {
    Optional<PlayerContextEntity> findByPlayerId(String playerId);
    Optional<PlayerContextEntity> findBySessionId(String sessionId);
    
    @Query("SELECT p FROM PlayerContextEntity p WHERE p.playerId = ?1 AND p.isActive = true ORDER BY p.lastUpdate DESC")
    List<PlayerContextEntity> findActiveSessionsByPlayerId(String playerId);
    
    @Query("SELECT p FROM PlayerContextEntity p WHERE p.isActive = true ORDER BY p.lastUpdate DESC")
    List<PlayerContextEntity> findAllActiveSessions();
    
    @Query("UPDATE PlayerContextEntity p SET p.isActive = false WHERE p.sessionId = ?1")
    void deactivateSession(String sessionId);
    
    @Modifying
    @Query("UPDATE PlayerContextEntity p SET p.lastUpdate = CURRENT_INSTANT() WHERE p.sessionId = ?1")
    void updateLastAccess(String sessionId);
} 