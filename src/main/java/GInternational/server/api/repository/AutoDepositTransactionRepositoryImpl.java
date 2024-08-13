package GInternational.server.api.repository;

import GInternational.server.api.entity.AutoDepositTransaction;
import GInternational.server.api.vo.TransactionEnum;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static GInternational.server.api.entity.QAutoDepositTransaction.*;

@RequiredArgsConstructor
public class AutoDepositTransactionRepositoryImpl implements AutoDepositTransactionRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    //30분이 지난 신청건 조회
    @Override
    public List<AutoDepositTransaction> searchByAutoDepositTransactionCondition() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        return queryFactory.selectFrom(autoDepositTransaction)
                .where(autoDepositTransaction.status.eq(TransactionEnum.WAITING)
                        .and(autoDepositTransaction.createdAt.before(thirtyMinutesAgo)))
                .fetch();
    }


    @Override
    public List<AutoDepositTransaction> findByAutoDepositTransaction(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);

        return queryFactory
                .selectFrom(autoDepositTransaction)
                .where(autoDepositTransaction.status.eq(TransactionEnum.APPROVAL)
                        .and(autoDepositTransaction.processedAt.between(startDateTime, endDateTime)))
                .fetch();
    }

    @Override
    public List<AutoDepositTransaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .selectFrom(autoDepositTransaction)
                .where(autoDepositTransaction.createdAt.between(startDate, endDate))
                .fetch();
    }
}
