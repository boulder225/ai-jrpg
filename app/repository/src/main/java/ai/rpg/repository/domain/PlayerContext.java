package ai.rpg.repository.domain;

public record PlayerContext(
    Long playerId,
    String playerName,
    int level,
    int experience,
    int health,
    int mana
) {} 