package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.AmazonUserResponseDTO;

import GInternational.server.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonUserResponseMapper extends GenericMapper<AmazonUserResponseDTO, User> {
    AmazonUserResponseMapper INSTANCE = Mappers.getMapper(AmazonUserResponseMapper.class);
}

