package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonCommunityResDTO;
import GInternational.server.api.entity.AmazonCommunity;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonCommunityResMapper extends GenericMapper<AmazonCommunityResDTO, AmazonCommunity> {
    AmazonCommunityResMapper INSTANCE = Mappers.getMapper(AmazonCommunityResMapper.class);
}