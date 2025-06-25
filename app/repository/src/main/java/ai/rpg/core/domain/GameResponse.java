package ai.rpg.core.domain;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class GameResponse {
    private boolean success;
    private String message;
    private String sessionId;
    private Object context;
    private String error;

    public static GameResponse success(String message, String sessionId, Object context) {
        return GameResponse.builder()
                .success(true)
                .message(message)
                .sessionId(sessionId)
                .context(context)
                .build();
    }

    public static GameResponse error(String message, String error) {
        return GameResponse.builder()
                .success(false)
                .message(message)
                .error(error)
                .build();
    }
} 