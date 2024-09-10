package GInternational.server.amzn.dto.indi.indi_prj;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznRechargeDTO {

    private Long id;
    private long rechargeAmount;

    @QueryProjection
    public AmznRechargeDTO(Long id, long rechargeAmount) {
        this.id = id;
        this.rechargeAmount = rechargeAmount;
    }

    @QueryProjection
    public AmznRechargeDTO(long rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }
}
