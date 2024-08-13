package GInternational.server.l_sport.info.dto.pre;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Setter
@Getter
public class MarketDTO {

    private String marketId;
    private String marketName;
//    private String marketMainLine;
    private List<OddDTO> bets;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketDTO marketDTO = (MarketDTO) o;
        return Objects.equals(marketId, marketDTO.marketId) &&
                Objects.equals(marketName, marketDTO.marketName) &&
//                Objects.equals(marketMainLine, marketDTO.marketMainLine) &&
                Objects.equals(bets, marketDTO.bets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                marketId,
                marketName,
//              marketMainLine,
                bets
        );
    }
}
