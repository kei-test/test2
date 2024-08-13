package GInternational.server.api.service;

import GInternational.server.api.dto.TransformStatisticResDTO;
import GInternational.server.api.entity.CasinoTransaction;
import GInternational.server.api.repository.CasinoRepository;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class TransformStatisticService {

    private final CasinoRepository casinoRepository;

    public TransformStatisticResDTO calculateMonthlyStatistics(LocalDate month, PrincipalDetails principalDetails) {
        LocalDate startOfMonth = month.withDayOfMonth(1);
        LocalDate today = LocalDate.now();
        LocalDate endOfMonth = month.withDayOfMonth(month.lengthOfMonth());

        LocalDate endOfPeriod = today.isBefore(endOfMonth) ? today : endOfMonth;
        LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endDateTime = endOfPeriod.atTime(23, 59, 59);

        List<CasinoTransaction> transactions = casinoRepository.findByProcessedAtBetween(startDateTime, endDateTime);

        TransformStatisticResDTO result = new TransformStatisticResDTO();
        initializeMapsForAllDays(result, endOfPeriod.getDayOfMonth());

        transactions.forEach(tx -> {
            int day = tx.getProcessedAt().getDayOfMonth();
            if (tx.getStatus() == TransactionEnum.APPROVAL) {
                long usedCasinoBalance = tx.getUsedCasinoBalance();
                long usedSportsBalance = tx.getUsedSportsBalance();

                if (usedSportsBalance != 0) {
                    result.getToCasino().merge(day, usedSportsBalance, Long::sum);
                    result.getToCasinoCount().merge(day, 1, Integer::sum);
                }

                if (usedCasinoBalance != 0) {
                    result.getToSports().merge(day, usedCasinoBalance, Long::sum);
                    result.getToSportsCount().merge(day, 1, Integer::sum);
                }
            }
        });

        performCalculationsAndStoreResults(result, endOfPeriod.getDayOfMonth());
        calculateProfitRates(result, endOfPeriod.getDayOfMonth());

        return result;
    }

    private void initializeMapsForAllDays(TransformStatisticResDTO result, int daysInPeriod) {
        IntStream.rangeClosed(1, daysInPeriod).forEach(day -> {
            result.getToCasino().putIfAbsent(day, 0L);
            result.getToCasinoCount().putIfAbsent(day, 0);
            result.getToSports().putIfAbsent(day, 0L);
            result.getToSportsCount().putIfAbsent(day, 0);
        });
    }

    private void performCalculationsAndStoreResults(TransformStatisticResDTO result, int daysInPeriod) {
        long totalCasino = result.getToCasino().values().stream().mapToLong(Long::longValue).sum();
        int totalCasinoCount = result.getToCasinoCount().values().stream().mapToInt(Integer::intValue).sum();
        double averageCasino = totalCasinoCount > 0 ? truncateDouble((double) totalCasino / totalCasinoCount, 2) : 0.0;
        result.setTotalToCasino(totalCasino);
        result.setTotalToCasinoCount(totalCasinoCount);
        result.setTotalToCasinoAverage(formatDouble(averageCasino));

        long totalSports = result.getToSports().values().stream().mapToLong(Long::longValue).sum();
        int totalSportsCount = result.getToSportsCount().values().stream().mapToInt(Integer::intValue).sum();
        double averageSports = totalSportsCount > 0 ? truncateDouble((double) totalSports / totalSportsCount, 2) : 0.0;
        result.setTotalToSports(totalSports);
        result.setTotalToSportsCount(totalSportsCount);
        result.setTotalToSportsAverage(formatDouble(averageSports));

        double averageToCasino = daysInPeriod > 0 ? truncateDouble((double) totalCasino / daysInPeriod, 2) : 0.0;
        double averageToSports = daysInPeriod > 0 ? truncateDouble((double) totalSports / daysInPeriod, 2) : 0.0;
        double averageToCasinoCount = daysInPeriod > 0 ? truncateDouble((double) totalCasinoCount / daysInPeriod, 2) : 0.0;
        double averageToSportsCount = daysInPeriod > 0 ? truncateDouble((double) totalSportsCount / daysInPeriod, 2) : 0.0;
        result.setAverageToCasino(formatDouble(averageToCasino));
        result.setAverageToCasinoCount(formatDouble(averageToCasinoCount));
        result.setAverageToSports(formatDouble(averageToSports));
        result.setAverageToSportsCount(formatDouble(averageToSportsCount));

        long casinoMinusSports = totalCasino - totalSports;
        result.setTotalToCasinoMinusToSports(casinoMinusSports);

        calculateAveragesAndDifferences(result);
    }

    private void calculateAveragesAndDifferences(TransformStatisticResDTO result) {
        result.getToCasino().forEach((day, total) -> {
            int count = result.getToCasinoCount().get(day);
            double average = count > 0 ? truncateDouble((double) total / count, 2) : 0.0;
            result.getToCasinoAverage().put(day, formatDouble(average));

            Long sportsTotal = result.getToSports().getOrDefault(day, 0L);
            result.getToCasinoMinusToSports().put(day, total - sportsTotal);
        });

        result.getToSports().forEach((day, total) -> {
            int count = result.getToSportsCount().get(day);
            double average = count > 0 ? truncateDouble((double) total / count, 2) : 0.0;
            result.getToSportsAverage().put(day, formatDouble(average));
        });
    }

    private void calculateProfitRates(TransformStatisticResDTO result, int daysInPeriod) {
        List<Double> dailyRates = new ArrayList<>();
        result.getToCasino().forEach((day, toCasinoAmount) -> {
            long casinoAmount = toCasinoAmount;
            long sportsAmount = result.getToSports().getOrDefault(day, 0L);
            long minusAmount = casinoAmount - sportsAmount; // toCasino - toSports

            double rate;
            if (casinoAmount == 0) {
                if (sportsAmount == 0) {
                    rate = 0.0;  // 둘 다 0이면 0%
                } else {
                    rate = -100.0;  // toCasino가 0이고 toSports가 양수인 경우
                }
            } else {
                rate = (double) minusAmount / casinoAmount * 100; // 일반적인 경우 수익률 계산
            }

            result.getDailyProfitRate().put(day, String.format("%,.2f%%", rate));
            dailyRates.add(rate);
        });

        double monthlyRate = dailyRates.isEmpty() ? 0.0 : dailyRates.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        result.setMonthlyAverageProfitRate(String.format("%,.2f%%", monthlyRate));
    }


    private String formatDouble(double value) {
        return String.format("%,.2f", value);
    }

    private double truncateDouble(double value, int decimalPlaces) {
        if (decimalPlaces < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(decimalPlaces, RoundingMode.DOWN);
        return bd.doubleValue();
    }
}