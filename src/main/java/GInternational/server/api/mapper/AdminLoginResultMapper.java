package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.AdminLoginResultDTO;
import GInternational.server.api.entity.AdminLoginHistory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AdminLoginResultMapper extends GenericMapper<AdminLoginResultDTO, AdminLoginHistory> {

    AdminLoginResultMapper INSTANCE = Mappers.getMapper(AdminLoginResultMapper.class);
}