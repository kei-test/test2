package GInternational.server.partner.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.partner.dto.PartnerUserRequestDTO;
import GInternational.server.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PartnerUserRequestMapper extends GenericMapper<PartnerUserRequestDTO, User> {
    PartnerUserRequestMapper INSTANCE = Mappers.getMapper(PartnerUserRequestMapper.class);
}
