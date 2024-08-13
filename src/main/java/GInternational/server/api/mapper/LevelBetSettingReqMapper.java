package GInternational.server.api.mapper;

import GInternational.server.api.dto.LevelBetSettingReqDTO;
import GInternational.server.api.entity.LevelBetSetting;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LevelBetSettingReqMapper extends GenericMapper<LevelBetSettingReqDTO, LevelBetSetting> {
    LevelBetSettingReqMapper INSTANCE = Mappers.getMapper(LevelBetSettingReqMapper.class);
}
