package GInternational.server.amzn.dto.indi.indi_response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AmznUserResDTO {


    private String referredBy;
    private long sportsBalance;
    private long point;
    private long rechargeAmount;
    private long exchangeAmount;
    private long totalSettlement;


    @QueryProjection
    public AmznUserResDTO(String referredBy, long sportsBalance, long point) {
        this.referredBy = referredBy;
        this.sportsBalance = sportsBalance;
        this.point = point;
    }



}
