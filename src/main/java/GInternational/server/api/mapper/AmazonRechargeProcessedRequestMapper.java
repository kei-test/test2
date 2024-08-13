package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonRechargeProcessedRequestDTO;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonRechargeProcessedRequestMapper extends GenericMapper<AmazonRechargeProcessedRequestDTO, User> {
    AmazonRechargeProcessedRequestMapper INSTANCE = Mappers.getMapper(AmazonRechargeProcessedRequestMapper.class);
}
