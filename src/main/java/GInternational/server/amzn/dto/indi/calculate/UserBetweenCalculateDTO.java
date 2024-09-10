package GInternational.server.amzn.dto.indi.calculate;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserBetweenCalculateDTO {

    private Long userId;
    private long totalBetAmount;
    private long totalWinningAmount;
    private long totalBetSettlement;
    private long totalRechargeAmount;
    private long totalExchangeAmount;
    private long totalWalletSettlement;
    private long casinoMoney;


    @QueryProjection
    public UserBetweenCalculateDTO(Long userId,long totalRechargeAmount, long totalExchangeAmount) {
        this.userId = userId;
        this.totalRechargeAmount = totalRechargeAmount;
        this.totalExchangeAmount = totalExchangeAmount;
    }



    @QueryProjection
    public UserBetweenCalculateDTO(Long userId, long totalRechargeAmount, long totalExchangeAmount, long totalWalletSettlement, long casinoMoney) {
        this.userId = userId;
        this.casinoMoney = casinoMoney;
    }


    @QueryProjection
    public UserBetweenCalculateDTO(Long userId, long totalBetAmount, long totalWinningAmount, long totalBetSettlement) {
        this.userId = userId;
        this.totalBetAmount = totalBetAmount;
        this.totalWinningAmount = totalWinningAmount;
        this.totalBetSettlement = totalBetSettlement;
    }
}
