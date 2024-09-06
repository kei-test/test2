package GInternational.server.amzn.dto.indi.indi_prj;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznDebit {

    private int prdId;
    private int amount;
    private int isCancel;

    @QueryProjection
    public AmznDebit(int prdId, int amount, int isCancel) {
        this.prdId = prdId;
        this.amount = amount;
        this.isCancel = isCancel;
    }

    @QueryProjection
    public AmznDebit(int amount) {
        this.amount = amount;
    }
}
