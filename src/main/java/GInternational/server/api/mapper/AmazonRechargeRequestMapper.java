package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonRechargeRequestDTO;

import GInternational.server.common.generic.GenericMapper;

import GInternational.server.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonRechargeRequestMapper extends GenericMapper<AmazonRechargeRequestDTO, User> {
    AmazonRechargeRequestMapper INSTANCE = Mappers.getMapper(AmazonRechargeRequestMapper.class);
}
