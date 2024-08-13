package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.User;
import GInternational.server.api.dto.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserResponseMapper extends GenericMapper<UserResponseDTO, User> {
    UserResponseMapper INSTANCE = Mappers.getMapper(UserResponseMapper.class);


}
