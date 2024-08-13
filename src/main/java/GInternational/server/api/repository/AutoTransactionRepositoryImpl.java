package GInternational.server.api.repository;

import GInternational.server.api.entity.AutoTransaction;
import GInternational.server.api.vo.TransactionEnum;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

import static GInternational.server.api.entity.QAutoTransaction.autoTransaction;
import static GInternational.server.api.entity.QUser.*;

@RequiredArgsConstructor
public class AutoTransactionRepositoryImpl implements AutoTransactionRepositoryCustom {


    private final JPAQueryFactory queryFactory;


    //30분이 지난 신청건 조회
    @Override
    public List<AutoTransaction> searchByAutoTransactionCondition() {
        Date threeMinutesAgo = new Date(System.currentTimeMillis() - 1800000);

        return queryFactory.selectFrom(autoTransaction)
                .where(autoTransaction.status.eq(TransactionEnum.WAITING)
                        .and(autoTransaction.createdAt.before(threeMinutesAgo)))
                .fetch();
    }

    //유저의 신청건 조회 반환값
    @Override
    public Page<AutoTransaction> findByUserIdAndAutoTransaction(Long userId, Pageable pageable) {
         List<AutoTransaction> results = queryFactory.selectFrom(autoTransaction)
                 .innerJoin(autoTransaction.user, user)
                 .where(user.id.eq(userId))
                 .orderBy(autoTransaction.id.desc())
                 .offset(pageable.getOffset())
                 .limit(pageable.getPageSize())
                 .fetch();

         long totalElements = queryFactory.selectFrom(autoTransaction)
                 .innerJoin(autoTransaction.user,user)
                 .where(user.id.eq(userId))
                 .fetch().size();

         return new PageImpl<>(results, pageable, totalElements);
    }
}
