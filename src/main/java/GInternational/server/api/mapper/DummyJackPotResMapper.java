package GInternational.server.api.mapper;

import GInternational.server.api.dto.DummyJackPotResDTO;
import GInternational.server.api.entity.DummyJackPot;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DummyJackPotResMapper extends GenericMapper<DummyJackPotResDTO, DummyJackPot> {
    DummyJackPotResMapper INSTANCE = Mappers.getMapper(DummyJackPotResMapper.class);
}
