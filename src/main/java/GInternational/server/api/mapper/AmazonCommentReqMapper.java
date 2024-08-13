package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonCommentReqDTO;
import GInternational.server.api.entity.AmazonComment;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonCommentReqMapper extends GenericMapper<AmazonCommentReqDTO, AmazonComment> {
    AmazonCommentReqMapper INSTANCE = Mappers.getMapper(AmazonCommentReqMapper.class);
}
