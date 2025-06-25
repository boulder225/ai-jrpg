package ai.rpg.persistence.mapper;

import ai.rpg.core.domain.NPCRelationship;
import ai.rpg.core.domain.NPCRelationshipData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NPCRelationshipMapper {
    @Mapping(target = "mood", source = "mood.value")
    NPCRelationshipData toData(NPCRelationship domain);

    @Mapping(target = "mood", expression = "java(ai.rpg.core.domain.NPCMood.fromString(data.mood()))")
    NPCRelationship fromData(NPCRelationshipData data);
} 