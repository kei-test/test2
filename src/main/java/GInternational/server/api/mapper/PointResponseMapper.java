package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.PointResponseDTO;
import GInternational.server.api.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PointResponseMapper extends GenericMapper<PointResponseDTO, Wallet> {
    PointResponseMapper INSTANCE = Mappers.getMapper(PointResponseMapper.class);
}
