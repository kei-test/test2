package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.ExchangeTransactionAdminDTO;
import GInternational.server.api.entity.ExchangeTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExchangeTransactionAdminResponseMapper extends GenericMapper<ExchangeTransactionAdminDTO, ExchangeTransaction> {
    ExchangeTransactionAdminResponseMapper INSTANCE = Mappers.getMapper(ExchangeTransactionAdminResponseMapper.class);
}
