package GInternational.server.l_sport.info.dto.inplay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@Setter
@Getter
public class OddLiveDTO {
    private String idx;
    private String betName;
    private String line;
    private String baseLine;
    private String price;
    private String lastUpdate;
    private String betStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OddLiveDTO oddLiveDTO = (OddLiveDTO) o;
        return Objects.equals(idx, oddLiveDTO.idx) &&
                Objects.equals(betName, oddLiveDTO.betName) &&
                Objects.equals(lastUpdate, oddLiveDTO.lastUpdate) &&
                Objects.equals(line, oddLiveDTO.line) &&
                Objects.equals(baseLine, oddLiveDTO.baseLine) &&
                Objects.equals(price, oddLiveDTO.price) &&
                Objects.equals(betStatus, oddLiveDTO.betStatus);
//                Objects.equals(startPrice, oddLiveDTO.startPrice);

    }

    @Override
    public int hashCode() {
        return Objects.hash(
                idx,
                betName,
                lastUpdate,
                line,
                baseLine,
                price,
                betStatus
//                startPrice
        );
    }
}
