package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.User;
import GInternational.server.api.dto.UserListDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserListMapper extends GenericMapper<UserListDTO, User> {
    UserListMapper INSTANCE = Mappers.getMapper(UserListMapper.class);
}
