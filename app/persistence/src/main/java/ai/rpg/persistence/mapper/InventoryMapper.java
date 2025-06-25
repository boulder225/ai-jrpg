package ai.rpg.persistence.mapper;

import ai.rpg.core.domain.InventoryItem;
import ai.rpg.core.domain.InventoryItemData;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InventoryMapper {
    InventoryItemData toData(InventoryItem domain);
    InventoryItem fromData(InventoryItemData data);
} 