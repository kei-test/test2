package GInternational.server.l_sport.info.dto.pre;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@Setter
@Getter
public class OddDTO {

    private String idx;
    private String betName;
    private String line;
    private String baseLine;
    private String price;
//    private String startPrice;
    private String lastUpdate;
    private String betStatus;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OddDTO oddDTO = (OddDTO) o;
        return Objects.equals(idx, oddDTO.idx) &&
                Objects.equals(betName, oddDTO.betName) &&
                Objects.equals(lastUpdate, oddDTO.lastUpdate) &&
                Objects.equals(line, oddDTO.line) &&
                Objects.equals(baseLine, oddDTO.baseLine) &&
                Objects.equals(price, oddDTO.price) &&
                Objects.equals(betStatus, oddDTO.betStatus);

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
        );
    }
}
