package GInternational.server.amzn.repo;


import GInternational.server.amzn.dto.excclc.AdminExcclcCalculateDTO;

import GInternational.server.amzn.dto.excclc.AmznExcclcDTO;
import GInternational.server.amzn.dto.excclc.AmznPartnerCountDTO;
import GInternational.server.amzn.dto.indi.indi_prj.AmznExchangeDTO;
import GInternational.server.amzn.dto.indi.indi_prj.AmznRechargeDTO;
import GInternational.server.amzn.dto.indi.indi_prj.AmznRollingDTO;
import GInternational.server.api.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


import static GInternational.server.api.entity.QAmazonRollingTransaction.*;
import static GInternational.server.api.entity.QExchangeTransaction.*;
import static GInternational.server.api.entity.QRechargeTransaction.*;
import static GInternational.server.api.entity.QUser.*;
import static GInternational.server.api.entity.QWallet.*;

@Repository
@RequiredArgsConstructor
public class AmznExcclcImpl {


    private final JPAQueryFactory queryFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AdminExcclcCalculateDTO adminExcclcCalculate(LocalDate startDate,LocalDate endDate) {
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);

        //wallet
        AdminExcclcCalculateDTO response1 = queryFactory.select(Projections.constructor(AdminExcclcCalculateDTO.class,
                        wallet.todayDeposit.sum(),
                        wallet.todayWithdraw.sum(),
                        wallet.todayDeposit.sum().subtract(wallet.todayWithdraw.sum()),
                        wallet.sportsBalance.sum()))
                .from(wallet)
                .leftJoin(user).on(user.id.eq(wallet.user.id))
                .where(user.isAmazonUser.isTrue().and(user.partnerType.isNull()))
                .fetchOne();


        //isAmazonUser count
        AdminExcclcCalculateDTO response2 = queryFactory.select(Projections.constructor(AdminExcclcCalculateDTO.class,
                        user.count()))
                .from(user)
                .where(user.createdAt.between(startOfDay,endOfDay)
                        .and(user.isAmazonUser.isTrue()))
                .fetchOne();

        //recharge Amount
        AmznRechargeDTO response3 = queryFactory.select(Projections.constructor(AmznRechargeDTO.class,
                        rechargeTransaction.rechargeAmount.sum()))
                .from(rechargeTransaction)
                .leftJoin(user).on(user.id.eq(rechargeTransaction.user.id))
                .where(user.createdAt.between(startOfDay,endOfDay)
                        .and(user.isAmazonUser.isTrue()))
                .fetchOne();

        //exchange Amount
        AmznExchangeDTO response4 = queryFactory.select(Projections.constructor(AmznExchangeDTO.class,
                        exchangeTransaction.exchangeAmount.sum()))
                .from(exchangeTransaction)
                .leftJoin(user).on(user.id.eq(exchangeTransaction.user.id))
                .where(user.createdAt.between(startOfDay,endOfDay)
                        .and(user.isAmazonUser.isTrue()))
                .fetchOne();

        response1.setBetweenUserCount(response2.getBetweenUserCount());
        response1.setBetweenUserTotalDeposit(response3.getRechargeAmount());
        response1.setBetweenUserTotalExchange(response4.getExchangeAmount());

        return response1;
    }


    // 일주일 단위로 통합정산 데이터를 초기화한다
    // 정산금액과 실수령 : 파트너에게 지급된 롤링(카지노,슬롯)금액 총액
    // 삭제 파라미터 : 이월금액
    public List<AmznExcclcDTO> getPartnerExcclc(String username) {
        List<AmznExcclcDTO> results = queryFactory.select(Projections.constructor(AmznExcclcDTO.class,
                        user.id,
                        user.partnerType,
                        user.username,
                        user.amazonCode,
                        user.casinoRolling,
                        user.slotRolling,
                        user.referredBy))
                .from(user)
                .where(user.id.eq(user.id)
                        .and(usernameEq(username))
                        .and(user.partnerType.isNotNull()))
                .orderBy(user.createdAt.asc())
                .fetch();
        return results;
    }




    public List<AmznRollingDTO> getPartnerRollingAmount(LocalDate startDate,LocalDate endDate,String username) {
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);

        List<AmznRollingDTO> rollingResponse = queryFactory.select(Projections.constructor(AmznRollingDTO.class,
                        user.username,
                        amazonRollingTransaction.rollingAmount.sum()))
                .from(user)
                .leftJoin(amazonRollingTransaction).on(amazonRollingTransaction.user.id.eq(user.id))
                .where(amazonRollingTransaction.processedAt.between(startOfDay,endOfDay)
                        .and(usernameEq(username))
                        .and(amazonRollingTransaction.user.id.eq(user.id)))
                .groupBy(amazonRollingTransaction.user.id)
                .fetch();

        return rollingResponse;
    }



    private BooleanBuilder usernameEq(String username) {
        return nullSafeBooleanBuilder(() -> user.username.eq(username));
    }

    private BooleanBuilder nullSafeBooleanBuilder(Supplier<BooleanExpression> supplier) {
        try {
            return new BooleanBuilder(supplier.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }

    }
}

















