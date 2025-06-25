package ai.rpg.core.domain;

public enum ActionType {
    MOVE("move"),
    TALK("talk"),
    ATTACK("attack"),
    EXAMINE("examine"),
    USE("use"),
    CAST("cast"),
    TRADE("trade"),
    REST("rest"),
    COMBAT("combat"),
    UNKNOWN("unknown");

    private final String value;

    ActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // The fromCommand method remains in ActionEvent.java
} 