package GInternational.server.api.mapper;

import GInternational.server.api.dto.CategoryRequestDTO;
import GInternational.server.api.entity.Category;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryRequestMapper extends GenericMapper<CategoryRequestDTO,Category> {

    CategoryRequestMapper INSTANCE = Mappers.getMapper(CategoryRequestMapper.class);
}
