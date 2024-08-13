package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonCommunity;

import java.time.LocalDateTime;
import java.util.List;

public interface AmazonCommunityRepositoryCustom {

    List<AmazonCommunity> findByAmazonCategoryIdAndCreatedAtBetween(Long amazonCategoryId, LocalDateTime startDateTime, LocalDateTime endDateTime);

//    List<Community> findParentCommunityAndChildCommunity(Community community,LocalDateTime start,LocalDateTime endDate);
}
