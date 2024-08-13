package GInternational.server.api.repository;

import GInternational.server.api.dto.AmazonCommentResDTO;
import GInternational.server.api.entity.AmazonComment;

import java.util.List;
import java.util.Optional;

public interface AmazonCommentRepositoryCustom {

    List<AmazonCommentResDTO> findByAmazonCommunityParentAndChild(Long id);

    Optional<AmazonComment> findAmazonCommentByIdWithParent(Long id);

}
