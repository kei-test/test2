package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonCommunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmazonCommunityRepository extends JpaRepository<AmazonCommunity,Long>, AmazonCommunityRepositoryCustom {
    List<AmazonCommunity> findAllByParentIsNull(); // 최상위 문의글 조회
    List<AmazonCommunity> findAllByParentId(Long parentId);
}
