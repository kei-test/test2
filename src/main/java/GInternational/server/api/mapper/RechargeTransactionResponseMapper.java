package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.RechargeTransactionResDTO;
import GInternational.server.api.entity.RechargeTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RechargeTransactionResponseMapper extends GenericMapper<RechargeTransactionResDTO, RechargeTransaction> {
    RechargeTransactionResponseMapper INSTANCE = Mappers.getMapper(RechargeTransactionResponseMapper.class);
}
