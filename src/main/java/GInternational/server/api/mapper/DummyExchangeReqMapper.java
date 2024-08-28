package GInternational.server.api.mapper;

import GInternational.server.api.dto.DummyExchangeReqDTO;
import GInternational.server.api.entity.DummyExchange;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DummyExchangeReqMapper extends GenericMapper<DummyExchangeReqDTO, DummyExchange> {
    DummyExchangeReqMapper INSTANCE = Mappers.getMapper(DummyExchangeReqMapper.class);
}
