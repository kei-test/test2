package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonDedicatedAccountResponseDTO;
import GInternational.server.api.entity.AmazonDedicatedAccount;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonDedicatedAccountResponseMapper extends GenericMapper<AmazonDedicatedAccountResponseDTO, AmazonDedicatedAccount> {
    AmazonDedicatedAccountResponseMapper INSTANCE = Mappers.getMapper(AmazonDedicatedAccountResponseMapper.class);
}
