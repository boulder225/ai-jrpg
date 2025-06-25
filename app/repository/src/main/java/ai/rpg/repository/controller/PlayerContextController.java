package ai.rpg.repository.controller;

import ai.rpg.core.domain.PlayerContext;
import ai.rpg.core.domain.PlayerCommand;
import ai.rpg.core.domain.GameResponse;
import ai.rpg.persistence.entity.PlayerContextEntity;
import ai.rpg.persistence.mapper.PlayerContextMapper;
import ai.rpg.repository.repository.PlayerContextRepository;
import ai.rpg.repository.service.AIPromptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api")
public class PlayerContextController {
    private final PlayerContextRepository repository;
    private final PlayerContextMapper mapper;
    private final AIPromptService aiPromptService;

    public PlayerContextController(
            PlayerContextRepository repository, 
            PlayerContextMapper mapper,
            AIPromptService aiPromptService) {
        this.repository = repository;
        this.mapper = mapper;
        this.aiPromptService = aiPromptService;
    }

    @PostMapping("/session/create")
    @Transactional
    public ResponseEntity<GameResponse> createSession(@RequestBody PlayerCommand command) {
        if (command.getPlayerId() == null || command.getPlayerName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GameResponse.error("Invalid request", "PlayerID and PlayerName are required"));
        }

        // Check if player already has an active session
        List<PlayerContextEntity> activeSessions = repository.findActiveSessionsByPlayerId(command.getPlayerId());
        if (!activeSessions.isEmpty()) {
            // Deactivate existing sessions
            activeSessions.forEach(session -> repository.deactivateSession(session.getSessionId()));
        }

        // Create new session
        PlayerContextEntity newSession = new PlayerContextEntity();
        newSession.setPlayerId(command.getPlayerId());
        newSession.getCharacter().setName(command.getPlayerName());
        newSession.setSessionId(UUID.randomUUID().toString());
        newSession = repository.save(newSession);
        
        return ResponseEntity.ok(GameResponse.success(
            String.format("Welcome to the adventure, %s! Your journey begins in a small village.", command.getPlayerName()),
            newSession.getSessionId(),
            mapper.toPlayerContext(newSession)
        ));
    }

    @PostMapping("/game/action")
    @Transactional
    public ResponseEntity<GameResponse> handleGameAction(@RequestBody PlayerCommand command) {
        if (command.getSessionId() == null || command.getCommand() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GameResponse.error("Invalid request", "SessionID and Command are required"));
        }

        return repository.findBySessionId(command.getSessionId())
                .map(entity -> {
                    repository.updateLastAccess(command.getSessionId());
                    // TODO: Process game command and update context
                    return ResponseEntity.ok(GameResponse.success(
                        "Action processed successfully",
                        command.getSessionId(),
                        mapper.toPlayerContext(entity)
                    ));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(GameResponse.error("Session not found", "Invalid session ID")));
    }

    @GetMapping("/game/status")
    public ResponseEntity<GameResponse> getGameStatus(@RequestParam String sessionId) {
        return repository.findBySessionId(sessionId)
                .map(entity -> {
                    repository.updateLastAccess(sessionId);
                    return ResponseEntity.ok(GameResponse.success(
                        "Context retrieved successfully",
                        sessionId,
                        mapper.toPlayerContext(entity)
                    ));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(GameResponse.error("Session not found", "Invalid session ID")));
    }

    @GetMapping("/metrics")
    public ResponseEntity<GameResponse> getMetrics() {
        List<PlayerContext> activeSessions = repository.findAllActiveSessions()
                .stream()
                .map(mapper::toPlayerContext)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(GameResponse.success(
            "Metrics retrieved successfully",
            null,
            Map.of(
                "active_sessions", activeSessions.size(),
                "sessions", activeSessions
            )
        ));
    }

    @GetMapping("/ai/prompt")
    public ResponseEntity<GameResponse> getAIPrompt(@RequestParam String sessionId) {
        try {
            String prompt = aiPromptService.generatePrompt(sessionId);
            return ResponseEntity.ok(GameResponse.success(
                prompt,
                sessionId,
                null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GameResponse.error("Session not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GameResponse.error("Failed to generate prompt", e.getMessage()));
        }
    }
} 