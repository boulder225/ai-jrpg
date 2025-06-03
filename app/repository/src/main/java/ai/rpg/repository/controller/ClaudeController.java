package ai.rpg.repository.controller;

import ai.rpg.repository.ai.ClaudeClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/claude")
public class ClaudeController {

    private final ClaudeClientService claudeClientService;

    @Autowired
    public ClaudeController(ClaudeClientService claudeClientService) {
        this.claudeClientService = claudeClientService;
    }

    @PostMapping("/chat")
    public Mono<String> chatCompletion(@RequestBody String prompt) {
        return claudeClientService.chatCompletion(prompt);
    }
} 