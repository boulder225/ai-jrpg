package ai.rpg.persistence.mapper;

import ai.rpg.core.domain.EquipmentItem;
import ai.rpg.core.domain.EquipmentItemData;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EquipmentMapper {
    EquipmentItemData toData(EquipmentItem domain);
    EquipmentItem fromData(EquipmentItemData data);
} 