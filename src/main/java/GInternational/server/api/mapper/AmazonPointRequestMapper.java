package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonPointRequestDTO;
import GInternational.server.api.entity.AmazonPoint;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonPointRequestMapper extends GenericMapper<AmazonPointRequestDTO, AmazonPoint> {
    AmazonPointRequestMapper INSTANCE = Mappers.getMapper(AmazonPointRequestMapper.class);
}
