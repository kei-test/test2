package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.RouletteSettingDTO;
import GInternational.server.api.entity.RouletteSettings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RouletteSettingMapper extends GenericMapper<RouletteSettingDTO, RouletteSettings> {
    RouletteSettingMapper INSTANCE = Mappers.getMapper(RouletteSettingMapper.class);

}
