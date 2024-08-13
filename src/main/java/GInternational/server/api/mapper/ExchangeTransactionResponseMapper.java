package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.ExchangeTransactionResponseDTO;
import GInternational.server.api.entity.ExchangeTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExchangeTransactionResponseMapper extends GenericMapper<ExchangeTransactionResponseDTO, ExchangeTransaction> {
    ExchangeTransactionResponseMapper INSTANCE = Mappers.getMapper(ExchangeTransactionResponseMapper.class);
}
