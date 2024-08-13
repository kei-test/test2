package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.PointLogResponseDTO;
import GInternational.server.api.entity.PointLog;
import GInternational.server.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PointLogResponseMapper extends GenericMapper<PointLogResponseDTO, PointLog> {
    PointLogResponseMapper INSTANCE = Mappers.getMapper(PointLogResponseMapper.class);

    @Mapping(source = "userId.id", target = "userId")
    PointLogResponseDTO toDto(PointLog pointLog);

    default User map(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
