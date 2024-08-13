package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.LoginStatisticDTO;
import GInternational.server.api.entity.LoginStatistic;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginStatisticMapper extends GenericMapper<LoginStatisticDTO, LoginStatistic> {
}
