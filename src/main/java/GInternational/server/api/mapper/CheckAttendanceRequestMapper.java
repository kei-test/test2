package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.CheckAttendanceRequestDTO;
import GInternational.server.api.entity.CheckAttendance;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CheckAttendanceRequestMapper extends GenericMapper<CheckAttendanceRequestDTO, CheckAttendance> {
    CheckAttendanceRequestMapper INSTANCE = Mappers.getMapper(CheckAttendanceRequestMapper.class);

}