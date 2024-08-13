package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonComment;
import GInternational.server.api.entity.AmazonCommunity;
import GInternational.server.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AmazonCommentRepository extends JpaRepository<AmazonComment,Long>, AmazonCommentRepositoryCustom {

    Optional<AmazonComment> findByIdAndAmazonCommunity(Long amazonCommentId, AmazonCommunity amazonCommunity);
    List<AmazonComment> findByWriter(User user);

}
