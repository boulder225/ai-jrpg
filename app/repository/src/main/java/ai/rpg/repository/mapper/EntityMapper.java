package ai.rpg.repository.mapper;

import ai.rpg.persistence.entity.ActionEventEntity;
import ai.rpg.persistence.entity.PlayerContextEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    // If you need to map between different types, define those methods here.
    // For now, just provide identity mappings as placeholders.
    ActionEventEntity toEntity(ActionEventEntity entity);
    ActionEventEntity toDomain(ActionEventEntity entity);
    PlayerContextEntity toEntity(PlayerContextEntity entity);
    PlayerContextEntity toDomain(PlayerContextEntity entity);
} 