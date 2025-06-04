package ai.rpg.context.service;

import ai.rpg.core.domain.*;
import ai.rpg.persistence.entity.ActionEventEntity;
import ai.rpg.persistence.entity.PlayerContextEntity;
import ai.rpg.persistence.mapper.PlayerContextMapper;
import ai.rpg.persistence.mapper.PlayerContextSummaryMapper;
import ai.rpg.persistence.mapper.ContextSummary;
import ai.rpg.persistence.repository.ActionEventRepository;
import ai.rpg.persistence.repository.PlayerContextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * FIXED: Context Manager Service - Core business logic for player context management
 * 
 * CRITICAL FIXES:
 * - Updated to work with proper JPA entity classes
 * - Fixed all mapping operations
 * - Added proper null-safety
 * - Corrected method signatures
 */
@Service
@Transactional
public class ContextManagerService {
    
    private static final Logger log = LoggerFactory.getLogger(ContextManagerService.class);
    
    // Configuration constants (matching Go implementation)
    private static final int MAX_ACTIONS = 50;
    private static final Duration CACHE_TIMEOUT = Duration.ofMinutes(30);
    private static final Duration CLEANUP_INTERVAL = Duration.ofHours(6);
    private static final Duration MAX_CONTEXT_AGE = Duration.ofDays(30);
    
    private final PlayerContextRepository playerContextRepository;
    private final ActionEventRepository actionEventRepository;
    private final PlayerContextMapper mapper;
    private final PlayerContextSummaryMapper summaryMapper;
    private final ApplicationEventPublisher eventPublisher;
    
    public ContextManagerService(
            PlayerContextRepository playerContextRepository,
            ActionEventRepository actionEventRepository,
            PlayerContextMapper mapper,
            PlayerContextSummaryMapper summaryMapper,
            ApplicationEventPublisher eventPublisher) {
        this.playerContextRepository = playerContextRepository;
        this.actionEventRepository = actionEventRepository;
        this.mapper = mapper;
        this.summaryMapper = summaryMapper;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Create a new player session
     * Replicates Go: CreateSession(playerID, playerName string) (string, error)
     */
    public String createSession(String playerId, String playerName) {
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player ID cannot be null or blank");
        }
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be null or blank");
        }
        
        var sessionId = UUID.randomUUID().toString();
        
        // Create entity directly instead of converting from domain
        var entity = new PlayerContextEntity(playerId, sessionId);
        entity.getCharacter().setName(playerName);
        
        entity = playerContextRepository.save(entity);
        
        // Publish session creation event
        eventPublisher.publishEvent(new SessionCreatedEvent(sessionId, playerId, playerName));
        
