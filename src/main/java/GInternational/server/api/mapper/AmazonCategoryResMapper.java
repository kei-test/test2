package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonCategoryResDTO;
import GInternational.server.api.entity.AmazonCategory;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonCategoryResMapper extends GenericMapper<AmazonCategoryResDTO, AmazonCategory> {
    AmazonCategoryResMapper INSTANCE = Mappers.getMapper(AmazonCategoryResMapper.class);
}
