package GInternational.server.api.repository;

import GInternational.server.api.entity.ExchangeTransaction;
import GInternational.server.api.vo.TransactionEnum;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static GInternational.server.api.entity.QExchangeTransaction.*;
import static GInternational.server.api.entity.QUser.user;

@RequiredArgsConstructor
public class ExchangeRepositoryImpl implements ExchangeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ExchangeTransaction> findByUserIdAndExchangeTransaction(Long userId, Pageable pageable) {
        List<ExchangeTransaction> results = queryFactory
                .selectFrom(exchangeTransaction)
                .innerJoin(exchangeTransaction.user, user)
                .where(user.id.eq(userId))
                .orderBy(exchangeTransaction.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results,pageable,results.size());
    }

    @Override
    public Long countByUserId(Long userId) {
        return (long) queryFactory.selectFrom(exchangeTransaction)
                .innerJoin(exchangeTransaction.user, user)
                .where(user.id.eq(userId))
                .fetch().size();
    }

    @Override
    public long sumExchangeAmountByProcessedAt(Long userId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1); // Adding one day to include the end date

        return queryFactory
                .select(Expressions.numberTemplate(Integer.class, "coalesce(sum({0}), 0)", exchangeTransaction.exchangeAmount))
                .from(exchangeTransaction)
                .where(exchangeTransaction.user.id.eq(userId)
                        .and(exchangeTransaction.processedAt.between(startDateTime,endDateTime))
                        .and(exchangeTransaction.status.eq(TransactionEnum.APPROVAL)))
                .fetchOne();
    }

    @Override
    public List<ExchangeTransaction> findByExchangeTransaction(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);

        return queryFactory
                .selectFrom(exchangeTransaction)
                .where(exchangeTransaction.processedAt.between(startDateTime, endDateTime)
                        .and(exchangeTransaction.status.eq(TransactionEnum.APPROVAL)))
                .fetch();
    }
}
