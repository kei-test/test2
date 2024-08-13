package GInternational.server.api.repository;

import GInternational.server.api.entity.CouponTransaction;
import GInternational.server.api.entity.QCouponTransaction;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.util.List;

import static GInternational.server.api.entity.QUser.user;


@RequiredArgsConstructor
public class CouponTransactionRepositoryImpl implements CouponTransactionRepositoryCustom {


    private final JPAQueryFactory queryFactory;


    @Override
    public List<CouponTransaction> findByUserIdAndDate(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return queryFactory
                .selectFrom(QCouponTransaction.couponTransaction)
                .where(QCouponTransaction.couponTransaction.user.id.eq(userId)
                        .and(QCouponTransaction.couponTransaction.createdAt.between(startDateTime, endDateTime)))
                .orderBy(QCouponTransaction.couponTransaction.createdAt.desc())
                .fetch();
    }

    @Override
    public Long countByUserId(Long userId) {
        return (long) queryFactory.selectFrom(QCouponTransaction.couponTransaction)
                .innerJoin(QCouponTransaction.couponTransaction.user, user)
                .where(user.id.eq(userId))
                .fetch().size();
    }
}
