package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.EventsBoardRequestDTO;
import GInternational.server.api.dto.EventsBoardResponseDTO;
import GInternational.server.api.entity.EventsBoard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EventsBoardResponseMapper extends GenericMapper<EventsBoardResponseDTO, EventsBoard> {
    EventsBoardResponseMapper INSTANCE = Mappers.getMapper(EventsBoardResponseMapper.class);

    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    EventsBoard toEntity(EventsBoardRequestDTO dto);
}
