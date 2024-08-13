package GInternational.server.api.mapper;

import GInternational.server.api.dto.CommentReqDTO;
import GInternational.server.api.entity.Comment;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CommentReqMapper extends GenericMapper<CommentReqDTO, Comment> {
    CommentReqMapper INSTANCE = Mappers.getMapper(CommentReqMapper.class);
}
