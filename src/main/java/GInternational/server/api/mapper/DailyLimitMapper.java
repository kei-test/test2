package GInternational.server.api.mapper;

import GInternational.server.api.dto.DailyLimitDTO;
import GInternational.server.api.entity.DailyLimit;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DailyLimitMapper extends GenericMapper<DailyLimitDTO, DailyLimit> {
    DailyLimitMapper INSTANCE = Mappers.getMapper(DailyLimitMapper.class);
}
