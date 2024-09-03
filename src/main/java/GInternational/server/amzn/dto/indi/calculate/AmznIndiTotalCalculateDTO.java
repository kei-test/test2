package GInternational.server.amzn.dto.indi.calculate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AmznIndiTotalCalculateDTO {

    //총 롤링
    private long totalRolling;

    //유저
    private long totalUserSportsBalance;
    private long totalUserPoint;
    private long totalUserRecharge;
    private long totalUserExchange;
    private long totalUserSettlement;

    //파트너
    private long totalPartnerAmazonMoney;
    private long totalPartnerAmazonMileage;
    private long totalPartnerAmazonRecharge;
    private long totalPartnerAmazonExchange;
    private long totalPartnerSettlement;

    //슬롯
    private long totalSlotBetAmount;
    private long totalSlotWinningAmount;
    private long totalSlotSettlement;
    private long totalSlotRollingAmount;

    //카지노
    private long totalCasinoBetAmount;
    private long totalCasinoWinningAmount;
    private long totalCasinoSettlement;
    private long totalCasinoRollingAmount;

    //스포츠
    private long totalSportBetAmount;
    private long totalSportWinningAmount;
    private long totalSportSettlement;
    private long totalSportRollingAmount;

    //아케이드
    private long totalArcadeBetAmount;
    private long totalArcadeWinningAmount;
    private long totalArcadeSettlement;
    private long totalArcadeRollingAmount;
}
