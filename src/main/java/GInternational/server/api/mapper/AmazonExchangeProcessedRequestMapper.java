package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonExchangeProcessedRequestDTO;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonExchangeProcessedRequestMapper extends GenericMapper<AmazonExchangeProcessedRequestDTO, User> {
    AmazonExchangeProcessedRequestMapper INSTANCE = Mappers.getMapper(AmazonExchangeProcessedRequestMapper.class);
}
