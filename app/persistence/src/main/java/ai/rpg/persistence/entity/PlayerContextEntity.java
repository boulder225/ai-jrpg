package ai.rpg.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "player_contexts")
public class PlayerContextEntity {
    @Id
    private Long playerId;
    
    @Column(name = "player_name", nullable = false)
    private String playerName;
    
    @Column(name = "level", nullable = false)
    private int level;
    
    @Column(name = "experience", nullable = false)
    private int experience;
    
    @Column(name = "health", nullable = false)
    private int health;
    
    @Column(name = "mana", nullable = false)
    private int mana;

    // No-args constructor required by JPA
    public PlayerContextEntity() {
    }

    // Constructor with all fields
    public PlayerContextEntity(Long playerId, String playerName, int level, int experience, int health, int mana) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.level = level;
        this.experience = experience;
        this.health = health;
        this.mana = mana;
    }

    // Getters and Setters
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }
} 