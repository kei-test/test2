package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.AccountResponseDTO;
import GInternational.server.api.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountAdminResponseMapper extends GenericMapper<AccountResponseDTO, Account> {
    RechargeTransactionAdminResponseMapper INSTANCE = Mappers.getMapper(RechargeTransactionAdminResponseMapper.class);
}
