package ai.rpg.persistence.mapper;

import ai.rpg.persistence.entity.PlayerContextEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PlayerContextSummaryMapper {

    @Mapping(target = "currentLocation", source = "location.current")
    @Mapping(target = "previousLocation", source = "location.previous")
    @Mapping(target = "playerHealth", expression = "java(entity.getCharacter().getHealthCurrent() + \"/\" + entity.getCharacter().getHealthMax())")
    @Mapping(target = "playerReputation", source = "character.reputation")
    @Mapping(target = "sessionDuration", expression = "java(calculateSessionDuration(entity))")
    @Mapping(target = "playerMood", expression = "java(determinePlayerMood(entity))")
    @Mapping(target = "recentActions", expression = "java(getRecentActionStrings(entity))")
    @Mapping(target = "activeNPCs", expression = "java(java.util.List.of())") // Simplified for now
    @Mapping(target = "worldState", expression = "java(java.util.Map.of())")
    ContextSummary toSummary(PlayerContextEntity entity);

    default double calculateSessionDuration(PlayerContextEntity entity) {
        if (entity.getStartTime() == null || entity.getLastUpdate() == null) {
            return 0.0;
        }
        return java.time.Duration.between(entity.getStartTime(), entity.getLastUpdate()).toMinutes();
    }

    default String determinePlayerMood(PlayerContextEntity entity) {
        var character = entity.getCharacter();
        if (character == null) return "neutral";
        double healthPercentage = (double) character.getHealthCurrent() / character.getHealthMax();
        int reputation = character.getReputation();
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

    default List<String> getRecentActionStrings(PlayerContextEntity entity) {
        if (entity.getActions() == null) return List.of();
        return entity.getActions().stream()
            .limit(5)
            .map(action -> action.getCommand() + " -> " + action.getOutcome())
            .collect(Collectors.toList());
    }
}