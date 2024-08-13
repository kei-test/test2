package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonCommunityReqDTO;
import GInternational.server.api.entity.AmazonCommunity;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonCommunityReqMapper extends GenericMapper<AmazonCommunityReqDTO, AmazonCommunity> {
    AmazonCommunityReqMapper INSTANCE = Mappers.getMapper(AmazonCommunityReqMapper.class);
}
