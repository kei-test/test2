package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonPointRequestDTO;
import GInternational.server.api.entity.AmazonPoint;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonPointResponseMapper extends GenericMapper<AmazonPointRequestDTO, AmazonPoint> {
    AmazonPointResponseMapper INSTANCE = Mappers.getMapper(AmazonPointResponseMapper.class);
}
