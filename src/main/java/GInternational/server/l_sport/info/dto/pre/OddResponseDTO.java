package GInternational.server.l_sport.info.dto.pre;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OddResponseDTO {


    private String idx;
    private String matchId;
    private String marketName;
    private String betName;
    private String betStatus;
    private String price;
    private String baseLine;
    private String lastUpdate;


    @QueryProjection
    public OddResponseDTO(String idx, String matchId, String marketName, String betName, String betStatus, String price, String baseLine, String lastUpdate) {
        this.idx = idx;
        this.matchId = matchId;
        this.marketName = marketName;
        this.betName = betName;
        this.betStatus = betStatus;
        this.price = price;
        this.baseLine = baseLine;
        this.lastUpdate = lastUpdate;
    }
}
