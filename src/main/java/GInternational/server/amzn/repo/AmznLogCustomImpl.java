package GInternational.server.amzn.repo;

import GInternational.server.amzn.dto.log.AmznLogDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static GInternational.server.api.entity.QAmazonLoginHistory.amazonLoginHistory;


@Repository
@RequiredArgsConstructor
public class AmznLogCustomImpl {

    private final JPAQueryFactory queryFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public Page<AmznLogDTO> searchByAmazonLoginHistory(String username, String nickname, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        BooleanExpression predicate = amazonLoginHistory.attemptDate.between(endDate.atStartOfDay(), startDate.atStartOfDay());
        if (username != null) {
            predicate = predicate.and(usernameEq(username));
        }
        if (nickname != null) {
            predicate = predicate.and(nicknameEq(nickname));
        }

        List<AmznLogDTO> results = queryFactory
                .select(Projections.constructor(AmznLogDTO.class,
                        amazonLoginHistory.gubun,
                        amazonLoginHistory.attemptUsername,
                        amazonLoginHistory.attemptNickname,
                        amazonLoginHistory.attemptPassword,
                        amazonLoginHistory.result,
                        amazonLoginHistory.attemptIP,
                        amazonLoginHistory.attemptDate))
                .from(amazonLoginHistory)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(amazonLoginHistory.attemptDate.desc())
                .fetch();

        long totalElements = queryFactory.select(amazonLoginHistory.count())
                .from(amazonLoginHistory)
                .where(predicate)
                .fetchOne();

        return new PageImpl<>(results, pageable, totalElements);
    }


    private BooleanExpression usernameEq(String username) {
        return username != null ? amazonLoginHistory.attemptUsername.eq(username) : Expressions.TRUE.isTrue();
    }
    private BooleanExpression nicknameEq(String nickname) {
        return nickname != null ? amazonLoginHistory.attemptNickname.eq(nickname) : Expressions.TRUE.isTrue();
    }
}
