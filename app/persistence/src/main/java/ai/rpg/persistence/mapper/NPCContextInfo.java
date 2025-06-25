package ai.rpg.persistence.mapper;

public record NPCContextInfo(
    String id,
    String name,
    int disposition,
    String mood,
    java.util.List<String> knownFacts,
    String lastSeen,
    String location,
    String relationship
) {} 