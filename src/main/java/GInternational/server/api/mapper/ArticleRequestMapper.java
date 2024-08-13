package GInternational.server.api.mapper;

import GInternational.server.api.dto.ArticlesRequestDTO;
import GInternational.server.api.entity.Articles;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ArticleRequestMapper extends GenericMapper<ArticlesRequestDTO, Articles> {
    ArticleRequestMapper INSTANCE = Mappers.getMapper(ArticleRequestMapper.class);
}
