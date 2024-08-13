package GInternational.server.api.dto;

import GInternational.server.api.entity.Wallet;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WalletDTO {

    private long sportsBalance;
    private long point;
    private long chargedCount;

    @QueryProjection
    public WalletDTO(Wallet wallet) {
        this.sportsBalance = wallet.getSportsBalance();
        this.point = wallet.getPoint();
        this.chargedCount = wallet.getChargedCount();
    }
}
