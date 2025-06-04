package ai.rpg.persistence.repository;

import ai.rpg.persistence.entity.PlayerContextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerContextRepository extends JpaRepository<PlayerContextEntity, Long> {
    // Basic CRUD operations are inherited from JpaRepository
    // Additional custom queries can be added here if needed
} 