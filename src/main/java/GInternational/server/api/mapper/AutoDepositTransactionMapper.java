package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.AutoDepositTransactionAdminResDTO;
import GInternational.server.api.entity.AutoDepositTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AutoDepositTransactionMapper extends GenericMapper<AutoDepositTransactionAdminResDTO, AutoDepositTransaction> {
    AutoDepositTransactionMapper INSTANCE = Mappers.getMapper(AutoDepositTransactionMapper.class);
}
