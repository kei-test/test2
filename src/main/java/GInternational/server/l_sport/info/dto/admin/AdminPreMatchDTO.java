package GInternational.server.l_sport.info.dto.admin;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AdminPreMatchDTO {

    private String matchId;
    private String startDate;
    private String sportsName;
    private String locationName;
    private String leagueName;
    private String homeName;
    private String homeScore;
    private String awayName;
    private String awayScore;
    private String status;
    private String orderingStatus;
    private String betHistory;
    private String marketConfig;


    @QueryProjection
    public AdminPreMatchDTO(String matchId, String startDate, String sportsName, String locationName, String leagueName, String homeName, String homeScore, String awayName, String awayScore, String status) {
        this.matchId = matchId;
        this.startDate = startDate;
        this.sportsName = sportsName;
        this.locationName = locationName;
        this.leagueName = leagueName;
        this.homeName = homeName;
        this.homeScore = homeScore;
        this.awayName = awayName;
        this.awayScore = awayScore;
        this.status = status;
    }
}
