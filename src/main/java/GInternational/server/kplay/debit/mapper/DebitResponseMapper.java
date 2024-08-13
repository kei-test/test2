package GInternational.server.kplay.debit.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.kplay.debit.dto.DebitUserResponseDTO;
import GInternational.server.kplay.debit.entity.Debit;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DebitResponseMapper extends GenericMapper<DebitUserResponseDTO, Debit> {
    DebitResponseMapper INSTANCE = Mappers.getMapper(DebitResponseMapper.class);
}