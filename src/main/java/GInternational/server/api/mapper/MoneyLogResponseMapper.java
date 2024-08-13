package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.MoneyLogResponseDTO;
import GInternational.server.api.entity.MoneyLog;
import GInternational.server.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MoneyLogResponseMapper extends GenericMapper<MoneyLogResponseDTO, MoneyLog> {
    MoneyLogResponseMapper INSTANCE = Mappers.getMapper(MoneyLogResponseMapper.class);

    @Mapping(source = "user.id", target = "userId")
    MoneyLogResponseDTO toDto(MoneyLog moneyLog);

    default User map(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
