package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.MessageListResponseDTO;
import GInternational.server.api.entity.Messages;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MessageListMapper extends GenericMapper<MessageListResponseDTO, Messages> {
    MessageListMapper INSTANCE = Mappers.getMapper(MessageListMapper.class);
}
