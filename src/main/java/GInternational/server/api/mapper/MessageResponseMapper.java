package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.MessageResponseDTO;
import GInternational.server.api.entity.Messages;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MessageResponseMapper extends GenericMapper<MessageResponseDTO, Messages>{
    MessageResponseMapper INSTANCE = Mappers.getMapper(MessageResponseMapper.class);
}

