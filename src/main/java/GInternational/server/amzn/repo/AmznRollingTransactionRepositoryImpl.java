package GInternational.server.amzn.repo;


import GInternational.server.amzn.dto.indi.business.AmznPartnerRollingInfo;
import GInternational.server.amzn.dto.indi.business.AmznRollingTransactionResDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import static GInternational.server.api.entity.QAmazonRollingTransaction.*;
import static GInternational.server.api.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class AmznRollingTransactionRepositoryImpl {


    private final JPAQueryFactory queryFactory;

    public List<AmznRollingTransactionResDTO> searchByRollingTransactions(Long userId, String category, LocalDate startDate, LocalDate endDate) {
        List<AmznRollingTransactionResDTO> results = queryFactory.select(Projections.constructor(AmznRollingTransactionResDTO.class,
                amazonRollingTransaction.id,
                amazonRollingTransaction.category,
                amazonRollingTransaction.username,
                amazonRollingTransaction.nickname,
                amazonRollingTransaction.betAmount,
                amazonRollingTransaction.rollingAmount,
                amazonRollingTransaction.betTime,
                amazonRollingTransaction.processedAt))
                .from(amazonRollingTransaction)
                .where(amazonRollingTransaction.processedAt.between(startDate.atStartOfDay(),endDate.atStartOfDay())
                                .and(amazonRollingTransaction.user.id.eq(userId))
                        .and(categoryEq(category)))
                .fetch();

        return results;
    }


    public AmznPartnerRollingInfo searchByPartnerRollingInfo(Long id) {
        return queryFactory.select(Projections.constructor(AmznPartnerRollingInfo.class,
                user.casinoRolling,
                user.slotRolling))
                .from(user)
                .where(user.id.eq(id))
                .fetchOne();
    }


    private BooleanBuilder categoryEq(String category) {
        return nullSafeBooleanBuilder(() -> amazonRollingTransaction.category.eq(category));
    }


    private BooleanBuilder nullSafeBooleanBuilder(Supplier<BooleanExpression> supplier) {
        try {
            return new BooleanBuilder(supplier.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }
}
