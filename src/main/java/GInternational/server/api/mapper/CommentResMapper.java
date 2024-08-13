package GInternational.server.api.mapper;

import GInternational.server.api.dto.CommentResDTO;
import GInternational.server.api.entity.Comment;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CommentResMapper extends GenericMapper<CommentResDTO, Comment> {
    CommentResMapper INSTANCE = Mappers.getMapper(CommentResMapper.class);
}
