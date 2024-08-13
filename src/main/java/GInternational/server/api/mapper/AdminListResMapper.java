package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.AdminListResDTO;
import GInternational.server.api.entity.AdminLoginHistory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AdminListResMapper extends GenericMapper<AdminListResDTO, AdminLoginHistory> {

    AdminListResMapper INSTANCE = Mappers.getMapper(AdminListResMapper.class);
}
