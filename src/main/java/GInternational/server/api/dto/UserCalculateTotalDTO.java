package GInternational.server.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserCalculateTotalDTO {

    private long totalRechargeAmount;   // 총 입금액
    private long totalExchangeAmount;   // 총 출금액
    private long totalDifference;       // 총 차액
    private long totalSportsBalance;    // 총 보유머니
    private Double averageBetAmount;    // 평균 베팅액
    private Double averageWinningAmount;// 평균 적중액
    private Double averageWinningRate;  // 평균 적중률 (1~10 레벨까지의 평균 적중률)
    private long totalLoseAmount;       // 총 낙첨액
    private long totalBetDifference;    // 총 베팅차액
}
