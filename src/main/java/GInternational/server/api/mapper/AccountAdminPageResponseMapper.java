package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.AccountAdminPageDTO;
import GInternational.server.api.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountAdminPageResponseMapper extends GenericMapper<AccountAdminPageDTO, Account> {
    AccountAdminPageResponseMapper INSTANCE = Mappers.getMapper(AccountAdminPageResponseMapper.class);
}
