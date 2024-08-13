package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.EventsBoardRequestDTO;
import GInternational.server.api.entity.EventsBoard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EventsBoardRequestMapper extends GenericMapper<EventsBoardRequestDTO, EventsBoard> {
    EventsBoardRequestMapper INSTANCE = Mappers.getMapper(EventsBoardRequestMapper.class);

    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    EventsBoard toEntity(EventsBoardRequestDTO dto);
}
