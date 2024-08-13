package GInternational.server.api.mapper;

import GInternational.server.api.dto.CategoryResponseDTO;
import GInternational.server.api.entity.Category;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryResponseMapper extends GenericMapper<CategoryResponseDTO, Category> {
    CategoryRequestMapper INSTANCE = Mappers.getMapper(CategoryRequestMapper.class);
}
