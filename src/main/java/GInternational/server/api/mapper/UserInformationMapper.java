package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.User;
import GInternational.server.api.dto.UserInformationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserInformationMapper extends GenericMapper<UserInformationDTO, User> {
    UserInformationMapper INSTANCE = Mappers.getMapper(UserInformationMapper.class);
}
