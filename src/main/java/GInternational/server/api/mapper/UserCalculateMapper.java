package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.User;
import GInternational.server.api.dto.UserCalculateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserCalculateMapper extends GenericMapper<UserCalculateDTO, User> {
    UserCalculateMapper INSTANCE = Mappers.getMapper(UserCalculateMapper.class);

}
