package GInternational.server.api.mapper;

import GInternational.server.api.dto.AutoRechargeBankAccountDTO;
import GInternational.server.api.entity.AutoRechargeBankAccount;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AutoRechargeBankAccountMapper extends GenericMapper<AutoRechargeBankAccountDTO, AutoRechargeBankAccount> {
    AutoRechargeBankAccountMapper INSTANCE = Mappers.getMapper(AutoRechargeBankAccountMapper.class);
}