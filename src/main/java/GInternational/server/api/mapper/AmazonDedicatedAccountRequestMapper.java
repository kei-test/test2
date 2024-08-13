package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonDedicatedAccountRequestDTO;
import GInternational.server.api.entity.AmazonDedicatedAccount;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonDedicatedAccountRequestMapper extends GenericMapper<AmazonDedicatedAccountRequestDTO, AmazonDedicatedAccount> {
    AmazonDedicatedAccountRequestMapper INSTANCE = Mappers.getMapper(AmazonDedicatedAccountRequestMapper.class);
}
