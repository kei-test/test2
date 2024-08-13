package GInternational.server.api.service;

import GInternational.server.api.entity.Articles;
import GInternational.server.api.repository.*;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.kplay.debit.entity.Debit;
import GInternational.server.kplay.debit.repository.DebitRepository;
import GInternational.server.api.dto.MonthlyBetStatisticDailyAmountDTO;
import GInternational.server.api.dto.MonthlyBetStatisticMonthlyTotalsDTO;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class MonthlyBetStatisticService {

    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final ExchangeRepository exchangeRepository;
    private final DebitRepository debitRepository;
    private final PointLogRepository pointLogRepository;
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BetHistoryRepository betHistoryRepository;

    /**
     * 지정된 월과 연도에 대한 모든 통계 합계를 계산.
     *
     * @param month 월
     * @param year 연도
     * @param principalDetails 사용자 인증 정보
     * @return MonthlyBetStatisticMonthlyTotalsDTO 월별 총계 정보를 담은 DTO
     */
    public MonthlyBetStatisticMonthlyTotalsDTO calculateMonthlyTotals(int month, int year, PrincipalDetails principalDetails) {
        MonthlyBetStatisticMonthlyTotalsDTO rechargeTotals = calculateMonthlyRechargeTotals(month, year);
        MonthlyBetStatisticMonthlyTotalsDTO exchangeTotals = calculateMonthlyExchangeTotals(month, year);
        MonthlyBetStatisticMonthlyTotalsDTO debitTotals = calculateMonthlyDebitTotals(month, year);
        MonthlyBetStatisticMonthlyTotalsDTO pointTotals = calculateMonthlyPointTotals(month, year);

        Long customerCenterCategoryId = categoryRepository.findByName("고객센터")
                .orElseThrow(() -> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND))
                .getId();

        Long boardCategoryId = categoryRepository.findByName("게시판")
                .orElseThrow(() -> new RestControllerException(ExceptionCode.CATEGORY_NOT_FOUND))
                .getId();

        // 고객센터 등록글 수 계산
        List<Articles> customerCenterArticles = articleRepository.findByCategoryIdAndMonthAndYearAndUserRole(customerCenterCategoryId, month, year);
        Map<Integer, Long> dailyCustomerCenterPosts = initializeDailyTotalsForMonth(month, year);
        customerCenterArticles.forEach(article -> {
            int dayOfMonth = article.getCreatedAt().getDayOfMonth();
            dailyCustomerCenterPosts.merge(dayOfMonth, 1L, Long::sum);
        });

        // 게시판 등록글 수 계산
        List<Articles> boardArticles = articleRepository.findByCategoryIdAndMonthAndYearAndUserRole(boardCategoryId, month, year);
        Map<Integer, Long> dailyBoardPosts = initializeDailyTotalsForMonth(month, year);
        boardArticles.forEach(article -> {
            int dayOfMonth = article.getCreatedAt().getDayOfMonth();
            dailyBoardPosts.merge(dayOfMonth, 1L, Long::sum);
        });

        // 회원가입 수 계산
        List<User> joinedUsers = userRepository.findByCreatedAtMonthAndYearAndRoleUser(month, year);
        Map<Integer, Long> dailyJoinTotals = initializeDailyTotalsForMonth(month, year);
        joinedUsers.forEach(user -> {
            int dayOfMonth = user.getCreatedAt().getDayOfMonth();
            dailyJoinTotals.merge(dayOfMonth, 1L, Long::sum);
        });

        // 실 입금자 수 계산
        List<Object[]> distinctUserIds = rechargeTransactionRepository.findDistinctUserIdsByProcessedMonthAndYearAndRoleUser(month, year);
        Map<Integer, Long> dailyRechargeCounts = initializeDailyTotalsForMonth(month, year);
        distinctUserIds.forEach(record -> {
            int dayOfMonth = (Integer) record[1];
            dailyRechargeCounts.merge(dayOfMonth, 1L, Long::sum);
        });

        // 각 일별 당첨금 계산
        Map<Integer, Long> dailyBetRewardTotals = initializeDailyTotalsForMonth(month, year);
        List<Object[]> dailyBetRewards = betHistoryRepository.findDailyBetRewardSum(year, month);
        dailyBetRewards.forEach(record -> {
            Timestamp timestamp = (Timestamp) record[0];
            LocalDate date = timestamp.toLocalDateTime().toLocalDate();
            Long betRewardSum = record[1] != null ? Math.round((Double) record[1]) : 0L;
            dailyBetRewardTotals.put(date.getDayOfMonth(), betRewardSum);
        });

        Long monthlyCustomerCenterPostsTotal = dailyCustomerCenterPosts.values().stream().mapToLong(Long::longValue).sum();
        Long monthlyBoardPostsTotal = dailyBoardPosts.values().stream().mapToLong(Long::longValue).sum();
        Long monthlyJoinTotal = dailyJoinTotals.values().stream().mapToLong(Long::longValue).sum();
        Long monthlyRechargeCountTotal = dailyRechargeCounts.values().stream().mapToLong(Long::longValue).sum();
        Long monthlyBetRewardTotal = dailyBetRewardTotals.values().stream().mapToLong(Long::longValue).sum();

        Map<Integer, Long> dailyDifferenceAmounts = new HashMap<>();
        rechargeTotals.getDailyRechargeTotals().forEach((day, rechargeAmount) -> {
            Long exchangeAmount = exchangeTotals.getDailyExchangeTotals().getOrDefault(day, 0L);
            dailyDifferenceAmounts.put(day, rechargeAmount - exchangeAmount);
        });

        Long monthlyRechargeTotal = rechargeTotals.getMonthlyRechargeTotal();
        Long monthlyExchangeTotal = exchangeTotals.getMonthlyExchangeTotal();
        Long monthlyDifferenceAmount = monthlyRechargeTotal - monthlyExchangeTotal;

        Map<Integer, Double> dailyRevenueRates = calculateDailyRevenueRates(dailyDifferenceAmounts, exchangeTotals.getDailyExchangeTotals(), rechargeTotals.getDailyRechargeTotals());
        Double monthlyRevenueRate = calculateMonthlyRevenueRate(dailyRevenueRates, month, year);

        MonthlyBetStatisticMonthlyTotalsDTO combinedTotalsDTO = new MonthlyBetStatisticMonthlyTotalsDTO();
        combinedTotalsDTO.setDailyRechargeTotals(rechargeTotals.getDailyRechargeTotals());
        combinedTotalsDTO.setDailyExchangeTotals(exchangeTotals.getDailyExchangeTotals());
        combinedTotalsDTO.setDailyDebitTotals(debitTotals.getDailyDebitTotals());
        combinedTotalsDTO.setDailyBetRewardTotals(dailyBetRewardTotals);
        combinedTotalsDTO.setDailyPointTotals(pointTotals.getDailyPointTotals());
        combinedTotalsDTO.setDailyCustomerCenterPosts(dailyCustomerCenterPosts);
        combinedTotalsDTO.setDailyDifferenceAmounts(dailyDifferenceAmounts);
        combinedTotalsDTO.setDailyRevenueRates(dailyRevenueRates);
        combinedTotalsDTO.setMonthlyRechargeTotal(monthlyRechargeTotal);
        combinedTotalsDTO.setMonthlyExchangeTotal(monthlyExchangeTotal);
        combinedTotalsDTO.setMonthlyDebitTotal(debitTotals.getMonthlyDebitTotal());
        combinedTotalsDTO.setMonthlyBetRewardTotal(monthlyBetRewardTotal);
        combinedTotalsDTO.setMonthlyPointTotal(pointTotals.getMonthlyPointTotal());
        combinedTotalsDTO.setMonthlyCustomerCenterPostsTotal(monthlyCustomerCenterPostsTotal);
        combinedTotalsDTO.setMonthlyDifferenceAmount(monthlyDifferenceAmount);
        combinedTotalsDTO.setMonthlyRevenueRate(monthlyRevenueRate);
        combinedTotalsDTO.setDailyArticlePosts(dailyBoardPosts);
        combinedTotalsDTO.setMonthlyArticlePostsTotal(monthlyBoardPostsTotal);
        combinedTotalsDTO.setDailyJoinTotals(dailyJoinTotals);
        combinedTotalsDTO.setMonthlyJoinTotal(monthlyJoinTotal);
        combinedTotalsDTO.setDailyRechargeCounts(dailyRechargeCounts);
        combinedTotalsDTO.setMonthlyRechargeCountTotal(monthlyRechargeCountTotal);

        return combinedTotalsDTO;
    }

    /**
     * 지정된 월과 연도에 대한 충전 통계 합계 계산.
     *
     * @param month 월
     * @param year 연도
     * @return MonthlyBetStatisticMonthlyTotalsDTO 월별 충전 총계 정보를 담은 DTO
     */
    public MonthlyBetStatisticMonthlyTotalsDTO calculateMonthlyRechargeTotals(int month, int year) {
        Map<Integer, Long> dailyRechargeTotals = initializeDailyTotalsForMonth(month, year);
        List<MonthlyBetStatisticDailyAmountDTO> dailyAmounts = rechargeTransactionRepository.findDailyRechargeAmountSum(month, year);

        dailyAmounts.forEach(dailyAmount ->
                dailyRechargeTotals.put(dailyAmount.getDay(), dailyAmount.getRechargeAmount()));

        Long monthlyTotalRecharge = dailyRechargeTotals.values().stream().mapToLong(Long::longValue).sum();

        MonthlyBetStatisticMonthlyTotalsDTO monthlyBetStatisticMonthlyTotalsDTO = new MonthlyBetStatisticMonthlyTotalsDTO();
        monthlyBetStatisticMonthlyTotalsDTO.setDailyRechargeTotals(dailyRechargeTotals);
        monthlyBetStatisticMonthlyTotalsDTO.setMonthlyRechargeTotal(monthlyTotalRecharge);

        return monthlyBetStatisticMonthlyTotalsDTO;
    }

    /**
     * 지정된 월과 연도에 대한 환전 통계 합계 계산.
     *
     * @param month 월
     * @param year 연도
     * @return MonthlyBetStatisticMonthlyTotalsDTO 월별 환전 총계 정보를 담은 DTO
     */
    public MonthlyBetStatisticMonthlyTotalsDTO calculateMonthlyExchangeTotals(int month, int year) {
        Map<Integer, Long> dailyExchangeTotals = initializeDailyTotalsForMonth(month, year);
        List<MonthlyBetStatisticDailyAmountDTO> dailyExchanges = exchangeRepository.findDailyExchangeAmountSum(month, year);

        dailyExchanges.forEach(dailyExchange ->
                dailyExchangeTotals.put(dailyExchange.getDay(), dailyExchange.getExchangeAmount()));

        Long monthlyTotalExchange = dailyExchangeTotals.values().stream().mapToLong(Long::longValue).sum();

        MonthlyBetStatisticMonthlyTotalsDTO monthlyBetStatisticMonthlyTotalsDTO = new MonthlyBetStatisticMonthlyTotalsDTO();
        monthlyBetStatisticMonthlyTotalsDTO.setDailyExchangeTotals(dailyExchangeTotals);
        monthlyBetStatisticMonthlyTotalsDTO.setMonthlyExchangeTotal(monthlyTotalExchange);

        return monthlyBetStatisticMonthlyTotalsDTO;
    }

    /**
     * 지정된 월과 연도에 대한 베팅 통계 합계 계산.
     *
     * @param month 월
     * @param year 연도
     * @return MonthlyBetStatisticMonthlyTotalsDTO 월별 베팅 총계 정보를 담은 DTO
     */
    public MonthlyBetStatisticMonthlyTotalsDTO calculateMonthlyDebitTotals(int month, int year) {
        List<Integer> userAasIds = userRepository.findAllAasIdsByRole("ROLE_USER");

        LocalDateTime startDateTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDateTime = startDateTime.plusMonths(1).minusSeconds(1);
        List<Debit> debits = debitRepository.findAllByUserIdsAndCreatedDateBetween(userAasIds, startDateTime, endDateTime);

        Map<Integer, Long> dailyDebitTotals = initializeDailyTotalsForMonth(month, year);
        for (Debit debit : debits) {
            LocalDate date = debit.getCreatedAt().toLocalDate();
            dailyDebitTotals.merge(date.getDayOfMonth(), (long)debit.getAmount(), Long::sum);
        }

        Long monthlyTotalDebit = dailyDebitTotals.values().stream().mapToLong(Long::longValue).sum();
        MonthlyBetStatisticMonthlyTotalsDTO dto = new MonthlyBetStatisticMonthlyTotalsDTO();
        dto.setDailyDebitTotals(dailyDebitTotals);
        dto.setMonthlyDebitTotal(monthlyTotalDebit);

        return dto;
    }

    /**
     * 지정된 월과 연도에 대한 포인트 통계 합계 계산.
     *
     * @param month 월
     * @param year 연도
     * @return MonthlyBetStatisticMonthlyTotalsDTO 월별 포인트 총계 정보를 담은 DTO
     */
    public MonthlyBetStatisticMonthlyTotalsDTO calculateMonthlyPointTotals(int month, int year) {
        Map<Integer, Long> dailyPointTotals = initializeDailyTotalsForMonth(month, year);
        List<MonthlyBetStatisticDailyAmountDTO> dailyPoints = pointLogRepository.findDailyPointAmountSum(month, year);

        dailyPoints.forEach(dailyPoint ->
                dailyPointTotals.put(dailyPoint.getDay(), dailyPoint.getPoint()));

        Long monthlyTotalPoint = dailyPointTotals.values().stream().mapToLong(Long::longValue).sum();

        MonthlyBetStatisticMonthlyTotalsDTO monthlyBetStatisticMonthlyTotalsDTO = new MonthlyBetStatisticMonthlyTotalsDTO();
        monthlyBetStatisticMonthlyTotalsDTO.setDailyPointTotals(dailyPointTotals);
        monthlyBetStatisticMonthlyTotalsDTO.setMonthlyPointTotal(monthlyTotalPoint);

        return monthlyBetStatisticMonthlyTotalsDTO;
    }

    /**
     * 지정된 일별 차액과 일별 환전 총액을 기반으로 일별 수익률 계산.
     *
     * @param dailyDifferenceAmounts 일별 차액 목록 (이익 또는 손실)
     * @param dailyExchangeTotals 일별 환전 총액 목록
     * @return Map<Integer, Double> 일별 수익률, 키는 날짜(일)이고 값은 해당 날짜의 수익률.
     */
    private Map<Integer, Double> calculateDailyRevenueRates(Map<Integer, Long> dailyDifferenceAmounts, Map<Integer, Long> dailyExchangeTotals, Map<Integer, Long> dailyRechargeTotals) {
        Map<Integer, Double> dailyRevenueRates = new HashMap<>();

        dailyDifferenceAmounts.forEach((day, profit) -> {
            Long exchangeAmount = dailyExchangeTotals.getOrDefault(day, 0L);
            Long rechargeAmount = dailyRechargeTotals.getOrDefault(day, 0L);

            double rate = 0.0;
            if (rechargeAmount == 0) {
                if (exchangeAmount > 0) {
                    rate = -100.0; // 충전금 0, 환전금 > 0 경우
                } else {
                    rate = 0.0; // 충전금 0, 환전금 0 경우
                }
            } else {
                if (exchangeAmount == 0) {
                    rate = 100.0; // 충전금 > 0, 환전금 0 경우
                } else {
                    // 기본 수익률 계산 (충전금 - 환전금) / 충전금 * 100
                    rate = (double)(rechargeAmount - exchangeAmount) / rechargeAmount * 100;
                    rate = Math.min(100.0, rate); // 최대 수익률 100%
                }
            }

            rate = Math.round(rate * 100.0) / 100.0;
            dailyRevenueRates.put(day, rate);
        });

        return dailyRevenueRates;
    }

    private Double calculateMonthlyRevenueRate(Map<Integer, Double> dailyRevenueRates, int month, int year) {
        LocalDate today = LocalDate.now();
        double sumOfRates = dailyRevenueRates.values().stream().mapToDouble(Double::doubleValue).sum();

        // 조회한 날짜가 당월인 경우 오늘 날짜까지 고려
        int totalDays = (today.getMonthValue() == month && today.getYear() == year) ? today.getDayOfMonth() : dailyRevenueRates.size();

        // 전체 일수가 0인 경우(예외 처리)
        if (totalDays == 0) return 0.0;

        double averageRate = sumOfRates / totalDays;
        averageRate = Math.round(averageRate * 100.0) / 100.0;

        return averageRate;
    }

    /**
     * 지정된 이익과 비용을 바탕으로 수익률 계산.
     *
     * @param profit 이익
     * @param cost 비용
     * @return Double 계산된 수익률, 분모(cost)가 0인 경우 0.0을 반환합니다.
     */
    private Double calculateRevenueRate(Long profit, Long cost) {
        if (cost == 0) return 0.0; // 분모가 0인 경우 수익률을 0으로 설정
        double rate = (double) profit / cost * 100;
        return Math.round(rate * 100.0) / 100.0; // 소수점 둘째 자리까지 반올림
    }

    /**
     * 지정된 월과 연도에 대해 일별 총계 초기화.
     *
     * @param month 월
     * @param year 연도
     * @return Map<Integer, Long> 초기화된 일별 총계, 키는 날짜(일)이고 값은 0L입니다.
     */
    private Map<Integer, Long> initializeDailyTotalsForMonth(int month, int year) {
        Map<Integer, Long> dailyTotals = new HashMap<>();
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            dailyTotals.put(day, 0L);
        }

        return dailyTotals;
    }
}
