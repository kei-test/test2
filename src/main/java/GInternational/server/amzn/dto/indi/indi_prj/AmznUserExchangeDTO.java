package GInternational.server.amzn.dto.indi.indi_prj;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznUserExchangeDTO {

    private Long id;
    private String referredBy;
    private long exchangeAmount;


    @QueryProjection
    public AmznUserExchangeDTO(Long id, String referredBy, long exchangeAmount) {
        this.id = id;
        this.referredBy = referredBy;
        this.exchangeAmount = exchangeAmount;
    }
}
