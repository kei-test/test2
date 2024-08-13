package GInternational.server.api.mapper;

import GInternational.server.api.dto.ArticlesListDTO;
import GInternational.server.api.entity.Articles;
import GInternational.server.common.generic.GenericMapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ArticleListResponseMapper extends GenericMapper<ArticlesListDTO, Articles> {
    ArticleListResponseMapper INSTANCE = Mappers.getMapper(ArticleListResponseMapper.class);

}
