package ai.rpg.core.domain;

import lombok.Data;

@Data
public class PlayerCommand {
    private String sessionId;
    private String command;
    private String playerId;
    private String playerName;
} 