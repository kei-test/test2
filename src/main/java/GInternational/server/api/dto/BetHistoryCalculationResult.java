package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BetHistoryCalculationResult {
    private BigDecimal totalBetAmount; // 전체 베팅금액
    private BigDecimal validBetAmount; // 유효 베팅금액
    private BigDecimal validWinningAmount; // 유효 당첨금액
    private BigDecimal totalBetReward; // 당첨금액
    private BigDecimal totalProfitAmount; // 손익금액
    private List<BetHistoryGroupedDTO> groupedBetHistories; // 그룹화된 베팅 내역들
}
