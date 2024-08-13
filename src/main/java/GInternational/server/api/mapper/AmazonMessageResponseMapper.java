package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonMessageResponseDTO;
import GInternational.server.api.entity.AmazonMessages;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonMessageResponseMapper extends GenericMapper<AmazonMessageResponseDTO, AmazonMessages>{
    AmazonMessageResponseMapper INSTANCE = Mappers.getMapper(AmazonMessageResponseMapper.class);
}

