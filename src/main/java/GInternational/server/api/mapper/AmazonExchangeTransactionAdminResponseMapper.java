package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonExchangeTransactionAdminDTO;
import GInternational.server.api.entity.AmazonExchangeTransaction;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonExchangeTransactionAdminResponseMapper extends GenericMapper<AmazonExchangeTransactionAdminDTO, AmazonExchangeTransaction> {
    AmazonExchangeTransactionAdminResponseMapper INSTANCE = Mappers.getMapper(AmazonExchangeTransactionAdminResponseMapper.class);
}
