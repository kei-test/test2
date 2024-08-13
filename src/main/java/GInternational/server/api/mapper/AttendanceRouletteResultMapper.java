package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.AttendanceRouletteSpinResultDTO;
import GInternational.server.api.entity.AttendanceRouletteResults;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AttendanceRouletteResultMapper extends GenericMapper<AttendanceRouletteSpinResultDTO,
        AttendanceRouletteResults> {
    AttendanceRouletteResultMapper INSTANCE = Mappers.getMapper(AttendanceRouletteResultMapper.class);

    @Mapping(target = "userId", ignore = true)
    AttendanceRouletteSpinResultDTO toDto(AttendanceRouletteResults attendanceRouletteResults);

    @Mapping(target = "userId", ignore = true)
    AttendanceRouletteResults toEntity(AttendanceRouletteSpinResultDTO dto);
}