        log.info("Created new session {} for player {} ({})", sessionId, playerId, playerName);
        return sessionId;
    }
    
    /**
     * Get context for a session with caching
     * Replicates Go: GetContext(sessionID string) (*PlayerContext, error)
     */
    @Cacheable(value = "playerContexts", key = "#sessionId")
    @Transactional(readOnly = true)
    public Optional<PlayerContext> getContext(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return Optional.empty();
        }
        
        return playerContextRepository.findBySessionId(sessionId)
            .map(mapper::toDomain);
    }
    
    /**
     * Record a player action with consequences
     * Replicates Go: RecordAction(sessionID, command, actionType, target, location, outcome string, consequences []string) error
     */
    @CacheEvict(value = "playerContexts", key = "#sessionId")
    public void recordAction(String sessionId, String command, ActionType actionType,
                           String target, String location, String outcome, 
                           List<String> consequences) {
        
        var contextEntity = playerContextRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new SessionNotFoundException("Session not found: " + sessionId));
        
        // Create action event entity directly
        var actionEntity = new ActionEventEntity(
            UUID.randomUUID().toString(),
            actionType,
            command,
            target,
            location,
            outcome,
            consequences
        );
        
        // Add to context and save
        contextEntity.addAction(actionEntity);
        contextEntity.getSessionStats().incrementAction(actionType);
        contextEntity.updateLastUpdate();
        
        // Trim actions to prevent memory issues
        contextEntity.trimActions(MAX_ACTIONS);
        
        playerContextRepository.save(contextEntity);
        
        // Convert to domain event for publishing
        var domainAction = mapper.toDomain(actionEntity);
        eventPublisher.publishEvent(new ActionRecordedEvent(sessionId, domainAction));
        
        log.debug("Recorded action for session {}: {} -> {}", sessionId, command, outcome);
    }
    
    /**
     * Update player location
     * Replicates Go: UpdateLocation(sessionID, newLocation string) error
     */
    @CacheEvict(value = "playerContexts", key = "#sessionId")
    public void updateLocation(String sessionId, String newLocation) {
        var contextEntity = playerContextRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new SessionNotFoundException("Session not found: " + sessionId));
        
        var currentLocation = contextEntity.getLocation().getCurrent();
        
        if (!currentLocation.equals(newLocation)) {
            // Update location in entity
            var location = contextEntity.getLocation();
            location.setPrevious(currentLocation);
            location.setCurrent(newLocation);
            location.setTimeInLocationMinutes(0);
            
            // Update visit count
            if (newLocation.equals(currentLocation)) {
                location.setVisitCount(location.getVisitCount() + 1);
            } else {
                location.setVisitCount(1);
                contextEntity.getSessionStats().setLocationsVisited(
                    contextEntity.getSessionStats().getLocationsVisited() + 1
                );
            }
            
            contextEntity.updateLastUpdate();
            playerContextRepository.save(contextEntity);
            
            // Publish location change event
            eventPublisher.publishEvent(new LocationChangedEvent(sessionId, currentLocation, newLocation));
            
            log.debug("Updated location for session {}: {} -> {}", sessionId, currentLocation, newLocation);
        }
    }
    
    /**
     * Update NPC relationship
     * Replicates Go: UpdateNPCRelationship(sessionID, npcID, npcName string, dispositionChange int, facts []string) error
     */
    @CacheEvict(value = "playerContexts", key = "#sessionId")
    public void updateNPCRelationship(String sessionId, String npcId, String npcName,
                                    int dispositionChange, List<String> newFacts) {
        var contextEntity = playerContextRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new SessionNotFoundException("Session not found: " + sessionId));
        
        var npcStates = contextEntity.getNpcStates();
        var currentData = npcStates.get(npcId);
        
        if (currentData == null) {
            // First meeting - create new NPC relationship data
            var now = Instant.now();
            var newData = new ai.rpg.persistence.entity.NPCRelationshipData(
                npcId, npcName, dispositionChange, now, now, 1, 
                newFacts != null ? List.copyOf(newFacts) : List.of(),
                determineNPCMood(dispositionChange), 
                contextEntity.getLocation().getCurrent(), 
                List.of()
            );
            
            npcStates.put(npcId, newData);
            contextEntity.getSessionStats().setNpcsInteracted(
                contextEntity.getSessionStats().getNpcsInteracted() + 1
            );
        } else {
            // Update existing relationship
            var newDisposition = Math.max(-100, Math.min(100, currentData.getDisposition() + dispositionChange));
            currentData.setDisposition(newDisposition);
            currentData.setLastInteraction(Instant.now());
            currentData.setInteractionCount(currentData.getInteractionCount() + 1);
            currentData.setMood(determineNPCMood(newDisposition));
            
            // Add new facts
            if (newFacts != null) {
                var existingFacts = currentData.getKnownFacts();
                for (var fact : newFacts) {
                    if (!existingFacts.contains(fact)) {
                        existingFacts.add(fact);
                    }
                }
            }
        }
        
        contextEntity.updateLastUpdate();
        playerContextRepository.save(contextEntity);
        
        // Publish NPC interaction event
        eventPublisher.publishEvent(new NPCInteractionEvent(sessionId, npcId, dispositionChange));
        
        log.debug("Updated NPC relationship for session {}: {} (disposition: {})", 
                 sessionId, npcName, dispositionChange);
    }
    
    /**
     * Update character health
     * Replicates Go: UpdateCharacterHealth(sessionID string, healthChange int) error
     */
    @CacheEvict(value = "playerContexts", key = "#sessionId")
    public void updateCharacterHealth(String sessionId, int healthChange) {
        var contextEntity = playerContextRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new SessionNotFoundException("Session not found: " + sessionId));
        
        var character = contextEntity.getCharacter();
        var newHealth = Math.max(0, Math.min(character.getHealthMax(), 
                                           character.getHealthCurrent() + healthChange));
        
        character.setHealthCurrent(newHealth);
        contextEntity.updateLastUpdate();
        playerContextRepository.save(contextEntity);
        
        // Publish health change event
        eventPublisher.publishEvent(new HealthChangedEvent(sessionId, healthChange, newHealth));
        
        log.debug("Updated health for session {}: {} -> {}", sessionId, healthChange, newHealth);
    }
    
    /**
     * Update player reputation
     * Replicates Go: UpdateReputation(sessionID string, reputationChange int) error
     */
    @CacheEvict(value = "playerContexts", key = "#sessionId")
    public void updateReputation(String sessionId, int reputationChange) {
        var contextEntity = playerContextRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new SessionNotFoundException("Session not found: " + sessionId));
        
        var character = contextEntity.getCharacter();
        var newReputation = Math.max(-100, Math.min(100, character.getReputation() + reputationChange));
        
        character.setReputation(newReputation);
        contextEntity.updateLastUpdate();
        playerContextRepository.save(contextEntity);
        
        // Publish reputation change event
        eventPublisher.publishEvent(new ReputationChangedEvent(sessionId, reputationChange, newReputation));
        
        log.debug("Updated reputation for session {}: {} -> {}", sessionId, reputationChange, newReputation);
    }
    
    /**
     * Get recent actions for AI context
     * Replicates Go: GetRecentActions(sessionID string, count int) ([]ActionEvent, error)
     */
    @Transactional(readOnly = true)
    public List<ActionEvent> getRecentActions(String sessionId, int count) {
        var pageRequest = PageRequest.of(0, count);
        var actionEntities = actionEventRepository.findRecentActionsBySessionId(sessionId, pageRequest);
        
        return actionEntities.getContent().stream()
            .map(mapper::toDomain)
            .toList();
    }
    
    /**
     * Generate AI prompt from context
     * Replicates Go: GenerateAIPrompt(sessionID string) (string, error)
     */
    @Transactional(readOnly = true)
    public String generateAIPrompt(String sessionId) {
        var contextEntity = playerContextRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new SessionNotFoundException("Session not found: " + sessionId));
        
        var recentActions = getRecentActions(sessionId, 10);
        var context = mapper.toDomain(contextEntity);
        
        return buildAIPrompt(context, recentActions);
    }
    
    /**
     * Get context summary for API responses
     */
    @Transactional(readOnly = true)
    public ContextSummary getContextSummary(String sessionId) {
        var contextEntity = playerContextRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new SessionNotFoundException("Session not found: " + sessionId));
        
        return summaryMapper.toSummary(contextEntity);
    }
    
    /**
     * List active sessions
     * Replicates Go: ListActiveSessions() ([]string, error)
     */
    @Transactional(readOnly = true)
    public List<String> listActiveSessions() {
        var since = Instant.now().minus(Duration.ofHours(1));
        return playerContextRepository.findActiveSessions(since).stream()
            .map(PlayerContextEntity::getSessionId)
            .toList();
    }
    
    /**
     * Get session metrics for monitoring
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSessionMetrics() {
        var since = Instant.now().minus(Duration.ofHours(1));
        var activeCount = playerContextRepository.countActiveSessions(since);
        var totalSessions = playerContextRepository.count();
        
        return Map.of(
            "activeSessions", activeCount,
            "totalSessions", totalSessions,
            "cacheSize", "N/A", // Redis cache size would be queried separately
            "lastCleanup", "N/A" // Would be tracked separately
        );
    }
    
    /**
     * Cleanup old sessions (scheduled operation)
     */
    @Transactional
    public int cleanupOldSessions() {
        var cutoff = Instant.now().minus(MAX_CONTEXT_AGE);
        var oldSessions = playerContextRepository.findSessionsForCleanup(cutoff);
        
        for (var session : oldSessions) {
            playerContextRepository.delete(session);
        }
        
        log.info("Cleaned up {} old sessions", oldSessions.size());
        return oldSessions.size();
    }
    
    // Private helper methods
    
    private String buildAIPrompt(PlayerContext context, List<ActionEvent> recentActions) {
        var prompt = new StringBuilder();
        
        prompt.append("GAME MASTER CONTEXT\n\n");
        prompt.append("CURRENT GAME STATE:\n");
        prompt.append("- Location: ").append(context.location().current());
        if (context.location().previous() != null) {
            prompt.append(" (previously: ").append(context.location().previous()).append(")");
        }
        prompt.append("\n");
        prompt.append("- Player Health: ")
            .append(context.character().health().current())
            .append("/")
            .append(context.character().health().max())
            .append("\n");
        prompt.append("- Player Reputation: ").append(context.character().reputation());
        
        var reputationDesc = getReputationDescription(context.character().reputation());
        prompt.append(" (").append(reputationDesc).append(")\n");
        
        var sessionDuration = Duration.between(context.startTime(), context.lastUpdate()).toMinutes();
        prompt.append("- Session Duration: ").append(sessionDuration).append(" minutes\n");
        
        prompt.append("- Player Mood: ").append(determinePlayerMood(context)).append("\n\n");
        
        prompt.append("RECENT PLAYER ACTIONS:\n");
        for (var action : recentActions) {
            var timeAgo = Duration.between(action.timestamp(), Instant.now()).toMinutes();
            prompt.append("- ").append(timeAgo).append(" min ago: ")
                .append(action.command()).append(" (").append(action.type().getValue()).append(") -> ")
                .append(action.outcome()).append("\n");
        }
        
        prompt.append("\nACTIVE NPCS IN AREA:\n");
        context.npcStates().values().stream()
            .filter(npc -> npc.location().equals(context.location().current()))
            .forEach(npc -> {
                var lastSeen = Duration.between(npc.lastInteraction(), Instant.now()).toMinutes();
                prompt.append("- ").append(npc.name())
                    .append(" (").append(npc.npcId()).append("): ")
                    .append(npc.mood().getValue()).append(" mood, ")
                    .append(npc.getRelationshipLevel()).append(" relationship")
                    .append(" (last seen ").append(lastSeen).append(" min ago)\n");
                
                if (!npc.knownFacts().isEmpty()) {
                    prompt.append("  - Knows: ").append(String.join(", ", npc.knownFacts())).append("\n");
                }
            });
        
        prompt.append("\nGM INSTRUCTIONS:\n");
        prompt.append("1. Respond as the omniscient narrator and world\n");
        prompt.append("2. Maintain consistency with previous interactions\n");
        prompt.append("3. React appropriately to the player's reputation and recent actions\n");
        prompt.append("4. Consider NPC relationships and dispositions\n");
        prompt.append("5. Provide immersive, contextual descriptions\n");
        prompt.append("6. Balance challenge with player agency\n\n");
        prompt.append("Current situation requires your response as Game Master.");
        
        return prompt.toString();
    }
    
    private String getReputationDescription(int reputation) {
        return switch (reputation) {
            case int r when r >= 75 -> "Legendary Hero";
            case int r when r >= 50 -> "Renowned";
            case int r when r >= 25 -> "Respected";
            case int r when r >= 0 -> "Neutral";
            case int r when r >= -25 -> "Disliked";
            case int r when r >= -50 -> "Despised";
            case int r when r >= -75 -> "Notorious";
            default -> "Villain";
        };
    }
    
    private String determinePlayerMood(PlayerContext context) {
        var healthPercentage = (double) context.character().health().current() / 
                              context.character().health().max();
        var reputation = context.character().reputation();
        
        if (healthPercentage > 0.8 && reputation > 25) {
            return "confident";
        } else if (healthPercentage < 0.3) {
            return "desperate";
        } else if (reputation < -25) {
            return "troubled";
        } else {
            return "focused";
        }
    }
    
    private String determineNPCMood(int disposition) {
        return switch (disposition) {
            case int d when d >= 80 -> "ecstatic";
            case int d when d >= 60 -> "joyful";
            case int d when d >= 40 -> "friendly";
            case int d when d >= 20 -> "helpful";
            case int d when d >= -20 -> "neutral";
            case int d when d >= -40 -> "suspicious";
            case int d when d >= -60 -> "unfriendly";
            case int d when d >= -80 -> "hostile";
            default -> "enraged";
        };
    }
}

/**
 * Custom exceptions
 */
class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String message) {
        super(message);
    }
}

/**
 * Event classes for Spring Event publishing
 */
record SessionCreatedEvent(String sessionId, String playerId, String playerName) {}
record ActionRecordedEvent(String sessionId, ActionEvent action) {}
record LocationChangedEvent(String sessionId, String fromLocation, String toLocation) {}
record NPCInteractionEvent(String sessionId, String npcId, int dispositionChange) {}
record HealthChangedEvent(String sessionId, int healthChange, int newHealth) {}
record ReputationChangedEvent(String sessionId, int reputationChange, int newReputation) {}
