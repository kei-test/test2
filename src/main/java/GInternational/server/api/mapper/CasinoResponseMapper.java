package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.CasinoResponseDTO;
import GInternational.server.api.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CasinoResponseMapper extends GenericMapper<CasinoResponseDTO, Wallet> {
    CasinoResponseMapper INSTANCE = Mappers.getMapper(CasinoResponseMapper.class);
}
