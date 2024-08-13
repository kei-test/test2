package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.ExchangeProcessedRequestDTO;
import GInternational.server.api.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExchangeProcessedRequestMapper extends GenericMapper<ExchangeProcessedRequestDTO, Wallet> {
    ExchangeProcessedRequestMapper INSTANCE = Mappers.getMapper(ExchangeProcessedRequestMapper.class);
}
