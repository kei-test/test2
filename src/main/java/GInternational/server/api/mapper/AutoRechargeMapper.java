package GInternational.server.api.mapper;

import GInternational.server.api.dto.AutoRechargeDTO;
import GInternational.server.api.entity.AutoRecharge;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AutoRechargeMapper {
    AutoRechargeMapper INSTANCE = Mappers.getMapper(AutoRechargeMapper.class);

    AutoRechargeDTO toDto(AutoRecharge autoRecharge);
}