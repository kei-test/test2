package GInternational.server.api.repository;

import GInternational.server.api.entity.AutoTransaction;
import GInternational.server.api.entity.RechargeTransaction;


import GInternational.server.api.vo.TransactionEnum;
import com.querydsl.core.types.Expression;
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
import java.util.Date;
import java.util.List;


import static GInternational.server.api.entity.QAutoTransaction.autoTransaction;
import static GInternational.server.api.entity.QRechargeTransaction.rechargeTransaction;
import static GInternational.server.api.entity.QUser.*;

@RequiredArgsConstructor
public class RechargeTransactionRepositoryImpl implements RechargeTransactionRepositoryCustom {


    private final JPAQueryFactory queryFactory;


    @Override
    public Page<RechargeTransaction> findByUserIdAndTransaction(Long userId, Pageable pageable) {
        List<RechargeTransaction> results = queryFactory
                .selectFrom(rechargeTransaction)
                .innerJoin(rechargeTransaction.user, user)
                .where(user.id.eq(userId))
                .orderBy(rechargeTransaction.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results,pageable,results.size());
    }

    @Override
    public Long countByUserId(Long userId) {
        return (long) queryFactory.selectFrom(rechargeTransaction)
                .innerJoin(rechargeTransaction.user, user)
                .where(user.id.eq(userId))
                .fetch().size();
    }

    @Override
    public long sumRechargeAmountByProcessedAt(Long userId,LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);

        return queryFactory
                .select(Expressions.numberTemplate(Integer.class, "coalesce(sum({0}), 0)", rechargeTransaction.rechargeAmount))
                .from(rechargeTransaction)
                .where(rechargeTransaction.user.id.eq(userId)
                                .and(rechargeTransaction.processedAt.between(startDateTime,endDateTime))
                                .and(rechargeTransaction.status.eq(TransactionEnum.APPROVAL)))
                .fetchOne();
    }

    //30분이 지난 신청건 조회
    @Override
    public List<RechargeTransaction> searchByRechargeTransactionCondition() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        return queryFactory
                .selectFrom(rechargeTransaction)
                .where(rechargeTransaction.status.in(TransactionEnum.WAITING, TransactionEnum.UNREAD)
                        .and(rechargeTransaction.createdAt.before(thirtyMinutesAgo)))
                .fetch();
    }
}
