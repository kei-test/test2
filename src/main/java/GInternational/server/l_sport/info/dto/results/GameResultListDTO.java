package GInternational.server.l_sport.info.dto.results;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@Setter
@Getter
public class GameResultListDTO {

    private String matchId;
    private String leagueName;
    private String locationName;
    private String sportsName;
    private String status;
    private String homeName;
    private String homeScore;
    private String awayName;
    private String awayScore;
    private String period1Home;
    private String period1Away;
    private String period2Home;
    private String period2Away;
    private String marketId;
    private String marketName;
    private String idx;
    private String betName;
    private String baseLine;
    private String price;
    private String settlement;
    private String startDate;


    @QueryProjection
    public GameResultListDTO(String matchId, String leagueName, String locationName, String sportsName, String status, String homeName, String homeScore, String awayName, String awayScore,String period1Home, String period1Away, String period2Home, String period2Away, String marketId, String marketName, String idx, String betName, String baseLine, String price, String settlement, String startDate) {
        this.matchId = matchId;
        this.leagueName = leagueName;
        this.locationName = locationName;
        this.sportsName = sportsName;
        this.status = status;
        this.homeName = homeName;
        this.homeScore = homeScore;
        this.awayName = awayName;
        this.awayScore = awayScore;
        this.period1Home = period1Home;
        this.period1Away = period1Away;
        this.period2Home = period2Home;
        this.period2Away = period2Away;
        this.marketId = marketId;
        this.marketName = marketName;
        this.idx = idx;
        this.betName = betName;
        this.baseLine = baseLine;
        this.price = price;
        this.settlement = settlement;
        this.startDate = startDate;

    }

}
