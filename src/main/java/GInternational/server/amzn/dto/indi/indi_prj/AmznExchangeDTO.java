package GInternational.server.amzn.dto.indi.indi_prj;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznExchangeDTO {


    private Long id;
    private long exchangeAmount;


    @QueryProjection
    public AmznExchangeDTO(Long id, long exchangeAmount) {
        this.id = id;
        this.exchangeAmount = exchangeAmount;
    }
}
