package ai.rpg.persistence.mapper;

import java.util.List;
import java.util.Map;

public record ContextSummary(
    String currentLocation,
    String previousLocation,
    String playerHealth,
    int playerReputation,
    List<String> recentActions,
    List<NPCContextInfo> activeNPCs,
    double sessionDuration,
    String playerMood,
    Map<String, Object> worldState
) {} 