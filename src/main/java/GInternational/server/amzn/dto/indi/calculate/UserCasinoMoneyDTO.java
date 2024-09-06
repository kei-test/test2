package GInternational.server.amzn.dto.indi.calculate;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserCasinoMoneyDTO {

    private Long userId;
    private int aas;
    private long casinoMoney;

    @QueryProjection
    public UserCasinoMoneyDTO(Long userId, int aas, long casinoMoney) {
        this.userId = userId;
        this.aas = aas;
        this.casinoMoney = casinoMoney;
    }
}
