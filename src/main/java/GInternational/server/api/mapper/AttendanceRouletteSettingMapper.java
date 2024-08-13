package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.AttendanceRouletteSettingDTO;
import GInternational.server.api.entity.AttendanceRouletteSettings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AttendanceRouletteSettingMapper extends GenericMapper<AttendanceRouletteSettingDTO, AttendanceRouletteSettings> {
    AttendanceRouletteSettingMapper INSTANCE = Mappers.getMapper(AttendanceRouletteSettingMapper.class);
}
