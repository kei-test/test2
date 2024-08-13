package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.EventsBoardListDTO;
import GInternational.server.api.entity.EventsBoard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EventsBoardListResponseMapper extends GenericMapper<EventsBoardListDTO, EventsBoard> {
    EventsBoardListResponseMapper INSTANCE = Mappers.getMapper(EventsBoardListResponseMapper.class);

    @Mapping(source = "writer.id", target = "userId")
    EventsBoardListDTO toDto(EventsBoard eventsBoard);
}
