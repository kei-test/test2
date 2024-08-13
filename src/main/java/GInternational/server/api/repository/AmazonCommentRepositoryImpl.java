package GInternational.server.api.repository;

import GInternational.server.api.dto.AmazonCommentResDTO;
import GInternational.server.api.entity.AmazonComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

import static GInternational.server.api.entity.QAmazonComment.amazonComment;
import static GInternational.server.api.entity.QAmazonCommunity.amazonCommunity;


@RequiredArgsConstructor
public class AmazonCommentRepositoryImpl implements AmazonCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AmazonCommentResDTO> findByAmazonCommunityParentAndChild(Long id) {
        List<AmazonComment> comments = queryFactory.selectFrom(amazonComment)
                .leftJoin(amazonComment.parent).fetchJoin()
                .where(amazonComment.id.eq(id).or(amazonComment.parent.id.eq(id)))
                .orderBy(amazonComment.parent.id.desc().nullsFirst(), amazonComment.createdAt.desc())
                .fetch();


        List<AmazonCommentResDTO> communityResponse = new ArrayList<>();
        Map<Long, AmazonCommentResDTO> communityDtoHashMap = new HashMap<>();

        for (AmazonComment c : comments) {
            AmazonCommentResDTO communityResDTO = new AmazonCommentResDTO(c);
            communityDtoHashMap.put(communityResDTO.getId(), communityResDTO);

            AmazonComment parent = c.getParent();
            if (parent != null) {
                AmazonCommentResDTO parentDto = communityDtoHashMap.get(parent.getId());
                if (parentDto != null) {
                    parentDto.getChildren().add(communityResDTO);
                }
            } else {
                communityResponse.add(communityResDTO);
            }
        }

        return communityResponse;
    }



    @Override
    public Optional<AmazonComment> findAmazonCommentByIdWithParent(Long id) {
        AmazonComment selectedComment = queryFactory.selectFrom(amazonComment)
                .leftJoin(amazonComment.parent).fetchJoin()
                .join(amazonComment.amazonCommunity, amazonCommunity).fetchJoin()
                .where(amazonComment.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(selectedComment);
    }
}
