package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonCommentResDTO;
import GInternational.server.api.entity.AmazonComment;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonCommentResMapper extends GenericMapper<AmazonCommentResDTO, AmazonComment> {
    AmazonCommentResMapper INSTANCE = Mappers.getMapper(AmazonCommentResMapper.class);
}
