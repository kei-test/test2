package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.RechargeTransactionAdminDTO;
import GInternational.server.api.entity.RechargeTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RechargeTransactionAdminResponseMapper extends GenericMapper<RechargeTransactionAdminDTO, RechargeTransaction> {
    RechargeTransactionAdminResponseMapper INSTANCE = Mappers.getMapper(RechargeTransactionAdminResponseMapper.class);
}
