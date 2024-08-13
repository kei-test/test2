package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.RechargeProcessedRequestDTO;
import GInternational.server.api.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RechargeProcessedRequestMapper extends GenericMapper<RechargeProcessedRequestDTO, Wallet> {
    RechargeProcessedRequestMapper INSTANCE = Mappers.getMapper(RechargeProcessedRequestMapper.class);
}
