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
public class AmznKplayResDTO {

    private String referredBy;
    private int prdId;
    private int debitAmount;
    private long creditAmount;
    private long partnerRollingAmount;
    private long totalSettlement;


    @QueryProjection
    public AmznKplayResDTO(String referredBy, int prdId, int debitAmount) {
        this.referredBy = referredBy;
        this.prdId = prdId;
        this.debitAmount = debitAmount;
    }


}
