package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.ExchangeSettlementAdminDTO;
import GInternational.server.api.entity.ExchangeTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExchangeSettlementAdminMapper extends GenericMapper<ExchangeSettlementAdminDTO, ExchangeTransaction> {
    ExchangeSettlementAdminMapper INSTANCE = Mappers.getMapper(ExchangeSettlementAdminMapper.class);
}
