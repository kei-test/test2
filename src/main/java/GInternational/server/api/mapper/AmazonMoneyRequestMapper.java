package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonMoneyRequestDTO;
import GInternational.server.api.entity.AmazonMoney;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonMoneyRequestMapper extends GenericMapper<AmazonMoneyRequestDTO, AmazonMoney> {
    AmazonMoneyRequestMapper INSTANCE = Mappers.getMapper(AmazonMoneyRequestMapper.class);
}
