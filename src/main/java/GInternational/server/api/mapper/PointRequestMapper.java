package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.PointRequestDTO;
import GInternational.server.api.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PointRequestMapper extends GenericMapper<PointRequestDTO, Wallet> {
    PointRequestMapper INSTANCE = Mappers.getMapper(PointRequestMapper.class);
}
