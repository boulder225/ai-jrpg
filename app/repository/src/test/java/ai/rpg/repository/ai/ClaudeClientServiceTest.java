package ai.rpg.repository.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = OpenAiClientService.class))
public class ClaudeClientServiceTest {

    @Autowired
    private ClaudeClientService claudeClientService;

    @Test
    public void testChatCompletion() {
        String prompt = "Hello, Claude! Can you help me with a simple test?";
        
        StepVerifier.create(claudeClientService.chatCompletion(prompt))
                .expectNextMatches(response -> response != null && !response.isEmpty())
                .verifyComplete();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public String claudeApiKey() {
            return System.getenv("CLAUDE_API_KEY");
        }

        @Bean
        public String claudeApiBaseUrl() {
            return "https://api.anthropic.com/v1";
        }
    }
} 