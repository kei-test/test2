package GInternational.server.api.mapper;

import GInternational.server.api.dto.DummyJackPotReqDTO;
import GInternational.server.api.entity.DummyJackPot;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DummyJackPotReqMapper extends GenericMapper<DummyJackPotReqDTO, DummyJackPot> {
    DummyJackPotReqMapper INSTANCE = Mappers.getMapper(DummyJackPotReqMapper.class);
}
