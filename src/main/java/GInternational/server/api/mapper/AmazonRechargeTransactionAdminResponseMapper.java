package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonRechargeTransactionAdminDTO;
import GInternational.server.api.entity.AmazonRechargeTransaction;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonRechargeTransactionAdminResponseMapper extends GenericMapper<AmazonRechargeTransactionAdminDTO, AmazonRechargeTransaction> {
    AmazonRechargeTransactionAdminResponseMapper INSTANCE = Mappers.getMapper(AmazonRechargeTransactionAdminResponseMapper.class);
}
