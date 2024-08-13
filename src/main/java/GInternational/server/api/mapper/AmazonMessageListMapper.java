package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonMessageListResponseDTO;
import GInternational.server.api.entity.AmazonMessages;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonMessageListMapper extends GenericMapper<AmazonMessageListResponseDTO, AmazonMessages> {
    AmazonMessageListMapper INSTANCE = Mappers.getMapper(AmazonMessageListMapper.class);
}
