package GInternational.server.api.service;

import GInternational.server.api.dto.SideBar1DTO;
import GInternational.server.api.dto.SideBar2DTO;
import GInternational.server.api.dto.SideBar3DTO;
import GInternational.server.api.entity.BetHistory;
import GInternational.server.api.entity.DifferenceStatistic;
import GInternational.server.api.repository.*;
import GInternational.server.api.vo.*;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class SideBarService {

    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final PasswordChangeTransactionRepository passwordChangeTransactionRepository;
    private final BetHistoryRepository betHistoryRepository;
    private final WalletRepository walletRepository;
    private final CasinoRepository casinoRepository;
    private final PointLogRepository pointLogRepository;
    private final DifferenceStatisticRepository differenceStatisticRepository;

    public SideBar1DTO calculate1(PrincipalDetails principalDetails) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(zoneId).toLocalDateTime();
        LocalDateTime endOfDay = LocalDate.now(zoneId).plusDays(1).atStartOfDay().with(LocalTime.MAX);

        Set<TransactionEnum> unreadStatuses = EnumSet.of(TransactionEnum.UNREAD);
        int todayRechargeRequestCount = rechargeTransactionRepository.countByCreatedAtBetweenAndStatus(
                startOfDay, endOfDay, unreadStatuses);
        int todayExchangeRequestCount = exchangeRepository.countByCreatedAtBetweenAndStatus(
                startOfDay, endOfDay, TransactionEnum.UNREAD);
        int todayJoinRequestCount = userRepository.countByCreatedAtBetweenAndUserGubunEnum(
                startOfDay, endOfDay, UserGubunEnum.대기);

        int totalCustomerCenterCount = articleRepository.countByAnswerStatus("답변요청");
        int totalLoginRequestCount = articleRepository.countByAnswerStatus("로그인문의 답변요청");
        int totalPasswordRequestCount = passwordChangeTransactionRepository.countByStatus("대기");
        DifferenceStatistic lastStatistic = differenceStatisticRepository.findTopByOrderByCreatedAtDesc();
        long centerSiteBalance = lastStatistic != null ? lastStatistic.getTotalAccount() : 0;

        int inplayBetMonitoring = betHistoryRepository.countUnreadByMonitoringStatus(UserMonitoringStatusEnum.주시베팅, UserMonitoringStatusEnum.초과베팅).intValue();

        return new SideBar1DTO(
                todayRechargeRequestCount,
                todayExchangeRequestCount,
                todayJoinRequestCount,
                totalCustomerCenterCount,
                totalLoginRequestCount,
                totalPasswordRequestCount,
                centerSiteBalance,
                inplayBetMonitoring
        );
    }

    public SideBar2DTO calculate2(PrincipalDetails principalDetails) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(zoneId).toLocalDateTime();
        LocalDateTime endOfDay = LocalDate.now(zoneId).plusDays(1).atStartOfDay().with(LocalTime.MAX);

        Long totalSportsBalance = walletRepository.sumAllSportsBalance();
        Long totalPoint = walletRepository.sumAllPoint();

        Set<TransactionEnum> approvalStatuses = EnumSet.of(TransactionEnum.APPROVAL, TransactionEnum.AUTO_APPROVAL);
        int todayRechargeCount = Optional.of(rechargeTransactionRepository.countByCreatedAtBetweenAndStatus(
                startOfDay, endOfDay, approvalStatuses)).orElse(0);
        Long todayRechargeSum = Optional.ofNullable(rechargeTransactionRepository.sumRechargeAmountByUserRolesAndProcessedAtBetweenAndStatus(
                "ROLE_USER", startOfDay, endOfDay, TransactionEnum.APPROVAL)).orElse(0L);
        int todayExchangeCount = Optional.of(exchangeRepository.countByCreatedAtBetweenAndStatus(
                startOfDay, endOfDay, TransactionEnum.APPROVAL)).orElse(0);
        Long todayExchangeSum = Optional.ofNullable(exchangeRepository.sumExchangeAmountByUserRolesAndProcessedAtBetweenAndStatus(
                "ROLE_USER", startOfDay, endOfDay, TransactionEnum.APPROVAL)).orElse(0L);
        Long todayDifference = todayRechargeSum - todayExchangeSum;

        int todayTransformToCasinoCount = casinoRepository.countByDescriptionAndStatus(
                TransactionEnum.APPROVAL, "스포츠머니 -> 카지노머니로 전환", startOfDay, endOfDay);
        Long todayTransformToCasinoBalance = Optional.ofNullable(casinoRepository.sumUsedSportsBalanceForConversion(
                TransactionEnum.APPROVAL, "스포츠머니 -> 카지노머니로 전환", startOfDay, endOfDay)).orElse(0L);
        int todayTransformToSportsCount = casinoRepository.countByDescriptionAndStatus(
                TransactionEnum.APPROVAL, "카지노머니 -> 스포츠머니로 전환", startOfDay, endOfDay);
        Long todayTransformToSportsBalance = Optional.ofNullable(casinoRepository.sumUsedCasinoBalanceForConversion(
                TransactionEnum.APPROVAL, "카지노머니 -> 스포츠머니로 전환", startOfDay, endOfDay)).orElse(0L);

        List<PointLogCategoryEnum> categories = Arrays.asList(PointLogCategoryEnum.포인트수동지급, PointLogCategoryEnum.행운복권);
        List<PointLogCategoryEnum> autoCategories = Arrays.asList(PointLogCategoryEnum.룰렛, PointLogCategoryEnum.충전, PointLogCategoryEnum.사과줍기,
                PointLogCategoryEnum.콤프, PointLogCategoryEnum.슬롯롤링적립, PointLogCategoryEnum.출석체크룰렛);
        Long todayPoint = Optional.ofNullable(pointLogRepository.sumPointsByCategoriesAndCreatedAtBetween(
                categories, startOfDay, endOfDay)).orElse(0L);
        Long todayAutoPoint = Optional.ofNullable(pointLogRepository.sumPointsByCategoriesAndCreatedAtBetween(
                autoCategories, startOfDay, endOfDay)).orElse(0L);

        Long todayChargedCountSum = walletRepository.countByTodayChargedCount();
        int todayJoinSum = userRepository.countByCreatedAtBetweenAndRole(startOfDay, endOfDay, "ROLE_USER");


        return new SideBar2DTO(
                totalSportsBalance,
                totalPoint,
                todayRechargeCount,
                todayRechargeSum,
                todayExchangeCount,
                todayExchangeSum,
                todayDifference,
                todayTransformToCasinoCount,
                todayTransformToCasinoBalance,
                todayTransformToSportsCount,
                todayTransformToSportsBalance,
                todayPoint,
                todayAutoPoint,
                todayChargedCountSum,
                todayJoinSum
        );
    }

    public SideBar3DTO calculate3(PrincipalDetails principalDetails) {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(seoulZone).toLocalDateTime();
        LocalDateTime endOfDay = LocalDate.now(seoulZone).plusDays(1).atStartOfDay().with(LocalTime.MAX);

        List<OrderStatusEnum> statuses = Arrays.asList(OrderStatusEnum.WAITING, OrderStatusEnum.HIT, OrderStatusEnum.FAIL);
        List<BetHistory> bets = betHistoryRepository.findBetsByStatusAndBetStartTimeBetween(statuses, startOfDay, endOfDay);
        List<OrderStatusEnum> waitingStatus = Collections.singletonList(OrderStatusEnum.WAITING);
        List<BetHistory> waitingBets = betHistoryRepository.findBetsByStatusAndBetStartTimeBetween(waitingStatus, startOfDay, endOfDay);

        // betGroupId 별로 bet을 파싱하여 합산
        Map<Long, Long> betAmounts = bets.stream()
                .collect(Collectors.groupingBy(BetHistory::getBetGroupId,
                        Collectors.collectingAndThen(Collectors.toList(), list -> list.stream()
                                .map(bh -> parseBetAmount(bh.getBet()))
                                .findFirst().orElse(0L))));

        // betGroupId 별로 betReward를 파싱하여 합산
        Map<Long, Long> rewardAmounts = bets.stream()
                .filter(bh -> bh.getBetReward() != null && !bh.getBetReward().isEmpty())
                .collect(Collectors.groupingBy(BetHistory::getBetGroupId,
                        Collectors.collectingAndThen(Collectors.toList(), list -> list.stream()
                                .map(bh -> parseBetAmount(bh.getBetReward()))
                                .findFirst().orElse(0L))));

        // 프리매치 베팅 건 필터링 및 중복 제거
        Map<Long, List<BetHistory>> prematchBets = waitingBets.stream()
                .filter(bh -> bh.getBetType() == BetTypeEnum.PRE_MATCH)
                .collect(Collectors.groupingBy(BetHistory::getBetGroupId));

        int prematchCount = prematchBets.size();
        Long prematchSum = prematchBets.values().stream()
                .map(list -> list.stream()
                        .findFirst() // 각 betGroupId에 대해 첫 번째 항목만 사용
                        .map(bh -> parseBetAmount(bh.getBet()))
                        .orElse(0L))
                .mapToLong(Long::longValue)
                .sum();

        // 인플레이 베팅 건 필터링 및 중복 제거
        Map<Long, List<BetHistory>> inplayBets = waitingBets.stream()
                .filter(bh -> bh.getBetType() == BetTypeEnum.IN_PLAY)
                .collect(Collectors.groupingBy(BetHistory::getBetGroupId));

        int inplayCount = inplayBets.size();
        Long inplaySum = inplayBets.values().stream()
                .map(list -> list.stream()
                        .findFirst() // 각 betGroupId에 대해 첫 번째 항목만 사용
                        .map(bh -> parseBetAmount(bh.getBet()))
                        .orElse(0L))
                .mapToLong(Long::longValue)
                .sum();

        // 일반(크로스,승무패,핸디캡) 베팅 건 필터링 및 중복 제거
        Set<BetTypeEnum> normalTypes = EnumSet.of(BetTypeEnum.CROSS, BetTypeEnum.HANDICAP, BetTypeEnum.W_D_L);
        Map<Long, List<BetHistory>> normalBets = waitingBets.stream()
                .filter(bh -> normalTypes.contains(bh.getBetType()))
                .collect(Collectors.groupingBy(BetHistory::getBetGroupId));

        int normalCount = normalBets.size();
        Long normalSum = normalBets.values().stream()
                .map(list -> list.stream()
                        .findFirst() // 각 betGroupId에 대해 첫 번째 항목만 사용
                        .map(bh -> parseBetAmount(bh.getBet()))
                        .orElse(0L))
                .mapToLong(Long::longValue)
                .sum();

        // 스페셜1 베팅 건 필터링 및 중복 제거
        Map<Long, List<BetHistory>> specialBets = waitingBets.stream()
                .filter(bh -> bh.getBetType() == BetTypeEnum.SPECIAL_ONE)
                .collect(Collectors.groupingBy(BetHistory::getBetGroupId));

        int specialCount = specialBets.size();
        Long specialSum = specialBets.values().stream()
                .map(list -> list.stream()
                        .findFirst() // 각 betGroupId에 대해 첫 번째 항목만 사용
                        .map(bh -> parseBetAmount(bh.getBet()))
                        .orElse(0L))
                .mapToLong(Long::longValue)
                .sum();

        // 스페셜2 베팅 건 필터링 및 중복 제거
        Map<Long, List<BetHistory>> special2Bets = waitingBets.stream()
                .filter(bh -> bh.getBetType() == BetTypeEnum.SPECIAL_TWO)
                .collect(Collectors.groupingBy(BetHistory::getBetGroupId));

        int special2Count = special2Bets.size();
        Long special2Sum = special2Bets.values().stream()
                .map(list -> list.stream()
                        .findFirst() // 각 betGroupId에 대해 첫 번째 항목만 사용
                        .map(bh -> parseBetAmount(bh.getBet()))
                        .orElse(0L))
                .mapToLong(Long::longValue)
                .sum();


        // betGroupId 별로 대기중인 bet을 파싱하여 합산
        Map<Long, Long> waitingBetAmounts = waitingBets.stream()
                .collect(Collectors.groupingBy(BetHistory::getBetGroupId,
                        Collectors.collectingAndThen(Collectors.toList(), list -> list.stream()
                                .map(bh -> parseBetAmount(bh.getBet()))
                                .findFirst().orElse(0L))));

        Long todayBetAmountSum = betAmounts.values().stream().mapToLong(Long::longValue).sum();
        Long totalRewardSum = rewardAmounts.values().stream().mapToLong(Long::longValue).sum();

        Long todayAdjustment = todayBetAmountSum - totalRewardSum;

        Long leftBet = waitingBetAmounts.values().stream().mapToLong(Long::longValue).sum();

        return new SideBar3DTO(
                String.valueOf(todayBetAmountSum),
                String.valueOf(todayAdjustment),
                prematchCount,
                String.valueOf(prematchSum),
                inplayCount,
                String.valueOf(inplaySum),
                normalCount,
                String.valueOf(normalSum),
                specialCount,
                String.valueOf(specialSum),
                special2Count,
                String.valueOf(special2Sum),
                String.valueOf(leftBet)
        );
    }

    private Long parseBetAmount(String betAmount) {
        if (betAmount == null || betAmount.isEmpty()) return 0L;
        try {
            BigDecimal bd = new BigDecimal(betAmount);
            return bd.longValue();
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse bet amount: " + betAmount);
            return 0L;
        }
    }
}