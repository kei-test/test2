package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonExchangeRequestDTO;
import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonExchangeRequestMapper extends GenericMapper<AmazonExchangeRequestDTO, User> {
    AmazonExchangeRequestMapper INSTANCE = Mappers.getMapper(AmazonExchangeRequestMapper.class);
}
