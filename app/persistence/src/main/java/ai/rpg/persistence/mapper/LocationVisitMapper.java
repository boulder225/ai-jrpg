package ai.rpg.persistence.mapper;

import ai.rpg.core.domain.LocationVisit;
import ai.rpg.core.domain.LocationVisitData;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LocationVisitMapper {
    LocationVisitData toData(LocationVisit domain);
    LocationVisit fromData(LocationVisitData data);
} 