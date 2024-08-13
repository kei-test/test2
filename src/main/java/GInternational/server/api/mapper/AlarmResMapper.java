package GInternational.server.api.mapper;

import GInternational.server.api.dto.AlarmResDTO;
import GInternational.server.api.entity.Alarm;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlarmResMapper extends GenericMapper<AlarmResDTO, Alarm> {
}
