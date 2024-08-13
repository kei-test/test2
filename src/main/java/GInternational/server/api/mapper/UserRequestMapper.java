package GInternational.server.api.mapper;



import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.User;
import GInternational.server.api.dto.UserRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserRequestMapper extends GenericMapper<UserRequestDTO, User> {
    UserRequestMapper INSTANCE = Mappers.getMapper(UserRequestMapper.class);
}
