package GInternational.server.amzn.dto.indi.business;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznPartnerRollingInfo {

    private long casinoRolling;
    private long slotRolling;

    @QueryProjection
    public AmznPartnerRollingInfo(long casinoRolling, long slotRolling) {
        this.casinoRolling = casinoRolling;
        this.slotRolling = slotRolling;
    }
}
