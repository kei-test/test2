package GInternational.server.api.mapper;

import GInternational.server.api.dto.LevelBetSettingResDTO;
import GInternational.server.api.entity.LevelBetSetting;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LevelBetSettingResMapper extends GenericMapper<LevelBetSettingResDTO, LevelBetSetting> {
    LevelBetSettingResMapper INSTANCE = Mappers.getMapper(LevelBetSettingResMapper.class);
}
