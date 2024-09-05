package GInternational.server.amzn.dto.indi.business;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznPartnerRollingInfo {

    private double casinoRolling;
    private double slotRolling;

    @QueryProjection
    public AmznPartnerRollingInfo(double casinoRolling, double slotRolling) {
        this.casinoRolling = casinoRolling;
        this.slotRolling = slotRolling;
    }
}
