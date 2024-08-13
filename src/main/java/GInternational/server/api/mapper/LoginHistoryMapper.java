package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.LoginHistoryDTO;
import GInternational.server.api.entity.LoginHistory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LoginHistoryMapper extends GenericMapper<LoginHistoryDTO, LoginHistory> {

    LoginHistoryMapper INSTANCE = Mappers.getMapper(LoginHistoryMapper.class);
}
