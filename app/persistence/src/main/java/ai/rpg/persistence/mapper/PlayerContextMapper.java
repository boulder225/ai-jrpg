package ai.rpg.persistence.mapper;

import ai.rpg.core.domain.*;
import ai.rpg.persistence.entity.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FIXED: MapStruct mapper for converting between domain records and JPA entities
 * 
 * CRITICAL FIXES:
 * - Updated to work with proper JPA entity classes (not records)
 * - Added proper null-safe mapping
 * - Fixed mapping method signatures
 * - Added defensive copying where needed
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
    uses = {
        EquipmentMapper.class,
        InventoryMapper.class,
        LocationVisitMapper.class,
        NPCRelationshipMapper.class
    }
)
public interface PlayerContextMapper {
    
    PlayerContextMapper INSTANCE = Mappers.getMapper(PlayerContextMapper.class);
    
    // =================================================================
    // Primary Entity Mapping
    // =================================================================
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "character", source = "character")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "npcStates", source = "npcStates")
    @Mapping(target = "sessionStats", source = "sessionStats")
    PlayerContextEntity toEntity(PlayerContext domain);
    
    @Mapping(target = "actions", source = "actions", qualifiedByName = "mapActionsFromEntity")
    @Mapping(target = "npcStates", source = "npcStates")
    @Mapping(target = "character", source = "character")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "sessionStats", source = "sessionStats")
    PlayerContext toPlayerContext(PlayerContextEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actions", ignore = true)
    void updateEntityFromDomain(PlayerContext domain, @MappingTarget PlayerContextEntity entity);
    
    // =================================================================
    // Sub-Object Mappings (Embeddables, Data, Entities)
    // =================================================================
    
    @Named("characterStateToEmbeddable")
    @Mapping(target = "healthCurrent", source = "health.current")
    @Mapping(target = "healthMax", source = "health.max")
    CharacterStateEmbeddable characterStateToEmbeddable(CharacterState domain);
    
    @Named("toCharacterState")
    @Mapping(target = "health", expression = "java(new ai.rpg.core.domain.HealthStatus(embeddable.getHealthCurrent(), embeddable.getHealthMax()))")
    CharacterState toCharacterState(CharacterStateEmbeddable embeddable);
    
    @Named("locationStateToEmbeddable")
    LocationStateEmbeddable locationStateToEmbeddable(LocationState domain);
    
    @Named("toLocationState")
    LocationState toLocationState(LocationStateEmbeddable embeddable);
    
    @Named("sessionMetricsToEmbeddable")
    SessionMetricsEmbeddable sessionMetricsToEmbeddable(SessionMetrics domain);
    
    @Named("toSessionMetrics")
    SessionMetrics toSessionMetrics(SessionMetricsEmbeddable embeddable);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actionId", source = "id")
    @Mapping(target = "playerContext", ignore = true)
    ActionEventEntity actionEventToEntity(ActionEvent domain);
    
    @Mapping(target = "id", source = "actionId")
    ActionEvent toActionEvent(ActionEventEntity entity);
    
    // =================================================================
    // Custom Qualified Methods for Complex Transformations
    // =================================================================
    
    @Named("mapActionsFromEntity")
    default List<ActionEvent> mapActionsFromEntity(List<ActionEventEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toActionEvent).collect(Collectors.toList());
    }
    
    // =================================================================
    // Helper Methods
    // =================================================================
    
    @Named("stringToNPCMood")
    default NPCMood stringToNPCMood(String mood) {
        if (mood == null) return NPCMood.NEUTRAL;
        
        try {
            // First try direct enum matching
            return NPCMood.valueOf(mood.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Fallback to parsing by value
            for (NPCMood npcMood : NPCMood.values()) {
                if (npcMood.getValue().equalsIgnoreCase(mood)) {
                    return npcMood;
                }
            }
            return NPCMood.NEUTRAL;
        }
    }
}
