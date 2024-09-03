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
public class AmznSportResDTO {

    private String referredBy;
    private String betAmount;
    private String betReward;
    private long cvtBetAmount;
    private long cvtBetReward;
    private long partnerRollingAmount;
    private long totalSettlement;


    @QueryProjection
    public AmznSportResDTO(String referredBy, String betAmount, String betReward) {
        this.referredBy = referredBy;
        this.betAmount = betAmount;
        this.betReward = betReward;
    }


    public AmznSportResDTO(String referredBy, long cvtBetAmount, long cvtBetReward, long partnerRollingAmount, long totalSettlement) {
        this.referredBy = referredBy;
        this.cvtBetAmount = cvtBetAmount;
        this.cvtBetReward = cvtBetReward;
        this.partnerRollingAmount = partnerRollingAmount;
        this.totalSettlement = totalSettlement;
    }
}
