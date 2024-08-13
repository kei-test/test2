package GInternational.server.api.service;

import GInternational.server.api.dto.AdjustmentDTO;
import GInternational.server.api.entity.ExchangeTransaction;
import GInternational.server.api.entity.RechargeTransaction;
import GInternational.server.api.repository.ExchangeRepository;
import GInternational.server.api.repository.RechargeTransactionRepository;
import GInternational.server.api.vo.TransactionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AdjustmentService {

    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final ExchangeRepository exchangeRepository;

    public AdjustmentDTO calculateAdjustments(Long userId) {
        LocalDate now = LocalDate.now();
        AdjustmentDTO dto = new AdjustmentDTO();

        long totalRechargeSum = 0;
        long totalExchangeSum = 0;
        long totalNetSum = 0;

        // 최근 5개월 동안의 데이터 계산
        for (int i = 0; i < 5; i++) {
            LocalDate startOfMonth = now.minusMonths(i).withDayOfMonth(1);
            LocalDate endOfMonth = i == 0 ? now : startOfMonth.plusMonths(1).minusDays(1);

            LocalDateTime startDateTime = startOfMonth.atStartOfDay();
            LocalDateTime endDateTime = endOfMonth.atTime(23, 59, 59);

            List<RechargeTransaction> rechargeTransactions = rechargeTransactionRepository.findByUserIdAndStatusAndProcessedAtBetween(
                    userId, TransactionEnum.APPROVAL, startDateTime, endDateTime);
            List<ExchangeTransaction> exchangeTransactions = exchangeRepository.findByUserIdAndStatusAndProcessedAtBetween(
                    userId, TransactionEnum.APPROVAL, startDateTime, endDateTime);

            long monthRecharge = rechargeTransactions.stream().mapToLong(RechargeTransaction::getRechargeAmount).sum();
            long monthExchange = exchangeTransactions.stream().mapToLong(ExchangeTransaction::getExchangeAmount).sum();
            long monthNet = monthRecharge - monthExchange;

            // 각 월별 금액 설정
            switch (i) {
                case 0:
                    dto.setMonth1RechargeAmount(monthRecharge);
                    dto.setMonth1ExchangeAmount(monthExchange);
                    dto.setMonth1NetAmount(monthNet);
                    break;
                case 1:
                    dto.setMonth2RechargeAmount(monthRecharge);
                    dto.setMonth2ExchangeAmount(monthExchange);
                    dto.setMonth2NetAmount(monthNet);
                    break;
                case 2:
                    dto.setMonth3RechargeAmount(monthRecharge);
                    dto.setMonth3ExchangeAmount(monthExchange);
                    dto.setMonth3NetAmount(monthNet);
                    break;
                case 3:
                    dto.setMonth4RechargeAmount(monthRecharge);
                    dto.setMonth4ExchangeAmount(monthExchange);
                    dto.setMonth4NetAmount(monthNet);
                    break;
                case 4:
                    dto.setMonth5RechargeAmount(monthRecharge);
                    dto.setMonth5ExchangeAmount(monthExchange);
                    dto.setMonth5NetAmount(monthNet);
                    break;
            }

            // 총합 계산
            totalRechargeSum += monthRecharge;
            totalExchangeSum += monthExchange;
            totalNetSum += monthNet;
        }

        long totalWeeklyRechargeSum = 0;
        long totalWeeklyExchangeSum = 0;
        long totalWeeklyNetSum = 0;

        for (int i = 0; i < 5; i++) {
            // 이번 주 월요일 계산
            LocalDate monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(i);
            LocalDate sunday = monday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            if (i == 0) {
                sunday = now; // 첫 주는 오늘까지
            }

            LocalDateTime startOfWeek = monday.atStartOfDay();
            LocalDateTime endOfWeek = sunday.atTime(23, 59, 59);

            List<RechargeTransaction> weeklyRechargeTransactions = rechargeTransactionRepository.findByUserIdAndStatusAndProcessedAtBetween(
                    userId, TransactionEnum.APPROVAL, startOfWeek, endOfWeek);
            List<ExchangeTransaction> weeklyExchangeTransactions = exchangeRepository.findByUserIdAndStatusAndProcessedAtBetween(
                    userId, TransactionEnum.APPROVAL, startOfWeek, endOfWeek);

            long weekRecharge = weeklyRechargeTransactions.stream().mapToLong(RechargeTransaction::getRechargeAmount).sum();
            long weekExchange = weeklyExchangeTransactions.stream().mapToLong(ExchangeTransaction::getExchangeAmount).sum();
            long weekNet = weekRecharge - weekExchange;

            // 각 주별 금액 설정
            switch (i) {
                case 0:
                    dto.setWeek1RechargeAmount(weekRecharge);
                    dto.setWeek1ExchangeAmount(weekExchange);
                    dto.setWeek1NetAmount(weekNet);
                    break;
                case 1:
                    dto.setWeek2RechargeAmount(weekRecharge);
                    dto.setWeek2ExchangeAmount(weekExchange);
                    dto.setWeek2NetAmount(weekNet);
                    break;
                case 2:
                    dto.setWeek3RechargeAmount(weekRecharge);
                    dto.setWeek3ExchangeAmount(weekExchange);
                    dto.setWeek3NetAmount(weekNet);
                    break;
                case 3:
                    dto.setWeek4RechargeAmount(weekRecharge);
                    dto.setWeek4ExchangeAmount(weekExchange);
                    dto.setWeek4NetAmount(weekNet);
                    break;
                case 4:
                    dto.setWeek5RechargeAmount(weekRecharge);
                    dto.setWeek5ExchangeAmount(weekExchange);
                    dto.setWeek5NetAmount(weekNet);
                    break;
            }

            // 총합 계산
            totalWeeklyRechargeSum += weekRecharge;
            totalWeeklyExchangeSum += weekExchange;
            totalWeeklyNetSum += weekNet;
        }

        // 5개월간의 총합 설정
        dto.setTotal5MonthsRecharge(totalRechargeSum);
        dto.setTotal5MonthsExchange(totalExchangeSum);
        dto.setTotal5MonthsNet(totalNetSum);
        dto.setTotal5WeeksRecharge(totalWeeklyRechargeSum);
        dto.setTotal5WeeksExchange(totalWeeklyExchangeSum);
        dto.setTotal5WeeksNet(totalWeeklyNetSum);

        return dto;
    }
}
