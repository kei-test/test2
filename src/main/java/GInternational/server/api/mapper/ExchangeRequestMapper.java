package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.ExchangeRequestDTO;
import GInternational.server.api.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExchangeRequestMapper extends GenericMapper<ExchangeRequestDTO, Wallet> {
    ExchangeRequestMapper INSTANCE = Mappers.getMapper(ExchangeRequestMapper.class);
}
