package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdjustmentDTO {
    // 최근 5개월의 총 충전, 총 환전, 총 정산 금액
    private Long month1RechargeAmount;
    private Long month1ExchangeAmount;
    private Long month1NetAmount;

    private Long month2RechargeAmount;
    private Long month2ExchangeAmount;
    private Long month2NetAmount;

    private Long month3RechargeAmount;
    private Long month3ExchangeAmount;
    private Long month3NetAmount;

    private Long month4RechargeAmount;
    private Long month4ExchangeAmount;
    private Long month4NetAmount;

    private Long month5RechargeAmount;
    private Long month5ExchangeAmount;
    private Long month5NetAmount;

    // 5개월의 총 충전, 총 환전, 총 정산 합
    private Long total5MonthsRecharge;
    private Long total5MonthsExchange;
    private Long total5MonthsNet;


    // 최근 5주의 총 충전, 총 환전, 총 정산 금액
    private Long week1RechargeAmount;
    private Long week1ExchangeAmount;
    private Long week1NetAmount;

    private Long week2RechargeAmount;
    private Long week2ExchangeAmount;
    private Long week2NetAmount;

    private Long week3RechargeAmount;
    private Long week3ExchangeAmount;
    private Long week3NetAmount;

    private Long week4RechargeAmount;
    private Long week4ExchangeAmount;
    private Long week4NetAmount;

    private Long week5RechargeAmount;
    private Long week5ExchangeAmount;
    private Long week5NetAmount;

    // 5주의 총 충전, 총 환전, 총 정산 합
    private Long total5WeeksRecharge;
    private Long total5WeeksExchange;
    private Long total5WeeksNet;
}
