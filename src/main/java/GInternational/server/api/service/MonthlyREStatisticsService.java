//package GInternational.server.api.service;
//
//import GInternational.server.api.repository.ExchangeRepository;
//import GInternational.server.api.repository.RechargeTransactionRepository;
//import GInternational.server.api.repository.RollingTransactionRepository;
//import GInternational.server.api.dto.DailyREStatisticsDTO;
//import GInternational.server.api.dto.MonthlyREStatisticsDTO;
//import GInternational.server.api.repository.PointLogRepository;
//import GInternational.server.security.auth.PrincipalDetails;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.text.DecimalFormat;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//@Transactional(value = "clientServerTransactionManager")
//@RequiredArgsConstructor
//public class MonthlyREStatisticsService {
//
//    private final RechargeTransactionRepository rechargeTransactionRepository;
//    private final ExchangeRepository exchangeRepository;
//    private final PointLogRepository pointLogRepository;
//    private final RollingTransactionRepository rollingTransactionRepository;
//
//    /**
//     * 월별 통계 계산.
//     *
//     * @param month 분석할 월
//     * @param year 분석할 연도
//     * @param principalDetails 인증된 사용자 정보
//     * @return MonthlyREStatisticsDTO 월별 통계 데이터
//     */
//    public MonthlyREStatisticsDTO calculateMonthlyStatistics(int month, int year, PrincipalDetails principalDetails) {
//        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
//        LocalDateTime endOfMonth = LocalDateTime.of(year, month, 1, 23, 59, 59).plusMonths(1).minusDays(1);
//
//        int totalFirstRechargeCount = rechargeTransactionRepository.countFirstRechargesByProcessedAtBetweenAndUserRole(startOfMonth, endOfMonth);
//        int daysWithFirstRecharge = rechargeTransactionRepository.countDaysWithFirstRechargeByUserRole(month, year);
//
//        int daysWithRecharge = rechargeTransactionRepository.countDaysWithRechargeByUserRole(month, year);
//        int daysWithExchange = exchangeRepository.countDaysWithExchangeByUserRole(month, year);
//
//        long totalRechargeAmount = defaultIfNull(rechargeTransactionRepository.sumRechargeAmountByMonthAndYearForRoleUser(month, year), 0L); // 월 총 충전금액
//        int totalRechargeCount = defaultIfNull(rechargeTransactionRepository.countRechargeTransactionsByMonthAndYearForRoleUser(month, year), 0); // 월 총 충전건수
//        long totalExchangeAmount = defaultIfNull(exchangeRepository.sumExchangeAmountByMonthAndYearForRoleUser(month, year), 0L); // 월 총 환전금액
//        int totalExchangeCount = defaultIfNull(exchangeRepository.countExchangeTransactionsByMonthAndYearForRoleUser(month, year), 0); // 월 총 환전건수
//
//        long totalPoint = defaultIfNull(pointLogRepository.sumPointsByMonthAndYearForRoleUser(month, year), 0L); // 월 총 포인트지급
//        long totalRollingPoint = defaultIfNull(rollingTransactionRepository.sumRollingPointsByMonthAndYearForRoleUser(month, year), 0L); // 월 총 카지노 이벤트 지급 (슬롯 롤링)
//        long totalNetRechargeAmount = defaultIfNull((totalRechargeAmount - totalExchangeAmount), 0L); // 월 총 충전-환전 차액
//
//        double averageFirstRechargeCount = daysWithFirstRecharge > 0 ? truncateDouble((double) totalFirstRechargeCount / daysWithFirstRecharge, 2) : 0.0;
//        double averageRechargeAmount = truncateDouble((double) totalRechargeAmount / daysWithRecharge, 2);
//        double averageRechargeCount = truncateDouble((double) totalRechargeCount / daysWithRecharge, 2);
//        double averageExchangeAmount = truncateDouble((double) totalExchangeAmount / daysWithExchange, 2);
//        double averageExchangeCount = truncateDouble((double) totalExchangeCount / daysWithExchange, 2);
//
//        Double averageRevenueRate = truncateDouble(calculateTotalMonthlyRevenueRate(month, year), 2);
//
//        // 월별 평균 통계 계산
//        MonthlyREStatisticsDTO monthlyStats = new MonthlyREStatisticsDTO();
//        monthlyStats.setMonthlyTotalRechargeAmount(totalRechargeAmount);
//        monthlyStats.setMonthlyTotalRechargeCount(totalRechargeCount);
//        monthlyStats.setMonthlyTotalExchangeAmount(totalExchangeAmount);
//        monthlyStats.setMonthlyTotalExchangeCount(totalExchangeCount);
//        monthlyStats.setMonthlyTotalPoint(totalPoint);
//        monthlyStats.setMonthlyTotalRollingPoint(totalRollingPoint);
//        monthlyStats.setMonthlyTotalNetRechargeAmount(totalNetRechargeAmount);
//        monthlyStats.setAverageMonthlyFirstRechargeCount(averageFirstRechargeCount);
//        monthlyStats.setAverageMonthlyRechargeAmount(averageRechargeAmount);
//        monthlyStats.setAverageMonthlyRechargeCount(averageRechargeCount);
//        monthlyStats.setAverageMonthlyExchangeAmount(averageExchangeAmount);
//        monthlyStats.setAverageMonthlyExchangeCount(averageExchangeCount);
//        monthlyStats.setAverageMonthlyRevenueRate(averageRevenueRate);
//
//        return new MonthlyREStatisticsDTO(
//                totalRechargeAmount,
//                totalRechargeCount,
//                totalExchangeAmount,
//                totalExchangeCount,
//                totalPoint,
//                totalRollingPoint,
//                totalNetRechargeAmount,
//                averageFirstRechargeCount,
//                averageRechargeAmount,
//                averageRechargeCount,
//                averageExchangeAmount,
//                averageExchangeCount,
//                averageRevenueRate
//        );
//    }
//
//    /**
//     * 지정된 월과 연도에 대한 총 수익률 계산.
//     *
//     * @param month 분석할 월
//     * @param year 분석할 연도
//     * @return Double 월 전체의 평균 수익률
//     */
//    public Double calculateTotalMonthlyRevenueRate(int month, int year) {
//        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
//        LocalDateTime endOfMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
//
//        if (endOfMonth.getMonthValue() != month || endOfMonth.getYear() != year) {
//            endOfMonth = LocalDateTime.of(year, month, 1, 23, 59, 59).plusMonths(1).minusNanos(1);
//        }
//
//        double totalRevenueRate = 0.0;
//        int count = 0;
//
//        for (LocalDateTime currentDate = startOfMonth; !currentDate.isAfter(endOfMonth); currentDate = currentDate.plusDays(1)) {
//            String dailyRevenueRateStr = getRevenueRateByDate(month, year, currentDate);
//            if (dailyRevenueRateStr != null) {
//                Double dailyRevenueRate = Double.parseDouble(dailyRevenueRateStr);
//                totalRevenueRate += dailyRevenueRate;
//                count++;
//            }
//        }
//
//        return truncateDouble(count > 0 ? totalRevenueRate / count : 0.0, 2);
//    }
//
//    /**
//     * 지정된 월과 연도에 대한 일별 통계를 계산하고 반환.
//     *
//     * @param month 분석할 월
//     * @param year 분석할 연도
//     * @param principalDetails 인증된 사용자 정보
//     * @return Map<Integer, DailyREStatisticsDTO> 각 날짜별 통계 데이터를 담은 맵
//     */
//    public Map<Integer, DailyREStatisticsDTO> calculateDailyStatistics(int month, int year, PrincipalDetails principalDetails) {
//        Map<Integer, DailyREStatisticsDTO> dailyStats = new HashMap<>();
//        DecimalFormat df = new DecimalFormat("#");
//        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
//        LocalDateTime endOfMonth = LocalDateTime.of(year, month, 1, 23, 59, 59).plusMonths(1).minusDays(1);
//
//        // 해당 월의 각 일자에 대한 통계 계산
//        for (LocalDateTime currentDate = startOfMonth; !currentDate.isAfter(endOfMonth); currentDate = currentDate.plusDays(1)) {
//            LocalDateTime startOfDay = currentDate;
//            LocalDateTime endOfDay = currentDate.plusDays(1).minusNanos(1);
//
//            // 해당 일자의 첫충전건수 계산
//            int dailyFirstRechargeCount = rechargeTransactionRepository.countDistinctFirstRechargesByDayForRoleUser(startOfDay, endOfDay);
//            Long dailyRollingPoint = defaultIfNull(rollingTransactionRepository.getRollingPointByDateForRoleUser(startOfDay, endOfDay), 0L);
//            Long dailyPoint = defaultIfNull(pointLogRepository.getPointByDateForRoleUser(startOfDay, endOfDay), 0L);
//
//            // 해당 일자의 일별 충전 정보 계산
//            Long dailyTotalRechargeAmount = defaultIfNull(rechargeTransactionRepository.getDailyTotalRechargeAmountForRoleUser(startOfDay, endOfDay), 0L);
//            Integer dailyRechargeCount = defaultIfNull(rechargeTransactionRepository.getDailyRechargeCountForRoleUser(startOfDay, endOfDay), 0);
//
//            // 일별 통계 정보 생성
//            DailyREStatisticsDTO dailyStat = new DailyREStatisticsDTO();
//            dailyStat.setFirstRechargeCount(dailyFirstRechargeCount);
//            dailyStat.setTotalRechargeAmount(dailyTotalRechargeAmount);
//            dailyStat.setRechargeCount(dailyRechargeCount);
//
//            // 일별 평균 충전 금액 계산
//            if (dailyRechargeCount > 0) {
//                double averageAmount = truncateDouble((double) dailyTotalRechargeAmount / dailyRechargeCount, 2);
//                dailyStat.setAverageRechargeAmount(String.valueOf(averageAmount));
//            } else {
//                dailyStat.setAverageRechargeAmount("0");
//            }
//
//            // 일별 환전 정보 계산
//            dailyStat.setTotalExchangeAmount(defaultIfNull(exchangeRepository.getDailyTotalExchangeAmountForRoleUser(startOfDay, endOfDay), 0L));
//            dailyStat.setExchangeCount(defaultIfNull(exchangeRepository.getDailyExchangeCountForRoleUser(startOfDay, endOfDay), 0));
//
//            // 일별 충전-환전 차액금액 계산
//            dailyStat.setNetRechargeAmount(getNetRechargeAmountByDate(month, year, currentDate));
//            // 일별 수익률 계산
//            String dailyRevenueRate = getRevenueRateByDate(month, year, currentDate);
//            dailyStat.setRevenueRate(truncateDouble(Double.parseDouble(dailyRevenueRate), 2));
//            // 해당 일자의 포인트 정보 계산
//            dailyStat.setPoint(dailyPoint);
//            // 해당 일자의 슬롯 롤링 포인트 정보 계산
//            dailyStat.setRollingPoint(dailyRollingPoint);
//
//            dailyStats.put(currentDate.getDayOfMonth(), dailyStat);
//        }
//        return dailyStats;
//    }
//
//    /**
//     * 지정된 날짜에 대한 순 수익 금액(충전 금액 - 환전 금액)을 계산.
//     *
//     * @param month 분석할 월
//     * @param year 분석할 연도
//     * @param currentDate 분석할 특정 날짜
//     * @return Long 지정된 날짜의 순 수익 금액
//     */
//    public Long getNetRechargeAmountByDate(int month, int year, LocalDateTime currentDate) {
//        LocalDateTime startOfDay = currentDate;
//        LocalDateTime endOfDay = currentDate.plusDays(1).minusNanos(1);
//
//        Long totalRechargeAmount = defaultIfNull(rechargeTransactionRepository.getDailyTotalRechargeAmountForRoleUser(startOfDay, endOfDay), 0L);
//        Long totalExchangeAmount = defaultIfNull(exchangeRepository.getDailyTotalExchangeAmountForRoleUser(startOfDay, endOfDay), 0L);
//        return totalRechargeAmount - totalExchangeAmount;
//    }
//
//    /**
//     * 지정된 날짜에 대한 수익률을 계산하고 반환합.
//     *
//     * @param month 분석할 월
//     * @param year 분석할 연도
//     * @param currentDate 분석할 특정 날짜
//     * @return String 지정된 날짜의 수익률(%), 문자열 포맷
//     */
//    public String getRevenueRateByDate(int month, int year, LocalDateTime currentDate) {
//        LocalDateTime startOfDay = currentDate;
//        LocalDateTime endOfDay = currentDate.plusDays(1).minusNanos(1);
//
//        Long netRechargeAmount = getNetRechargeAmountByDate(month, year, currentDate);
//        Long totalRechargeAmount = defaultIfNull(rechargeTransactionRepository.getDailyTotalRechargeAmountForRoleUser(startOfDay, endOfDay), 0L);
//
//        if (totalRechargeAmount == 0) {
//            return "0.00";
//        }
//
//        return String.format("%.2f", truncateDouble(((double) netRechargeAmount / totalRechargeAmount) * 100, 2));
//    }
//
//    /**
//     * 주어진 값이 null인 경우 기본값을 반환하는 유틸리티 메서드.
//     *
//     * @param <T> 값의 타입
//     * @param value 검사할 값
//     * @param defaultValue 기본값
//     * @return T 검사한 값 또는 기본값
//     */
//    private <T> T defaultIfNull(T value, T defaultValue) {
//        return value != null ? value : defaultValue;
//    }
//
//    private double truncateDouble(double value, int decimalPlaces) {
//        if (decimalPlaces < 0) throw new IllegalArgumentException();
//        BigDecimal bd = BigDecimal.valueOf(value);
//        bd = bd.setScale(decimalPlaces, RoundingMode.DOWN);
//        return bd.doubleValue();
//    }
//}