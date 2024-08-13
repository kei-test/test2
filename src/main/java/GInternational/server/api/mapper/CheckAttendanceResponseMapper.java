package GInternational.server.api.mapper;

import GInternational.server.api.dto.CheckAttendanceResponseDTO;
import GInternational.server.api.entity.CheckAttendance;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CheckAttendanceResponseMapper extends GenericMapper<CheckAttendanceResponseDTO, CheckAttendance> {
    CheckAttendanceResponseMapper INSTANCE = Mappers.getMapper(CheckAttendanceResponseMapper.class);
}