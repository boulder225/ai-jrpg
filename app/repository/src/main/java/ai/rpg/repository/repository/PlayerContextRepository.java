package ai.rpg.repository.repository;

import ai.rpg.repository.domain.PlayerContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerContextRepository extends JpaRepository<PlayerContext, Long> {
    // Basic CRUD operations are inherited from JpaRepository
    // Additional custom queries can be added here if needed
} 