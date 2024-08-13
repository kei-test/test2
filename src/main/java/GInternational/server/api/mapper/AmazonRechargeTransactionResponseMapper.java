package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonRechargeTransactionResDTO;
import GInternational.server.api.entity.AmazonRechargeTransaction;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonRechargeTransactionResponseMapper extends GenericMapper<AmazonRechargeTransactionResDTO, AmazonRechargeTransaction> {
    AmazonRechargeTransactionResponseMapper INSTANCE = Mappers.getMapper(AmazonRechargeTransactionResponseMapper.class);
}
