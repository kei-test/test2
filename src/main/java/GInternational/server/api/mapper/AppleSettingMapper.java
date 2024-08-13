package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.AppleSettingDTO;
import GInternational.server.api.entity.AppleSettings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AppleSettingMapper extends GenericMapper<AppleSettingDTO, AppleSettings> {
    AppleSettingMapper INSTANCE = Mappers.getMapper(AppleSettingMapper.class);
}

