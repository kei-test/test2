package GInternational.server.api.mapper;

import GInternational.server.api.dto.MessageRequestDTO;
import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.Messages;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MessageRequestMapper extends GenericMapper<MessageRequestDTO, Messages> {
    MessageRequestMapper INSTANCE = Mappers.getMapper(MessageRequestMapper.class);
}
