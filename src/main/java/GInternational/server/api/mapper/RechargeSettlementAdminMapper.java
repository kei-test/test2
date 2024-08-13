package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.RechargeSettlementAdminDTO;
import GInternational.server.api.entity.RechargeTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RechargeSettlementAdminMapper extends GenericMapper<RechargeSettlementAdminDTO, RechargeTransaction> {
    RechargeSettlementAdminMapper INSTANCE = Mappers.getMapper(RechargeSettlementAdminMapper.class);
}
