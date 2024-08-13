package GInternational.server.api.mapper;

import GInternational.server.api.dto.TradeLogResponseDTO;
import GInternational.server.api.entity.TradeLog;
import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TradeLogResponseMapper extends GenericMapper<TradeLogResponseDTO, TradeLog> {
    TradeLogResponseMapper INSTANCE = Mappers.getMapper(TradeLogResponseMapper.class);

    @Mapping(source = "userId.id", target = "userId")
    TradeLogResponseDTO toDto(TradeLog moneyLog);

    default User map(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
