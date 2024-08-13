package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonMessageRequestDTO;
import GInternational.server.api.entity.AmazonMessages;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonMessageRequestMapper extends GenericMapper<AmazonMessageRequestDTO, AmazonMessages> {
    AmazonMessageRequestMapper INSTANCE = Mappers.getMapper(AmazonMessageRequestMapper.class);
}
