package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.RouletteSpinResultDTO;
import GInternational.server.api.entity.RouletteResults;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RouletteResultMapper extends GenericMapper<RouletteSpinResultDTO, RouletteResults> {
    RouletteResultMapper INSTANCE = Mappers.getMapper(RouletteResultMapper.class);

    @Mapping(target = "userId", ignore = true)
    RouletteSpinResultDTO toDto(RouletteResults rouletteResults);

    @Mapping(target = "userId", ignore = true)
    RouletteResults toEntity(RouletteSpinResultDTO dto);
}
