package ai.rpg.repository.service;

import ai.rpg.core.domain.PlayerContext;
import ai.rpg.persistence.entity.PlayerContextEntity;
import ai.rpg.persistence.mapper.PlayerContextMapper;
import ai.rpg.repository.repository.PlayerContextRepository;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIPromptService {
    private final PlayerContextRepository repository;
    private final PlayerContextMapper mapper;

    public AIPromptService(PlayerContextRepository repository, PlayerContextMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public String generatePrompt(String sessionId) {
        PlayerContextEntity entity = repository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        PlayerContext context = mapper.toPlayerContext(entity);
        ContextSummary summary = generateContextSummary(context);
        List<String> recentActions = getRecentActions(context, 3);

        return String.format("""
            GAME MASTER CONTEXT

            CURRENT GAME STATE:
            - Location: %s (previously: %s)
            - Player Health: %s
            - Player Reputation: %d (%s)
            - Session Duration: %.1f minutes
            - Player Mood: %s

            RECENT PLAYER ACTIONS:
            %s

            ACTIVE NPCS IN AREA:
            %s

            PLAYER CHARACTER:
            - Name: %s
            - Equipment: %s
            - Recent Focus: %s

            WORLD CONTEXT:
            %s

            GM INSTRUCTIONS:
            You are the AI Game Master for this fantasy RPG session. Based on the current context:
            1. Respond as the omniscient narrator and world
            2. Maintain consistency with previous interactions
            3. React appropriately to the player's reputation and recent actions
            4. Consider NPC relationships and dispositions
            5. Provide immersive, contextual descriptions
            6. Balance challenge with player agency

            Current situation requires your response as Game Master.""",
            summary.currentLocation(),
            formatPreviousLocation(summary.previousLocation()),
            summary.playerHealth(),
            summary.playerReputation(),
            getReputationDescription(summary.playerReputation()),
            summary.sessionDuration(),
            summary.playerMood(),
            formatRecentActions(recentActions),
            formatActiveNPCs(summary.activeNPCs()),
            context.character().name(),
            formatEquipment(context),
            determinePlayerFocus(context),
            formatWorldContext(summary.worldState())
        );
    }

    private ContextSummary generateContextSummary(PlayerContext context) {
        return new ContextSummary(
            context.location().current(),
            context.location().previous(), // TODO: Add previous location tracking
            String.format("%d/%d", context.character().health().current(), context.character().health().max()), // TODO: Add max health
            context.character().reputation(),
            Duration.between(context.startTime(), Instant.now()).toMinutes(),
            determinePlayerMood(context),
            getRecentActions(context, 5),
            List.of(), // TODO: Add NPC tracking
            Map.of(
                "locations_visited", 1, // TODO: Add location tracking
                "total_actions", 0, // TODO: Add action tracking
                "combat_experienced", false,
                "social_active", false
            )
        );
    }

    private List<String> getRecentActions(PlayerContext context, int count) {
        // TODO: Implement action history tracking
        return List.of("No recent actions");
    }

    private String formatPreviousLocation(String previous) {
        return previous != null && !previous.isEmpty() ? previous : "none";
    }

    private String getReputationDescription(int reputation) {
        if (reputation >= 80) return "revered";
        if (reputation >= 50) return "respected";
        if (reputation >= 20) return "friendly";
        if (reputation >= -20) return "neutral";
        if (reputation >= -50) return "distrusted";
        if (reputation >= -80) return "hostile";
        return "hated";
    }

    private String determinePlayerMood(PlayerContext context) {
        // TODO: Implement mood tracking
        return "neutral";
    }

    private String formatRecentActions(List<String> actions) {
        return actions.stream()
                .collect(Collectors.joining("\n"));
    }

    private String formatActiveNPCs(List<NPCContextInfo> npcs) {
        // TODO: Implement NPC tracking
        return "No active NPCs";
    }

    private String formatEquipment(PlayerContext context) {
        // TODO: Implement equipment tracking
        return "No equipment";
    }

    private String determinePlayerFocus(PlayerContext context) {
        // TODO: Implement player focus tracking
        return "exploration";
    }

    private String formatWorldContext(Map<String, Object> worldState) {
        return worldState.entrySet().stream()
                .map(e -> String.format("- %s: %s", e.getKey(), e.getValue()))
                .collect(Collectors.joining("\n"));
    }

    // Record classes for structured data
    private record ContextSummary(
        String currentLocation,
        String previousLocation,
        String playerHealth,
        int playerReputation,
        double sessionDuration,
        String playerMood,
        List<String> recentActions,
        List<NPCContextInfo> activeNPCs,
        Map<String, Object> worldState
    ) {}

    private record NPCContextInfo(
        String id,
        String name,
        int disposition,
        String mood,
        List<String> knownFacts,
        String lastSeen,
        String location,
        String relationship
    ) {}
} 