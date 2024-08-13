package GInternational.server.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyREStatisticsDTO {

    private int firstRechargeCount; // 첫 충전 건수 (24시간 기준 첫충전 건수)
    private long totalRechargeAmount; // 충전 금액
    private int rechargeCount; // 충전건수 (첫충전 제외)
    private String averageRechargeAmount; // 평균 충전 금액
    private long totalExchangeAmount; // 환전 금액
    private int exchangeCount; // 환전 건수
    private long netRechargeAmount; // 충전 금액 - 환전 금액
    private double revenueRate; // 수익률
    private long point; // 포인트 지급
    private long rollingPoint; // 카지노 이벤트 지급
}
