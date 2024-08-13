package GInternational.server.api.repository;


import GInternational.server.api.entity.BetHistory;
import GInternational.server.api.vo.BetFoldCountEnum;
import GInternational.server.api.vo.BetTypeEnum;
import GInternational.server.api.vo.OrderStatusEnum;
import GInternational.server.l_sport.batch.job.dto.order.DetailResponseDTO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

import static GInternational.server.api.entity.QBetHistory.betHistory;
import static GInternational.server.api.entity.QUser.user;


@RequiredArgsConstructor
public class BetHistoryRepositoryImpl implements BetHistoryRepositoryCustom {


    private final JPAQueryFactory queryFactory;


    @Override
    public Page<DetailResponseDTO> searchOrderList(Long userId,
                                                   BetTypeEnum custom,
                                                   String username,
                                                   String nickname,
                                                   BetFoldCountEnum betFoldCount,
                                                   String ip,
                                                   Long id,
                                                   List<Long> betGroupId,
                                                   OrderStatusEnum orderStatus,
                                                   Boolean deleted,
                                                   String orderBy,
                                                   Pageable pageable,
                                                   LocalDate startDate,
                                                   LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(1095);
        } LocalDateTime startDateTime = startDate.atStartOfDay();

        if (endDate == null) {
            endDate = LocalDate.now();
        } LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);


        NumberExpression<Long> betLongExpression = betHistory.bet.castToNum(Long.class);
        OrderSpecifier<?> orderSpecifier;
        if ("1".equals(orderBy)) {
            orderSpecifier = betLongExpression.desc();
        } else if ("2".equals(orderBy)) {
            orderSpecifier = betLongExpression.asc();
        } else {
            orderSpecifier = betHistory.betStartTime.desc();
        }


        BooleanExpression statusCondition = null;
        if (orderStatus == OrderStatusEnum.HIT) {
            statusCondition = betHistory.orderStatus.in(OrderStatusEnum.HIT, OrderStatusEnum.CANCEL, OrderStatusEnum.CANCEL_HIT);
        } else if (orderStatus == OrderStatusEnum.FAIL) {
            statusCondition = betHistory.orderStatus.in(OrderStatusEnum.FAIL);
        } else if (orderStatus == OrderStatusEnum.WAITING) {
            statusCondition = betHistory.orderStatus.in(OrderStatusEnum.WAITING);
        }

        List<Long> distinctBetGroupIds = queryFactory
                .select(betHistory.betGroupId)
                .from(betHistory)
                .join(user).on(betHistory.user.id.eq(user.id))
                .where(betHistory.betStartTime.between(startDateTime, endDateTime)
                        .and(userIdEq(userId))
                        .and(gameCustomEq(custom))
                        .and(usernameEq(username))
                        .and(nicknameEq(nickname))
                        .and(foldCountEq(betFoldCount))
                        .and(ipEq(ip))
                        .and(historyIdEq(id))
                        .and(betGroupIdIn(betGroupId))
                        .and(statusCondition)
                        .and(deletedOrderEq(deleted)))
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        List<DetailResponseDTO> results = queryFactory.select(Projections.constructor(DetailResponseDTO.class,
                        betHistory.user.id,
                        betHistory.betIp,
                        betHistory.betStatus,
                        user.monitoringStatus,
                        betHistory.readStatus,
                        betHistory.readBy,
                        betHistory.readAt,
                        user.username,
                        user.nickname,
                        betHistory.matchId,
                        betHistory.startDate,
                        betHistory.leagueName,
                        betHistory.sportName,
                        betHistory.homeName,
                        betHistory.awayName,
                        betHistory.marketName,
                        betHistory.winRate,
                        betHistory.drawRate,
                        betHistory.loseRate,
                        betHistory.idx,
                        betHistory.betGroupId,
                        betHistory.betTeam,
                        betHistory.bet,
                        betHistory.price,
                        betHistory.settlement,
                        betHistory.betReward,
                        betHistory.matchStatus,
                        betHistory.deleted,
                        betHistory.betType,
                        betHistory.betFoldType,
                        betHistory.orderStatus,
                        betHistory.betStartTime,
                        betHistory.processedAt))
                .from(betHistory)
                .join(user).on(betHistory.user.id.eq(user.id))
                .where(betHistory.betStartTime.between(startDateTime, endDateTime)
                        .and(userIdEq(userId))
                        .and(gameCustomEq(custom))
                        .and(usernameEq(username))
                        .and(nicknameEq(nickname))
                        .and(foldCountEq(betFoldCount))
                        .and(ipEq(ip))
                        .and(historyIdEq(id))
                        .and(betGroupIdIn(betGroupId))
                        .and(betHistory.betGroupId.in(distinctBetGroupIds))
                        .and(statusCondition)
                        .and(deletedOrderEq(deleted)))
                .orderBy(orderSpecifier)
                .fetch();
        return new PageImpl<>(results, pageable, 1);
    }


    @Override
    public long countByOrder(Long userId,BetTypeEnum custom, String username, String nickname, BetFoldCountEnum betFoldCount, String ip, Long id, List<Long> betGroupId, OrderStatusEnum orderStatus, Boolean deleted, LocalDate startDate, LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(1095);
        } LocalDateTime startDateTime = startDate.atStartOfDay();

        if (endDate == null) {
            endDate = LocalDate.now();
        } LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);


        BooleanExpression statusCondition = null;
        if (orderStatus == OrderStatusEnum.HIT) {
            statusCondition = betHistory.orderStatus.in(OrderStatusEnum.HIT, OrderStatusEnum.CANCEL, OrderStatusEnum.CANCEL_HIT);
        } else if (orderStatus == OrderStatusEnum.FAIL) {
            statusCondition = betHistory.orderStatus.in(OrderStatusEnum.FAIL);
        } else if (orderStatus == OrderStatusEnum.WAITING) {
            statusCondition = betHistory.orderStatus.in(OrderStatusEnum.WAITING);
        }


        long totalElements = queryFactory.select(betHistory.betGroupId.countDistinct())
                .from(betHistory)
                .where(betHistory.betStartTime.between(startDateTime, endDateTime)
                        .and(userIdEq(userId))
                        .and(gameCustomEq(custom))
                        .and(usernameEq(username))
                        .and(nicknameEq(nickname))
                        .and(foldCountEq(betFoldCount))
                        .and(ipEq(ip))
                        .and(historyIdEq(id))
                        .and(betGroupIdIn(betGroupId))
                        .and(statusCondition)
                        .and(deletedOrderEq(deleted)))
                .fetchOne();
        return totalElements;
    }


    @Override
    public double calculateProfit(Long groupId) {
        List<String> rateStrings = queryFactory.select(betHistory.price)
                .from(betHistory)
                .where(betHistory.betGroupId.eq(groupId))
                .fetch();

        double totalRate = 1.0;
        for (String rateString : rateStrings) {
            double rate = Double.parseDouble(rateString);
            totalRate *= rate;
        }
        return totalRate;
    }


    @Override
    public List<BetHistory> searchByBetHistories(String matchId, String marketName, String winIdx, String drawIdx, String loseIdx) {
        List<BetHistory> list = queryFactory.select(betHistory)
                .from(betHistory)
                .where(betHistory.matchId.eq(matchId)
                        .and(betHistory.marketName.eq(marketName))
                        .or(betHistory.idx.in(winIdx, drawIdx, loseIdx)))
                .distinct()
                .fetch();
        return list;
    }


    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? betHistory.user.id.eq(userId) : null;
    }
    private BooleanExpression gameCustomEq(BetTypeEnum custom) {
        return custom != null ? betHistory.betType.eq(custom) : null;
    }
    private BooleanExpression usernameEq(String username) {
        return username != null ? user.username.eq(username) : null;
    }
    private BooleanExpression nicknameEq(String nickname) {
        return nickname != null ? user.nickname.eq(nickname) : null;
    }
    private BooleanExpression foldCountEq(BetFoldCountEnum foldCount) {
        return foldCount != null ? betHistory.betFoldCount.eq(foldCount) : null;
    }
    private BooleanExpression ipEq(String ip) {
        return ip != null ? betHistory.betIp.eq(ip) : null;
    }
    private BooleanExpression historyIdEq(Long historyIdEq) {
        return historyIdEq != null ? betHistory.id.eq(historyIdEq) : null;
    }
    private BooleanExpression betGroupIdIn(List<Long> betGroupIds) {
        return betGroupIds == null || betGroupIds.isEmpty() ? null : betHistory.betGroupId.in(betGroupIds);
    }
    private BooleanExpression deletedOrderEq(Boolean deleted) {
        return deleted != null ? betHistory.deleted.eq(deleted) : betHistory.deleted.eq(true).or(betHistory.deleted.eq(false));
    }
}
