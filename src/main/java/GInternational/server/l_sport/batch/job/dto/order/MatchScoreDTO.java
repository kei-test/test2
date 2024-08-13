package GInternational.server.l_sport.batch.job.dto.order;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class MatchScoreDTO {

    private String matchId;
    private String homeScore;
    private String awayScore;
    private String status;

    @QueryProjection
    public MatchScoreDTO(String matchId, String homeScore, String awayScore, String status) {
        this.matchId = matchId;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
    }
}
