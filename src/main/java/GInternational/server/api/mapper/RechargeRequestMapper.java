package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.RechargeRequestDTO;

import GInternational.server.api.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RechargeRequestMapper extends GenericMapper<RechargeRequestDTO, Wallet> {
    RechargeRequestMapper INSTANCE = Mappers.getMapper(RechargeRequestMapper.class);
}
