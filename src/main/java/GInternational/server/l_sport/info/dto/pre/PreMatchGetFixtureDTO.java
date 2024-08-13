package GInternational.server.l_sport.info.dto.pre;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class PreMatchGetFixtureDTO extends PreMatchGetFixtureResponseDTO {

    private String matchId;
    private String leagueName;
    private String locationName;
    private String sportsName;
    private String status;
    private String homeName;
    private String awayName;
    private String leagueId;
    private String startDate;
    private String marketId;
    private String marketName;
    private String idx;  //배당 id
    private String betName;
    private String line;
    private String baseLine;
    private String price;
    private String lastUpdate;
    private String betStatus;
    private String isPreMatch;
    private String isLive;





    @QueryProjection
    public PreMatchGetFixtureDTO(String matchId, String leagueName, String locationName, String sportsName, String status, String homeName, String awayName, String leagueId, String startDate, String marketId, String marketName, String idx, String betName, String line, String baseLine, String price, String lastUpdate, String betStatus, String isPreMatch, String isLive) {
        this.matchId = matchId;
        this.leagueName = leagueName;
        this.locationName = locationName;
        this.sportsName = sportsName;
        this.status = status;
        this.homeName = homeName;
        this.awayName = awayName;
        this.leagueId = leagueId;
        this.startDate = startDate;
        this.marketId = marketId;
        this.marketName = marketName;
        this.idx = idx;
        this.betName = betName;
        this.line = line;
        this.baseLine = baseLine;
        this.price = price;
        this.lastUpdate = lastUpdate;
        this.betStatus = betStatus;
        this.isPreMatch = isPreMatch;
        this.isLive = isLive;
    }
}

