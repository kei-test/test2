package GInternational.server.api.mapper;

import GInternational.server.api.dto.ArticlesResponseDTO;
import GInternational.server.api.entity.Articles;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ArticleResponseMapper extends GenericMapper<ArticlesResponseDTO, Articles> {
    ArticleResponseMapper INSTANCE = Mappers.getMapper(ArticleResponseMapper.class);

}
