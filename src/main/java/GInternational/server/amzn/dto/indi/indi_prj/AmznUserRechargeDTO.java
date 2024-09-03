package GInternational.server.amzn.dto.indi.indi_prj;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznUserRechargeDTO {

    private Long id;
    private String referredBy;
    private long rechargeAmount;


    @QueryProjection
    public AmznUserRechargeDTO(Long id, String referredBy, long rechargeAmount) {
        this.id = id;
        this.referredBy = referredBy;
        this.rechargeAmount = rechargeAmount;
    }

    @QueryProjection
    public AmznUserRechargeDTO(String referredBy, long rechargeAmount) {
        this.referredBy = referredBy;
        this.rechargeAmount = rechargeAmount;
    }
}
