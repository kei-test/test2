package GInternational.server.amzn.dto.indi.indi_prj;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznCredit {


    private String referredBy;
    private int prdId;
    private int amount;
    private int isCancel;

    @QueryProjection
    public AmznCredit(String referredBy, int prdId, int amount, int isCancel) {
        this.referredBy = referredBy;
        this.prdId = prdId;
        this.amount = amount;
        this.isCancel = isCancel;
    }
}
