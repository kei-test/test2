package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;

import GInternational.server.api.dto.PointTransactionResponseDTO;
import GInternational.server.api.entity.PointTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PointTransactionResponseMapper extends GenericMapper<PointTransactionResponseDTO, PointTransaction> {
    PointTransactionResponseMapper INSTANCE = Mappers.getMapper(PointTransactionResponseMapper.class);
}
