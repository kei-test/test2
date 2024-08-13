package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonMoneyRequestDTO;
import GInternational.server.api.entity.AmazonMoney;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonMoneyResponseMapper extends GenericMapper<AmazonMoneyRequestDTO, AmazonMoney> {
    AmazonMoneyResponseMapper INSTANCE = Mappers.getMapper(AmazonMoneyResponseMapper.class);
}
