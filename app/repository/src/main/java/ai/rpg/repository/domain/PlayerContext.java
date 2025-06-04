package ai.rpg.repository.domain;

import jakarta.persistence.*;

/**
 * JPA entity representing the aggregate state of a player.
 */
@Entity
@Table(name = "player_contexts")
public record PlayerContext(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long playerId,

    String playerName,

    int level,

    int experience,

    int health,

    int mana
) {}
