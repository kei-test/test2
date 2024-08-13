package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.CasinoTransactionResponseDTO;
import GInternational.server.api.entity.CasinoTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CasinoTransactionResponseMapper extends GenericMapper<CasinoTransactionResponseDTO, CasinoTransaction> {
    CasinoTransactionResponseMapper INSTANCE = Mappers.getMapper(CasinoTransactionResponseMapper.class);
}
