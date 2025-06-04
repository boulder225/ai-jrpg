package ai.rpg.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player_context")
@Data
@NoArgsConstructor
public class PlayerContextEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long playerId;
    private String playerName;
    private Integer level;
    private Integer experience;
    private Integer health;
    private Integer mana;
    private String location;
    private String currentQuest;
    private String gameState;
} 