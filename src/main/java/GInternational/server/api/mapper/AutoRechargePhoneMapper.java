package GInternational.server.api.mapper;

import GInternational.server.api.dto.AutoRechargePhoneDTO;
import GInternational.server.api.entity.AutoRechargePhone;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AutoRechargePhoneMapper extends GenericMapper<AutoRechargePhoneDTO, AutoRechargePhone> {
    AutoRechargePhoneMapper INSTANCE = Mappers.getMapper(AutoRechargePhoneMapper.class);
}