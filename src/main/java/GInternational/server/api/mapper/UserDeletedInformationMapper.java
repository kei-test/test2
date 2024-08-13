package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.User;
import GInternational.server.api.dto.UserDeletedInformationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserDeletedInformationMapper extends GenericMapper<UserDeletedInformationDTO, User> {
    UserDeletedInformationMapper INSTANCE = Mappers.getMapper(UserDeletedInformationMapper.class);
}
