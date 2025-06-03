package ai.rpg.repository.controller;

import ai.rpg.repository.ai.ClaudeClientService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/claude")
public class ClaudeController {
    private final ClaudeClientService claudeClientService;

    public ClaudeController(ClaudeClientService claudeClientService) {
        this.claudeClientService = claudeClientService;
    }

    @PostMapping("/chat")
    public Mono<String> chat(@RequestBody String prompt) {
        return claudeClientService.getChatCompletion(prompt);
    }
} 