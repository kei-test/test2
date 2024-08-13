package GInternational.server.api.mapper;

import GInternational.server.api.dto.JoinPointDTO;
import GInternational.server.api.entity.JoinPoint;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface JoinPointMapper extends GenericMapper<JoinPointDTO, JoinPoint> {
    JoinPointMapper INSTANCE = Mappers.getMapper(JoinPointMapper.class);
}
