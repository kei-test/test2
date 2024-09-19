package GInternational.server.amzn.dto.indi.business;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznTotalRollingAmountDTO {

    private int rawBetAmount;
    private long totalBetAmount;
    private long totalRollingAmount;


    @QueryProjection
    public AmznTotalRollingAmountDTO(int rawBetAmount, long totalRollingAmount) {
        this.rawBetAmount = rawBetAmount;
        this.totalRollingAmount = totalRollingAmount;
    }
}
