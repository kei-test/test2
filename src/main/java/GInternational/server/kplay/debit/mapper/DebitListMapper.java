package GInternational.server.kplay.debit.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.kplay.debit.dto.DebitRequestDTO;
import GInternational.server.kplay.debit.entity.Debit;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DebitListMapper extends GenericMapper<DebitRequestDTO, Debit> {
    DebitListMapper INSTANCE = Mappers.getMapper(DebitListMapper.class);
}