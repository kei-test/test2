package GInternational.server.l_sport.info.dto.results;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@Setter
@Getter
public class GameResultResponseDTO {
    private String leagueName;
    private String locationName;
    private String sportsName;
    private String startDate;
    private String status;
    private String homeName;
    private String homeScore;
    private String awayName;
    private String awayScore;


    @QueryProjection
    public GameResultResponseDTO(String leagueName, String locationName, String sportsName, String startDate, String status, String homeName, String homeScore, String awayName, String awayScore) {
        this.leagueName = leagueName;
        this.locationName = locationName;
        this.sportsName = sportsName;
        this.startDate = startDate;
        this.status = status;
        this.homeName = homeName;
        this.homeScore = homeScore;
        this.awayName = awayName;
        this.awayScore = awayScore;
    }
}
