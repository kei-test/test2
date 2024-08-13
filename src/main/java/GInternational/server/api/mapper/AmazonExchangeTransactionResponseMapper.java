package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonExchangeTransactionResponseDTO;
import GInternational.server.api.entity.AmazonExchangeTransaction;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonExchangeTransactionResponseMapper extends GenericMapper<AmazonExchangeTransactionResponseDTO, AmazonExchangeTransaction> {
    AmazonExchangeTransactionResponseMapper INSTANCE = Mappers.getMapper(AmazonExchangeTransactionResponseMapper.class);
}
