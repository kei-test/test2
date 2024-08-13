package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonCategoryReqDTO;
import GInternational.server.api.entity.AmazonCategory;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonCategoryReqMapper extends GenericMapper<AmazonCategoryReqDTO, AmazonCategory> {
    AmazonCategoryReqMapper INSTANCE = Mappers.getMapper(AmazonCategoryReqMapper.class);
}
