package GInternational.server.api.mapper;

import GInternational.server.api.dto.DummyExchangeResDTO;
import GInternational.server.api.entity.DummyExchange;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DummyExchangeResMapper extends GenericMapper<DummyExchangeResDTO, DummyExchange> {
    DummyExchangeResMapper INSTANCE = Mappers.getMapper(DummyExchangeResMapper.class);
}
