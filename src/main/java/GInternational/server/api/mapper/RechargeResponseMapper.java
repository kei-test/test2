package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.RechargeResponseDTO;

import GInternational.server.api.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RechargeResponseMapper extends GenericMapper<RechargeResponseDTO, Wallet> {
    RechargeRequestMapper INSTANCE = Mappers.getMapper(RechargeRequestMapper.class);
}
