package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.AmazonUserRequestDTO;

import GInternational.server.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonUserRequestMapper extends GenericMapper<AmazonUserRequestDTO, User> {
    AmazonUserRequestMapper INSTANCE = Mappers.getMapper(AmazonUserRequestMapper.class);
}
