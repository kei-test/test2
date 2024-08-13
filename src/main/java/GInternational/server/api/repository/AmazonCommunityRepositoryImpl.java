package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonCommunity;
import GInternational.server.api.entity.QAmazonCategory;
import GInternational.server.api.entity.QAmazonCommunity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class AmazonCommunityRepositoryImpl implements AmazonCommunityRepositoryCustom {



    private final JPAQueryFactory queryFactory;



    //카테고리별 게시물 조회 //유저 , 파트너
    @Override
    public List<AmazonCommunity> findByAmazonCategoryIdAndCreatedAtBetween(Long amazonCategoryId, LocalDateTime startDate, LocalDateTime endDate) {
        QAmazonCommunity amazonCommunity = QAmazonCommunity.amazonCommunity;
        QAmazonCategory amazonCategory = QAmazonCategory.amazonCategory;

        List<AmazonCommunity> results = queryFactory
                .selectFrom(amazonCommunity)
                .innerJoin(amazonCommunity.amazonCategory, amazonCategory)
                .where(amazonCategory.id.eq(amazonCategoryId),
                        amazonCommunity.createdAt.between(startDate, endDate))
                .orderBy(amazonCommunity.id.desc())
                .fetch();

        return results;
    }

//    @Override
//    public List<Community> findParentCommunityAndChildCommunity(Community community, LocalDateTime start, LocalDateTime endDate) {
//        return queryFactory.selectFrom(QCommunity.community)
//                .leftJoin(QCommunity.community.parent)
//                .fetchJoin()
//                .where(QCommunity.community.id.eq(community.getId()))
//                .orderBy(QCommunity.community.parent.id.asc().nullsFirst(), QCommunity.community.createdAt.asc())
//                .fetch();
//    }
}
