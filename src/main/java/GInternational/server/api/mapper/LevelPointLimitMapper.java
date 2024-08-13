package GInternational.server.api.mapper;

import GInternational.server.api.dto.LevelPointLimitDTO;
import GInternational.server.api.entity.LevelPointLimit;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LevelPointLimitMapper extends GenericMapper<LevelPointLimitDTO, LevelPointLimit> {
    LevelPointLimitMapper INSTANCE = Mappers.getMapper(LevelPointLimitMapper.class);
}
