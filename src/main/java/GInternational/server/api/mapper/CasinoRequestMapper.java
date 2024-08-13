package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.CasinoRequestDTO;
import GInternational.server.api.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CasinoRequestMapper extends GenericMapper<CasinoRequestDTO, Wallet> {
    CasinoRequestMapper INSTANCE = Mappers.getMapper(CasinoRequestMapper.class);
}
