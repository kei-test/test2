package GInternational.server.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@NoArgsConstructor
@Getter
@Setter
public class UserCalculateDTO {

    private int lv;
//    private long rechargeAmount;      // 입금액
//    private long exchangeAmount;      // 출금액
//    private long difference;          // 차액
    private long depositTotal;      // 입금액
    private long withdrawTotal;      // 출금액
    private long totalSettlement;          // 차액
    private long sportsBalance;       // 보유머니
    private long betAmount;           // 베팅액
    private long winningAmount;       // 적중액
    private Double winningRate;       // 적중률
    private long loseAmount;          // 낙첨액
    private long betDifference;       // 베팅차액 (베팅액 - 적중액)




    @QueryProjection
    public UserCalculateDTO(int lv, long depositTotal, long withdrawTotal, long totalSettlement, long sportsBalance) {
        this.lv = lv;
        this.depositTotal = depositTotal;
        this.withdrawTotal = withdrawTotal;
        this.totalSettlement = totalSettlement;
        this.sportsBalance = sportsBalance;
    }
}
