package GInternational.server.api.repository;


import GInternational.server.api.dto.AmazonUserInfoDTO;
import GInternational.server.api.dto.UserCalculateDTO;
import GInternational.server.api.entity.*;
import GInternational.server.api.vo.AmazonTransactionEnum;
import GInternational.server.api.vo.UserGubunEnum;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static GInternational.server.api.entity.QUser.user;
import static GInternational.server.api.entity.QWallet.wallet;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {


    private final JPAQueryFactory queryFactory;
    private final AmazonExchangeRepository amazonExchangeRepository;
    private final AmazonRechargeTransactionRepository amazonRechargeTransactionRepository;



    @Override
    public Page<User> deletedUserInfo(Pageable pageable, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);

        List<User> result = queryFactory
                .selectFrom(user)
                .where(user.isDeleted.isTrue().and(user.deletedAt.between(startDateTime,endDateTime)))
                .fetch();

        return new PageImpl<>(result,pageable,result.size());

    }

    //유저 구분이 null인 객체가 있을 경우 정산처리에 에러가 남
    @Override
    public Map<UserGubunEnum, Long> getCountByUserGubunForLevel(int level) {
        List<Tuple> results = queryFactory
                .select(user.userGubunEnum, user.count())
                .from(user)
                .where(user.lv.eq(level).and(user.role.eq("ROLE_USER")))
                .groupBy(user.userGubunEnum)
                .fetch();

        Map<UserGubunEnum, Long> userGubunCountMap = new HashMap<>();
        for (UserGubunEnum userGubunEnum : UserGubunEnum.values()) {
            userGubunCountMap.put(userGubunEnum, 0L);
        }

        for (Tuple result : results) {
            UserGubunEnum userGubunEnum = result.get(user.userGubunEnum);
            Long count = result.get(user.count());
            userGubunCountMap.put(userGubunEnum, count);
        }
        return userGubunCountMap;
    }

    //레벨별 정산
    @Override
    public List<UserCalculateDTO> getTotalAmountForAllLevelForPeriod(int lv, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<UserCalculateDTO> results = queryFactory
                .select(Projections.constructor(UserCalculateDTO.class,
                        user.lv,
                        wallet.depositTotal.sum(),
                        wallet.withdrawTotal.sum(),
                        wallet.depositTotal.sum().subtract(wallet.withdrawTotal.sum()).as("totalSettlement"),
                        wallet.sportsBalance.sum()))
                .from(user)
                .where(user.wallet.lastRechargedAt.between(startDateTime, endDateTime).and(user.wallet.exchangeProcessedAt.between(startDateTime, endDateTime)).and(user.lv.eq(lv)))
                .groupBy(user.lv)
                .fetch();
        return results;
    }


    private long parseStringToLong(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Long.parseLong(value) : 0L;
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public List<AmazonUserInfoDTO> findUsersByIsAmazonUser() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now();

        List<User> users = queryFactory
                .selectFrom(user)
                .where(user.isAmazonUser.isTrue())
                .fetch();

        return users.stream().map(user -> {
            Wallet wallet = user.getWallet();
            long todayDeposit = getTodayDepositByUserId(user.getId(), startOfDay, endOfDay);
            long todayWithdraw = getTodayWithdrawByUserId(user.getId(), startOfDay, endOfDay);
            long totalDeposit = getTotalAmazonDepositByUserId(user.getId());
            long totalWithdraw = getTotalAmazonWithdrawByUserId(user.getId());
            long totalProfitLoss = totalDeposit - totalWithdraw;

            // Wallet에서 아마존 머니와 아마존 포인트를 가져오기
            long amazonMoney = wallet != null ? wallet.getAmazonMoney() : 0;
            long amazonPoint = wallet != null ? wallet.getAmazonPoint() : 0;

            return new AmazonUserInfoDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getNickname(),
                    amazonMoney,
                    amazonPoint,
                    todayDeposit,
                    todayWithdraw,
                    totalDeposit,
                    totalWithdraw,
                    totalProfitLoss,
                    user.getCreatedAt(),
                    user.getLastVisit(),
                    user.getFailVisitCount()
            );
        }).collect(Collectors.toList());
    }

    private long getTodayDepositByUserId(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return amazonRechargeTransactionRepository.findByUserIdAndStatusAndProcessedAtBetween(
                        userId, AmazonTransactionEnum.APPROVAL, startOfDay, endOfDay)
                .stream()
                .mapToLong(AmazonRechargeTransaction::getRechargeAmount)
                .sum();
    }

    private long getTodayWithdrawByUserId(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return amazonExchangeRepository.findByUserIdAndStatusAndProcessedAtBetween(
                        userId, AmazonTransactionEnum.APPROVAL, startOfDay, endOfDay)
                .stream()
                .mapToLong(AmazonExchangeTransaction::getExchangeAmount)
                .sum();
    }

    private long getTotalAmazonDepositByUserId(Long userId) {
        return amazonRechargeTransactionRepository.findByUserIdAndStatus(
                        userId, AmazonTransactionEnum.APPROVAL)
                .stream()
                .mapToLong(AmazonRechargeTransaction::getRechargeAmount)
                .sum();
    }

    private long getTotalAmazonWithdrawByUserId(Long userId) {
        return amazonExchangeRepository.findByUserIdAndStatus(
                        userId, AmazonTransactionEnum.APPROVAL)
                .stream()
                .mapToLong(AmazonExchangeTransaction::getExchangeAmount)
                .sum();
    }

    @Override
    public List<AmazonUserInfoDTO> findUsersByReferredByAndIsAmazonUser(String referredBy) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now();

        List<User> users = queryFactory
                .selectFrom(user)
                .where(user.referredBy.eq(referredBy).and(user.isAmazonUser.isTrue()))
                .fetch();

        return users.stream().map(user -> {
            Wallet wallet = user.getWallet();
            long todayDeposit = getTodayDepositByUserId(user.getId(), startOfDay, endOfDay);
            long todayWithdraw = getTodayWithdrawByUserId(user.getId(), startOfDay, endOfDay);
            long totalDeposit = getTotalAmazonDepositByUserId(user.getId());
            long totalWithdraw = getTotalAmazonWithdrawByUserId(user.getId());
            long totalProfitLoss = totalDeposit - totalWithdraw;

            // Wallet에서 아마존 머니와 아마존 포인트를 가져오기
            long amazonMoney = wallet != null ? wallet.getAmazonMoney() : 0;
            long amazonPoint = wallet != null ? wallet.getAmazonPoint() : 0;

            return new AmazonUserInfoDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getNickname(),
                    amazonMoney,
                    amazonPoint,
                    todayDeposit,
                    todayWithdraw,
                    totalDeposit,
                    totalWithdraw,
                    totalProfitLoss,
                    user.getCreatedAt(),
                    user.getLastVisit(),
                    user.getFailVisitCount()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<User> findTop30ByLastVisitNotNullOrderByLastVisit() {
        QUser user = QUser.user;
        return queryFactory
                .selectFrom(user)
                .where(user.lastVisit.isNotNull()
                        .and(user.role.eq("ROLE_USER").or(user.role.eq("ROLE_TEST"))))
                .orderBy(user.lastVisit.desc())
                .limit(30)
                .fetch();
    }
}