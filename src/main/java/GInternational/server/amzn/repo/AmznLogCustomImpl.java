package GInternational.server.amzn.repo;

import GInternational.server.amzn.dto.log.AmznLogDTO;
import com.querydsl.core.BooleanBuilder;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Supplier;

import static GInternational.server.api.entity.QAmazonLoginHistory.amazonLoginHistory;
import static GInternational.server.api.entity.QUser.user;


@Repository
@RequiredArgsConstructor
public class AmznLogCustomImpl {

    private final JPAQueryFactory queryFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public Page<AmznLogDTO> searchByAmazonLoginHistory(String username, String nickname, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);


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
                .where(amazonLoginHistory.attemptDate.between(startOfDay,endOfDay).and(usernameEq(username)),nicknameEq(nickname))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(amazonLoginHistory.attemptDate.desc())
                .fetch();

        long totalElements = queryFactory.select(amazonLoginHistory.count())
                .from(amazonLoginHistory)
                .where(amazonLoginHistory.attemptDate.between(startOfDay,endOfDay),usernameEq(username),nicknameEq(nickname))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalElements);
    }


    private BooleanBuilder usernameEq(String username) {
        return nullSafeBooleanBuilder(() -> amazonLoginHistory.attemptUsername.eq(username));
    }
    private BooleanBuilder nicknameEq(String nickname) {
        return nullSafeBooleanBuilder(() -> amazonLoginHistory.attemptNickname.eq(nickname));
    }
    private BooleanBuilder nullSafeBooleanBuilder(Supplier<BooleanExpression> supplier) {
        try {
            return new BooleanBuilder(supplier.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }
}
